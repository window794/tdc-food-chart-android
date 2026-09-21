package com.broco.tdc_food_chart.data.photo

/**
 * 画像形式の判定。「ファイル内容と拡張子が食い違う状態を作らない」ために、
 * ContentResolver の MIME と先頭バイトの両方から判断する。
 */
enum class ImageFormat(val mimeType: String, val extension: String) {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp"),
    GIF("image/gif", "gif"),
    HEIC("image/heic", "heic"),
    HEIF("image/heif", "heif"),
    AVIF("image/avif", "avif"),
    BMP("image/bmp", "bmp"),
    ;

    companion object {
        fun fromMimeType(mime: String?): ImageFormat? {
            val m = mime?.lowercase()?.substringBefore(';')?.trim() ?: return null
            return when (m) {
                "image/jpeg", "image/jpg", "image/pjpeg" -> JPEG
                "image/png" -> PNG
                "image/webp" -> WEBP
                "image/gif" -> GIF
                "image/heic", "image/heic-sequence" -> HEIC
                "image/heif", "image/heif-sequence" -> HEIF
                "image/avif" -> AVIF
                "image/bmp", "image/x-ms-bmp" -> BMP
                else -> null
            }
        }

        /**
         * 先頭バイト（12 バイト以上あると確実）から形式を推定する。判別できなければ null。
         */
        fun sniff(header: ByteArray): ImageFormat? {
            if (header.size < 4) return null
            fun b(i: Int) = header[i].toInt() and 0xFF
            // JPEG: FF D8 FF
            if (b(0) == 0xFF && b(1) == 0xD8 && b(2) == 0xFF) return JPEG
            // PNG: 89 50 4E 47 0D 0A 1A 0A
            if (header.size >= 8 && b(0) == 0x89 && b(1) == 0x50 && b(2) == 0x4E && b(3) == 0x47 &&
                b(4) == 0x0D && b(5) == 0x0A && b(6) == 0x1A && b(7) == 0x0A
            ) return PNG
            // GIF: "GIF8"
            if (b(0) == 'G'.code && b(1) == 'I'.code && b(2) == 'F'.code && b(3) == '8'.code) return GIF
            // BMP: "BM"
            if (b(0) == 'B'.code && b(1) == 'M'.code) return BMP
            // WEBP: "RIFF" .... "WEBP"
            if (header.size >= 12 && b(0) == 'R'.code && b(1) == 'I'.code && b(2) == 'F'.code && b(3) == 'F'.code &&
                b(8) == 'W'.code && b(9) == 'E'.code && b(10) == 'B'.code && b(11) == 'P'.code
            ) return WEBP
            // ISO BMFF: .... "ftyp" + brand
            if (header.size >= 12 && b(4) == 'f'.code && b(5) == 't'.code && b(6) == 'y'.code && b(7) == 'p'.code) {
                val brand = String(header, 8, 4, Charsets.US_ASCII).lowercase()
                return when {
                    brand.startsWith("avif") || brand.startsWith("avis") -> AVIF
                    brand.startsWith("heic") || brand.startsWith("heix") || brand.startsWith("hevc") || brand.startsWith("hevx") -> HEIC
                    brand.startsWith("mif1") || brand.startsWith("msf1") || brand.startsWith("heif") -> HEIF
                    else -> null
                }
            }
            return null
        }

        /**
         * MIME とバイト列の両方を見て最終的な形式を決める。
         * バイト列で判別できればそれを優先（拡張子と中身を一致させる）。判別できなければ MIME に従う。
         */
        fun resolve(mime: String?, header: ByteArray): ImageFormat? = sniff(header) ?: fromMimeType(mime)
    }
}
