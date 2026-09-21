package com.broco.tdc_food_chart.ui.detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broco.tdc_food_chart.data.local.FoodPhotoEntity
import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import com.broco.tdc_food_chart.data.model.Food
import com.broco.tdc_food_chart.data.repository.FoodRepository
import com.broco.tdc_food_chart.data.repository.PhotoRepository
import com.broco.tdc_food_chart.data.repository.UserRecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

/** フード詳細の状態。 */
data class FoodDetailUiState(
    val loading: Boolean = true,
    /** マスターに存在しない ID を開いた場合 true。 */
    val notFound: Boolean = false,
    val food: Food? = null,
    val record: FoodUserRecordEntity? = null,
    val photos: List<FoodPhotoEntity> = emptyList(),
    /** メモ入力欄の現在値（保存はデバウンス）。 */
    val memo: String = "",
) {
    val favorite: Boolean get() = record?.favorite ?: false
    val eaten: Boolean get() = record?.eaten ?: false
    val eatenDate: LocalDate? get() = record?.eatenDate
}

/** 写真追加後、撮影日を食べた日にするか尋ねる提案。 */
data class CapturedDateSuggestion(val date: LocalDate, val photoCount: Int)

class FoodDetailViewModel(
    private val foodId: String,
    private val foodRepository: FoodRepository,
    private val userRecordRepository: UserRecordRepository,
    private val photoRepository: PhotoRepository,
    /** 画面を離れたあとにメモを書き切るための、ViewModel より長寿命なスコープ。 */
    private val appScope: CoroutineScope,
) : ViewModel() {

    private val memoDraft = MutableStateFlow<String?>(null)
    private var lastSavedMemo: String? = null

    private val _suggestion = MutableStateFlow<CapturedDateSuggestion?>(null)
    val suggestion: StateFlow<CapturedDateSuggestion?> = _suggestion.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val food = flow { emit(foodRepository.getById(foodId)) }

    val uiState: StateFlow<FoodDetailUiState> = combine(
        food,
        userRecordRepository.observe(foodId),
        photoRepository.observeForFood(foodId),
        memoDraft,
    ) { f, record, photos, draft ->
        FoodDetailUiState(
            loading = false,
            notFound = f == null,
            food = f,
            record = record,
            photos = photos,
            memo = draft ?: record?.memo ?: "",
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FoodDetailUiState())

    init {
        // メモは入力のたびに DB へ書かず、400ms 落ち着いたら保存する
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            memoDraft.filterNotNull().debounce(400).collect { saveMemo(it) }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch { userRecordRepository.toggleFavorite(foodId) }
    }

    /** 「食べた」の切り替え。ON で eatenDate 未設定なら今日が入る。OFF でも日付・メモ・写真は残る。 */
    fun setEaten(eaten: Boolean) {
        viewModelScope.launch { userRecordRepository.setEaten(foodId, eaten) }
    }

    fun setEatenDate(date: LocalDate) {
        viewModelScope.launch { userRecordRepository.setEatenDate(foodId, date) }
    }

    fun setMemo(text: String) {
        memoDraft.value = text
    }

    fun photoFile(photo: FoodPhotoEntity): File = photoRepository.fileOf(photo)

    /**
     * Photo Picker の結果を取り込む。
     * 取り込み後、eatenDate が未設定で撮影日が取れた写真があれば「食べた日にしますか？」を提案する。
     * 既に eatenDate がある場合は提案せず、上書きもしない。
     */
    fun addPhotos(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            _busy.value = true
            try {
                val result = photoRepository.addPhotos(foodId, uris)
                if (result.failedCount > 0) {
                    _message.value = if (result.added.isEmpty()) "写真を取り込めませんでした" else "${result.failedCount} 枚の写真を取り込めませんでした"
                }
                val record = userRecordRepository.observe(foodId).first()
                if (record?.eatenDate == null) {
                    val captured = result.added.mapNotNull { it.capturedAt?.toLocalDate() }
                    // 複数枚なら最も古い撮影日を候補にする
                    captured.minOrNull()?.let { _suggestion.value = CapturedDateSuggestion(it, captured.size) }
                }
            } finally {
                _busy.value = false
            }
        }
    }

    /** 提案に同意：eaten=true・eatenDate=撮影日（既存の日付があれば触らない）。 */
    fun acceptSuggestion() {
        val s = _suggestion.value ?: return
        _suggestion.value = null
        viewModelScope.launch { userRecordRepository.adoptCapturedDateIfUnset(foodId, s.date) }
    }

    /** 提案を断る：写真だけ残し、eaten 状態は変えない。 */
    fun dismissSuggestion() {
        _suggestion.value = null
    }

    fun deletePhoto(photo: FoodPhotoEntity) {
        viewModelScope.launch {
            val ok = photoRepository.deletePhoto(photo)
            if (!ok) _message.value = "写真を削除できませんでした"
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    private suspend fun saveMemo(text: String) {
        if (text == lastSavedMemo) return
        lastSavedMemo = text
        userRecordRepository.setMemo(foodId, text)
    }

    override fun onCleared() {
        // デバウンス待ちのメモを取りこぼさない
        val pending = memoDraft.value
        if (pending != null && pending != lastSavedMemo) {
            appScope.launch { userRecordRepository.setMemo(foodId, pending) }
        }
    }
}
