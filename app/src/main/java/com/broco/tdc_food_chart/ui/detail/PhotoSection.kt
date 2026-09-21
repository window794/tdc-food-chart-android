package com.broco.tdc_food_chart.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.broco.tdc_food_chart.data.local.FoodPhotoEntity
import com.broco.tdc_food_chart.ui.components.CloseIcon
import com.broco.tdc_food_chart.ui.components.PlusIcon
import com.broco.tdc_food_chart.ui.components.SectionLabel
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATETIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")

/**
 * 詳細画面の写真セクション。
 * 0 枚: 大きな空枠は出さず、「＋ 写真を追加」の控えめな導線だけ。
 * 1 枚以上: 横スクロールのサムネイル（96dp）＋ 末尾に追加タイル。
 */
@Composable
fun PhotoSection(
    photos: List<FoodPhotoEntity>,
    fileOf: (FoodPhotoEntity) -> File,
    busy: Boolean,
    onAdd: () -> Unit,
    onOpen: (FoodPhotoEntity) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            SectionLabel(if (photos.isEmpty()) "Photos" else "Photos ${photos.size}")
            if (busy) Text("取り込み中…", style = TdcType.Caption)
        }
        if (photos.isEmpty()) {
            Row(
                modifier = Modifier
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(role = Role.Button, enabled = !busy, onClick = onAdd)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PlusIcon(color = TdcColors.ChampagneMuted, size = 14.dp)
                Text("写真を追加", style = TdcType.Chip, color = TdcColors.ChampagneMuted)
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 4.dp),
            ) {
                items(photos, key = { it.id }) { photo ->
                    PhotoThumbnail(photo = photo, file = fileOf(photo), onClick = { onOpen(photo) })
                }
                item(key = "add") {
                    Box(
                        modifier = Modifier
                            .size(THUMB)
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, TdcColors.Brass34, RoundedCornerShape(4.dp))
                            .clickable(role = Role.Button, enabled = !busy, onClick = onAdd)
                            .semantics { contentDescription = "写真を追加" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PlusIcon(color = TdcColors.ChampagneMuted, size = 16.dp)
                            Text("追加", style = TdcType.Badge, color = TdcColors.ChampagneMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoThumbnail(photo: FoodPhotoEntity, file: File, onClick: () -> Unit) {
    AsyncImage(
        model = file,
        contentDescription = "写真 ${photoDescription(photo)}",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(THUMB)
            .clip(RoundedCornerShape(4.dp))
            .background(TdcColors.NavyPressed)
            .border(1.dp, TdcColors.Brass22, RoundedCornerShape(4.dp))
            .clickable(role = Role.Button, onClick = onClick),
    )
}

/**
 * 写真の拡大表示。全画面ダイアログ。× で閉じ、「削除」は確認ダイアログを挟む。
 * 編集機能（トリミング等）は付けない。
 */
@Composable
fun PhotoViewerDialog(
    photo: FoodPhotoEntity,
    file: File,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    var confirmDelete by rememberSaveable { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF2060B12))
                .safeDrawingPadding(),
        ) {
            AsyncImage(
                model = file,
                contentDescription = "写真 ${photoDescription(photo)}（拡大表示）",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp, bottom = 88.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(48.dp)
                        .semantics { contentDescription = "閉じる" },
                ) { CloseIcon(color = TdcColors.ChampagnePale, size = 16.dp) }
                TextButton(onClick = { confirmDelete = true }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("削除", style = TdcType.Chip, color = TdcColors.ClosedText)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    photo.capturedAt?.let { "撮影日時　" + it.format(DATETIME_FORMAT) } ?: "撮影日時　不明（EXIF なし）",
                    style = TdcType.Caption.copy(color = TdcColors.TextMid2, fontSize = 11.5.sp),
                )
                Text(
                    "追加日時　" + photo.createdAt.atZone(ZoneId.systemDefault()).format(DATETIME_FORMAT),
                    style = TdcType.Caption,
                )
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = TdcColors.Sheet,
            titleContentColor = TdcColors.Heading,
            textContentColor = TdcColors.TextMid,
            title = { Text("この写真を削除しますか？", style = TdcType.DetailTitle.copy(fontSize = 16.sp, lineHeight = 26.sp)) },
            text = { Text("アプリ内に保存した写真を削除します。この操作は元に戻せません。\n（元のギャラリーの写真には影響しません）", style = TdcType.Body) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) { Text("削除する", color = TdcColors.ClosedText) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("キャンセル", color = TdcColors.TextWeak) } },
        )
    }
}

private fun photoDescription(photo: FoodPhotoEntity): String =
    photo.capturedAt?.let { "撮影 " + it.format(DATETIME_FORMAT) } ?: "撮影日時不明"

private val THUMB = 96.dp
