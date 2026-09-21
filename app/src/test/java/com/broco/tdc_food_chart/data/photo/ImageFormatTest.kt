package com.broco.tdc_food_chart.data.photo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ImageFormatTest {
    private fun bytes(vararg v: Int) = ByteArray(v.size) { v[it].toByte() }
    private fun ascii(s: String) = s.toByteArray(Charsets.US_ASCII)

    @Test
    fun sniffsJpeg() {
        assertEquals(ImageFormat.JPEG, ImageFormat.sniff(bytes(0xFF, 0xD8, 0xFF, 0xE1, 0, 0, 0, 0, 0, 0, 0, 0)))
    }

    @Test
    fun sniffsPng() {
        assertEquals(ImageFormat.PNG, ImageFormat.sniff(bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0)))
    }

    @Test
    fun sniffsWebp() {
        assertEquals(ImageFormat.WEBP, ImageFormat.sniff(ascii("RIFF") + bytes(0, 0, 0, 0) + ascii("WEBPVP8 ")))
    }

    @Test
    fun sniffsHeicHeifAvif() {
        assertEquals(ImageFormat.HEIC, ImageFormat.sniff(bytes(0, 0, 0, 0x18) + ascii("ftypheic") + bytes(0, 0, 0, 0)))
        assertEquals(ImageFormat.HEIF, ImageFormat.sniff(bytes(0, 0, 0, 0x18) + ascii("ftypmif1") + bytes(0, 0, 0, 0)))
        assertEquals(ImageFormat.AVIF, ImageFormat.sniff(bytes(0, 0, 0, 0x18) + ascii("ftypavif") + bytes(0, 0, 0, 0)))
    }

    @Test
    fun sniffsGif() {
        assertEquals(ImageFormat.GIF, ImageFormat.sniff(ascii("GIF89a") + bytes(0, 0, 0, 0, 0, 0)))
    }

    @Test
    fun unknownBytesGiveNull() {
        assertNull(ImageFormat.sniff(ascii("hello world!")))
        assertNull(ImageFormat.sniff(bytes(1, 2)))
    }

    @Test
    fun mapsMimeTypes() {
        assertEquals(ImageFormat.JPEG, ImageFormat.fromMimeType("image/jpeg"))
        assertEquals(ImageFormat.JPEG, ImageFormat.fromMimeType("image/jpg"))
        assertEquals(ImageFormat.PNG, ImageFormat.fromMimeType("IMAGE/PNG"))
        assertEquals(ImageFormat.HEIC, ImageFormat.fromMimeType("image/heic"))
        assertEquals(ImageFormat.WEBP, ImageFormat.fromMimeType("image/webp; charset=binary"))
        assertNull(ImageFormat.fromMimeType("application/octet-stream"))
        assertNull(ImageFormat.fromMimeType(null))
    }

    @Test
    fun resolvePrefersBytesOverMime() {
        // MIME は png と言っているが中身は JPEG → 拡張子は中身に合わせる
        val jpegHeader = bytes(0xFF, 0xD8, 0xFF, 0xE0, 0, 0, 0, 0, 0, 0, 0, 0)
        assertEquals(ImageFormat.JPEG, ImageFormat.resolve("image/png", jpegHeader))
        // 中身で判別できないときだけ MIME に従う
        assertEquals(ImageFormat.PNG, ImageFormat.resolve("image/png", ascii("????????????")))
        assertNull(ImageFormat.resolve(null, ascii("????????????")))
    }

    @Test
    fun extensionsMatchMime() {
        ImageFormat.entries.forEach { f ->
            assertEquals(f, ImageFormat.fromMimeType(f.mimeType))
        }
        assertEquals("jpg", ImageFormat.JPEG.extension)
    }
}
