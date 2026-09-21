package com.broco.tdc_food_chart.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.broco.tdc_food_chart.ui.components.DefinitionRow
import com.broco.tdc_food_chart.ui.components.DetailTopBar
import com.broco.tdc_food_chart.ui.components.PillButton
import com.broco.tdc_food_chart.ui.components.SectionLabel
import com.broco.tdc_food_chart.ui.components.StarField
import com.broco.tdc_food_chart.ui.components.formatYen
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d")

/**
 * フード詳細。← ／ タイトル・価格 ／ ★・食べた ／ 定義リスト ／ 閉店注記 ／ 食べた日 ／ 写真 ／ メモ ／ 店舗ページ。
 */
@Composable
fun FoodDetailScreen(
    viewModel: FoodDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val suggestion by viewModel.suggestion.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var datePickerOpen by rememberSaveable { mutableStateOf(false) }
    var viewerPhotoId by rememberSaveable { mutableStateOf<Long?>(null) }

    // Photo Picker（READ_MEDIA_IMAGES 等の権限は不要）。複数選択、上限 10 枚
    val pickPhotos = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_PICK)) { uris ->
        viewModel.addPhotos(uris)
    }
    val launchPicker = {
        pickPhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        containerColor = TdcColors.Navy,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            StarField(modifier = Modifier.fillMaxSize(), alpha = 0.10f)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                DetailTopBar(label = "フード詳細", onBack = onBack)
                val food = state.food
                when {
                    state.loading -> Box(Modifier.fillMaxSize())
                    food == null -> Text(
                        "このフードは現在のデータに含まれていません。",
                        style = TdcType.BodyMuted,
                        modifier = Modifier.padding(20.dp),
                    )
                    else -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 26.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(food.menu, style = TdcType.DetailTitle, modifier = Modifier.semantics { heading() })
                            Text(formatYen(food.price), style = TdcType.DetailPrice)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PillButton(
                                label = if (state.favorite) "★　お気に入り" else "☆　お気に入り",
                                onClick = viewModel::toggleFavorite,
                                filled = state.favorite,
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { contentDescription = if (state.favorite) "お気に入りを解除" else "お気に入りに追加" },
                            )
                            PillButton(
                                label = if (state.eaten) "✓　食べた" else "食べた",
                                onClick = { viewModel.setEaten(!state.eaten) },
                                filled = state.eaten,
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { contentDescription = if (state.eaten) "食べた記録を外す" else "食べたとして記録" },
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            DefinitionRow("店舗", food.restaurant)
                            DefinitionRow("エリア", food.area)
                            DefinitionRow("考案者", food.author)
                            DefinitionRow("コースター", food.coaster)
                            DefinitionRow(
                                "営業状況",
                                if (food.isClosed) "閉店" else "営業中",
                                valueColor = if (food.isClosed) TdcColors.ClosedText else TdcColors.TextMid,
                            )
                        }

                        if (food.isClosed) {
                            Text(
                                "この店舗は閉店しています。掲載当時の情報です。",
                                style = TdcType.Caption.copy(color = TdcColors.ClosedNote, lineHeight = 20.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, TdcColors.ClosedNoteBorder, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                            )
                        }

                        if (state.eaten || state.eatenDate != null) {
                            EatenDateRow(
                                date = state.eatenDate,
                                eaten = state.eaten,
                                onChange = { datePickerOpen = true },
                            )
                        }

                        PhotoSection(
                            photos = state.photos,
                            fileOf = viewModel::photoFile,
                            busy = busy,
                            onAdd = launchPicker,
                            onOpen = { viewerPhotoId = it.id },
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            SectionLabel("Note")
                            MemoField(value = state.memo, onValueChange = viewModel::setMemo)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            PillButton(
                                label = "店舗ページを開く　→",
                                onClick = { openExternal(context, food.url) },
                                emphasized = false,
                                minHeight = 50.dp,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text("外部ブラウザで開きます", style = TdcType.Caption, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }

                        Text(
                            "写真・メモ・食べた記録はこの端末内に保存されます。",
                            style = TdcType.Caption,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }

    if (datePickerOpen) {
        EatenDatePickerDialog(
            initial = state.eatenDate ?: LocalDate.now(),
            onDismiss = { datePickerOpen = false },
            onConfirm = { date ->
                datePickerOpen = false
                viewModel.setEatenDate(date)
            },
        )
    }

    suggestion?.let { s ->
        CapturedDateDialog(
            suggestion = s,
            onAccept = viewModel::acceptSuggestion,
            onDismiss = viewModel::dismissSuggestion,
        )
    }

    val viewerPhoto = state.photos.firstOrNull { it.id == viewerPhotoId }
    if (viewerPhoto != null) {
        PhotoViewerDialog(
            photo = viewerPhoto,
            file = viewModel.photoFile(viewerPhoto),
            onDismiss = { viewerPhotoId = null },
            onDelete = {
                viewerPhotoId = null
                viewModel.deletePhoto(viewerPhoto)
            },
        )
    }
    // 削除などで表示中の写真が無くなったらビューアを閉じる
    LaunchedEffect(state.loading, state.photos, viewerPhotoId) {
        if (!state.loading && viewerPhotoId != null && state.photos.none { it.id == viewerPhotoId }) {
            viewerPhotoId = null
        }
    }
}

@Composable
private fun EatenDateRow(date: LocalDate?, eaten: Boolean, onChange: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(TdcColors.SurfaceFaint, RoundedCornerShape(4.dp))
            .border(1.dp, TdcColors.Brass26, RoundedCornerShape(4.dp))
            .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("食べた日", style = TdcType.DefLabel)
        Text(
            date?.format(DATE_FORMAT) ?: "未設定",
            style = TdcType.SmallNumber.copy(fontSize = 15.sp, color = if (eaten) TdcColors.Champagne else TdcColors.TextWeak2),
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onChange, modifier = Modifier.heightIn(min = 44.dp)) {
            Text(if (date == null) "設定" else "変更", style = TdcType.Chip, color = TdcColors.ChampagneMuted)
        }
    }
}

@Composable
private fun MemoField(value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TdcType.Body.copy(color = TdcColors.TextStrong, lineHeight = 21.sp),
        cursorBrush = SolidColor(TdcColors.Champagne),
        minLines = 3,
        modifier = Modifier
            .fillMaxWidth()
            .background(TdcColors.SurfaceFaint2, RoundedCornerShape(4.dp))
            .border(1.dp, TdcColors.Brass26, RoundedCornerShape(4.dp))
            .padding(13.dp)
            .semantics { contentDescription = "メモ" },
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text("メモを書く（端末内に保存）", style = TdcType.Body, color = TdcColors.Placeholder)
                }
                inner()
            }
        },
    )
}

