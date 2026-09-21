package com.broco.tdc_food_chart.data.repository

import com.broco.tdc_food_chart.data.local.FoodUserRecordDao
import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import java.time.LocalDate

/**
 * お気に入り・食べた・食べた日・メモ（ユーザーローカルデータ）。
 * すべて Flow で公開し、詳細・一覧・コレクション・観測記録の間で状態がずれないようにする。
 */
class UserRecordRepository(
    private val dao: FoodUserRecordDao,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    /** foodId → 記録。記録のない料理は含まれない。 */
    val records: Flow<Map<String, FoodUserRecordEntity>> =
        dao.observeAll().map { list -> list.associateBy { it.foodId } }

    fun observe(foodId: String): Flow<FoodUserRecordEntity?> = dao.observe(foodId)

    suspend fun setFavorite(foodId: String, favorite: Boolean) = update(foodId) { it.copy(favorite = favorite) }

    suspend fun toggleFavorite(foodId: String) = update(foodId) { it.copy(favorite = !it.favorite) }

    /**
     * 「食べた」を切り替える。
     * - ON にしたとき eatenDate が未設定なら今日を初期値にする
     * - OFF にしても eatenDate / memo は消さない（再度 ON にしたら以前の日付が戻る）
     */
    suspend fun setEaten(foodId: String, eaten: Boolean) = update(foodId) { cur ->
        if (eaten) cur.copy(eaten = true, eatenDate = cur.eatenDate ?: LocalDate.now(clock))
        else cur.copy(eaten = false)
    }

    suspend fun toggleEaten(foodId: String) {
        val cur = dao.get(foodId)
        setEaten(foodId, !(cur?.eaten ?: false))
    }

    /** 食べた日を任意の日付に変更する。日付を付けるなら食べた扱いにする。 */
    suspend fun setEatenDate(foodId: String, date: LocalDate) = update(foodId) {
        it.copy(eaten = true, eatenDate = date)
    }

    /**
     * 写真の撮影日を食べた日として採用する（ユーザーが確認ダイアログで同意した場合のみ呼ぶ）。
     * 既に eatenDate がある場合は上書きしない。
     */
    suspend fun adoptCapturedDateIfUnset(foodId: String, date: LocalDate) = update(foodId) { cur ->
        if (cur.eatenDate != null) cur.copy(eaten = true) else cur.copy(eaten = true, eatenDate = date)
    }

    suspend fun setMemo(foodId: String, memo: String) = update(foodId) { it.copy(memo = memo) }

    private suspend fun update(foodId: String, transform: (FoodUserRecordEntity) -> FoodUserRecordEntity) {
        val current = dao.get(foodId) ?: FoodUserRecordEntity(foodId = foodId)
        val next = transform(current)
        if (next != current) {
            dao.upsert(next.copy(updatedAt = Instant.now(clock)))
        }
    }
}
