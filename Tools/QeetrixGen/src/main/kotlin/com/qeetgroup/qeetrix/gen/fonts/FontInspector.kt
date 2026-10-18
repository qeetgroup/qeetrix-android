package com.qeetgroup.qeetrix.gen.fonts

import com.qeetgroup.qeetrix.gen.GeneratorError

/**
 * The facts the library needs from a TrueType/OpenType file: its names and its variation axes. They
 * are read from the font itself rather than from documentation, so what the library assumes about a
 * bundled font is what the file actually carries.
 */
data class FontInfo(
    val familyName: String,
    val postScriptName: String,
    val version: String,
    val axes: List<Axis>,
) {
    data class Axis(
        val tag: String,
        val minimum: Double,
        val defaultValue: Double,
        val maximum: Double,
    )
}

object FontInspector {
    fun inspect(
        bytes: ByteArray,
        context: String,
    ): FontInfo {
        fun u8(offset: Int): Int {
            if (offset >= bytes.size) throw GeneratorError("$context: truncated font.")
            return bytes[offset].toInt() and 0xFF
        }

        fun u16(offset: Int): Int = (u8(offset) shl 8) or u8(offset + 1)

        fun u32(offset: Int): Long = (u16(offset).toLong() shl 16) or u16(offset + 2).toLong()

        fun fixed(offset: Int): Double = u32(offset).toInt().toDouble() / 65536

        fun tag(offset: Int): String = String(ByteArray(4) { bytes[offset + it] }, Charsets.US_ASCII).also { u8(offset + 3) }

        val version = u32(0)
        if (version != 0x0001_0000L && version != 0x4F54_544FL) { // TrueType or 'OTTO'
            throw GeneratorError("$context: not a TrueType or OpenType font (WOFF/WOFF2 are web formats).")
        }
        val tables = HashMap<String, Int>()
        for (index in 0 until u16(4)) {
            val record = 12 + index * 16
            tables[tag(record)] = u32(record + 8).toInt()
        }

        // name: prefer Windows Unicode English, then any Windows Unicode, then Mac Roman.
        val name = tables["name"] ?: throw GeneratorError("$context: no name table.")
        val count = u16(name + 2)
        val storage = name + u16(name + 4)
        val candidates = HashMap<Int, MutableList<Pair<Int, String>>>()
        for (index in 0 until count) {
            val record = name + 6 + index * 12
            val platform = u16(record)
            val encoding = u16(record + 2)
            val language = u16(record + 4)
            val id = u16(record + 6)
            val length = u16(record + 8)
            val offset = storage + u16(record + 10)
            if (offset + length > bytes.size) continue
            val slice = bytes.copyOfRange(offset, offset + length)
            if (platform == 3 && (encoding == 1 || encoding == 10)) {
                candidates.getOrPut(id) { mutableListOf() } += (if (language == 0x409) 0 else 1) to String(slice, Charsets.UTF_16BE)
            } else if (platform == 1 && encoding == 0) {
                candidates.getOrPut(id) { mutableListOf() } += 2 to String(slice, Charsets.UTF_8)
            }
        }

        fun string(id: Int): String? = candidates[id]?.minByOrNull { it.first }?.second
        val postScriptName = string(6)
        val family = string(16) ?: string(1)
        if (postScriptName == null || family == null) throw GeneratorError("$context: the name table has no PostScript or family name.")

        val axes = mutableListOf<FontInfo.Axis>()
        tables["fvar"]?.let { fvar ->
            val axesOffset = fvar + u16(fvar + 4)
            val axisCount = u16(fvar + 8)
            val axisSize = u16(fvar + 10)
            for (index in 0 until axisCount) {
                val record = axesOffset + index * axisSize
                axes += FontInfo.Axis(tag(record), fixed(record + 4), fixed(record + 8), fixed(record + 12))
            }
        }
        return FontInfo(family, postScriptName, string(5) ?: "", axes)
    }
}