/**
 * M3 DatePicker。選択値は UTC 深夜の epoch millis なので、UTC で LocalDate に戻す（端末 TZ でずれない）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EatenDatePickerDialog(initial: LocalDate, onDismiss: () -> Unit, onConfirm: (LocalDate) -> Unit) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = pickerState.selectedDateMillis ?: return@TextButton
                    onConfirm(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                },
            ) { Text("この日にする", color = TdcColors.ChampagneStrong) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル", color = TdcColors.TextWeak) } },
        colors = DatePickerDefaults.colors(containerColor = TdcColors.Sheet),
    ) {
        DatePicker(
            state = pickerState,
            title = { Text("食べた日", style = TdcType.DefLabel, modifier = Modifier.padding(start = 24.dp, top = 16.dp)) },
            colors = DatePickerDefaults.colors(
                containerColor = TdcColors.Sheet,
                selectedDayContainerColor = TdcColors.Brass,
                selectedDayContentColor = TdcColors.Navy,
                todayDateBorderColor = TdcColors.Brass60,
                todayContentColor = TdcColors.Champagne,
            ),
        )
    }
}

@Composable
private fun CapturedDateDialog(suggestion: CapturedDateSuggestion, onAccept: () -> Unit, onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TdcColors.Sheet,
        titleContentColor = TdcColors.Heading,
        textContentColor = TdcColors.TextMid,
        title = { Text("撮影日を食べた日にしますか？", style = TdcType.DetailTitle.copy(fontSize = 16.sp, lineHeight = 26.sp)) },
        text = {
            Text(
                "写真の撮影日は ${suggestion.date.format(DATE_FORMAT)} です。" +
                    "この日を「食べた日」として記録しますか？\n設定しない場合、写真だけを保存します。",
                style = TdcType.Body,
            )
        },
        confirmButton = { TextButton(onClick = onAccept) { Text("食べた日にする", color = TdcColors.ChampagneStrong) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("設定しない", color = TdcColors.TextWeak) } },
    )
}

private fun openExternal(context: android.content.Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (e: ActivityNotFoundException) {
        // ブラウザが無い端末では何もしない（クラッシュさせない）
    }
}

private const val MAX_PICK = 10
