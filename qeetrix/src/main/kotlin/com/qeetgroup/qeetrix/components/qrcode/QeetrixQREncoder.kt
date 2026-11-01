package com.qeetgroup.qeetrix.components.qrcode

/**
 * The QR code encoder of `@qeetrix/react` 0.4.0 (`src/experimental/qr-code/encode.ts`), ported step for step so a value
 * gives the same modules on Android as on the web: ISO/IEC 18004 byte mode (UTF-8), versions 1 to 40, the four
 * error-correction levels, Reed–Solomon over GF(256), and the eight masks scored by the standard's four penalty rules.
 * `QeetrixQREncoderTest` compares it with the web's output, module by module.
 */
internal object QeetrixQREncoder {
    private val FORMAT = intArrayOf(1, 0, 3, 2)

    private fun table(vararg rows: String): Array<IntArray> =
        rows.map { row -> intArrayOf(0) + row.split(" ").map(String::toInt) }.toTypedArray()

    private val ECC_PER_BLOCK =
        table(
            "7 10 15 20 26 18 20 24 30 18 20 24 26 30 22 24 28 30 28 28 28 28 30 30 26 28 30 30 30 30 30 30 30 30 30 30 30 30 30 30",
            "10 16 26 18 24 16 18 22 22 26 30 22 22 24 24 28 28 26 26 26 26 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28",
            "13 22 18 26 18 24 18 22 20 24 28 26 24 20 30 24 28 28 26 30 28 30 30 30 30 28 30 30 30 30 30 30 30 30 30 30 30 30 30 30",
            "17 28 22 16 22 28 26 26 24 28 24 28 22 24 24 30 28 28 26 28 30 24 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30",
        )

    private val BLOCKS =
        table(
            "1 1 1 1 1 2 2 2 2 4 4 4 4 4 6 6 6 6 7 8 8 9 9 10 12 12 12 13 14 15 16 17 18 19 19 20 21 22 24 25",
            "1 1 1 2 2 4 4 4 5 5 5 8 9 9 10 10 11 13 14 16 17 17 18 20 21 23 25 26 28 29 31 33 35 37 38 40 43 45 47 49",
            "1 1 2 2 4 4 6 6 8 8 8 10 12 16 12 17 16 18 21 20 23 23 25 27 29 34 34 35 38 40 43 45 48 51 53 56 59 62 65 68",
            "1 1 2 4 4 4 5 6 8 8 11 11 16 16 18 16 19 21 25 25 25 34 30 32 35 37 40 42 45 48 51 54 57 60 63 66 70 74 77 81",
        )

    private fun codewords(version: Int): Int {
        var modules = (16 * version + 128) * version + 64
        if (version >= 2) {
            val align = version / 7 + 2
            modules -= (25 * align - 10) * align - 55
            if (version >= 7) modules -= 36
        }
        return modules shr 3
    }

    private fun dataCodewords(
        version: Int,
        level: Int,
    ): Int = codewords(version) - ECC_PER_BLOCK[level][version] * BLOCKS[level][version]

    /** The most bytes [version] holds at [level]: its data, less the mode and the count. */
    fun capacity(
        version: Int,
        level: QeetrixQRCodeLevel,
    ): Int = Math.floorDiv(dataCodewords(version, level.ordinal) * 8 - 4 - (if (version < 10) 8 else 16), 8)

    private fun alignment(
        version: Int,
        size: Int,
    ): List<Int> {
        if (version == 1) return emptyList()
        val count = version / 7 + 2
        val step = (version * 8 + count * 3 + 5) / (count * 4 - 4) * 2
        val positions = mutableListOf(6)
        var at = size - 7
        while (positions.size < count) {
            positions.add(1, at)
            at -= step
        }
        return positions
    }

    /** Multiplication in GF(256) modulo x⁸ + x⁴ + x³ + x² + 1. */
    private fun times(
        x: Int,
        y: Int,
    ): Int {
        var z = 0
        for (i in 7 downTo 0) {
            z = (z shl 1) xor ((z ushr 7) * 285)
            z = z xor (((y ushr i) and 1) * x)
        }
        return z
    }

    private fun correction(
        data: List<Int>,
        degree: Int,
    ): IntArray {
        val divisor = IntArray(degree)
        divisor[degree - 1] = 1
        var root = 1
        for (i in 0 until degree) {
            for (j in 0 until degree) divisor[j] = times(divisor[j], root) xor (if (j + 1 < degree) divisor[j + 1] else 0)
            root = times(root, 2)
        }
        val remainder = ArrayDeque(List(degree) { 0 })
        for (byte in data) {
            val factor = byte xor remainder.removeFirst()
            remainder.addLast(0)
            for (j in 0 until degree) remainder[j] = remainder[j] xor times(divisor[j], factor)
        }
        return remainder.toIntArray()
    }

    private fun formatBits(
        level: Int,
        mask: Int,
    ): Int {
        val data = (FORMAT[level] shl 3) or mask
        var rem = data
        for (i in 0 until 10) rem = (rem shl 1) xor ((rem ushr 9) * 1335)
        return ((data shl 10) or rem) xor 21522
    }

    private val MASKS: List<(Int, Int) -> Boolean> =
        listOf(
            { x, y -> (x + y) % 2 == 0 },
            { _, y -> y % 2 == 0 },
            { x, _ -> x % 3 == 0 },
            { x, y -> (x + y) % 3 == 0 },
            { x, y -> (x / 3 + y / 2) % 2 == 0 },
            { x, y -> x * y % 2 + x * y % 3 == 0 },
            { x, y -> (x * y % 2 + x * y % 3) % 2 == 0 },
            { x, y -> ((x + y) % 2 + x * y % 3) % 2 == 0 },
        )

    private val FINDER = listOf("10111010000", "00001011101")
    private val RUN = Regex("0{5,}|1{5,}")

    private fun penalty(
        dark: IntArray,
        size: Int,
    ): Int {
        var score = 0
        var total = 0
        val width = size + 1
        // Each row, then each column, as a line of '0' and '1'.
        val text = CharArray(2 * size * width) { '\n' }
        for (y in 0 until size) {
            for (x in 0 until size) {
                val c = dark[y * size + x]
                total += c
                text[y * width + x] = '0' + c
                text[(size + x) * width + y] = '0' + c
            }
        }
        val lines = String(text)
        for (run in RUN.findAll(lines)) score += run.value.length - 2
        for (pattern in FINDER) {
            var at = lines.indexOf(pattern)
            while (at >= 0) {
                score += 40
                at = lines.indexOf(pattern, at + 1)
            }
        }
        for (y in 0 until size - 1) {
            for (x in 0 until size - 1) {
                val c = dark[y * size + x]
                if (c == dark[y * size + x + 1] && c == dark[(y + 1) * size + x] && c == dark[(y + 1) * size + x + 1]) score += 3
            }
        }
        return score + 10 * (Math.abs(total * 20 - size * size * 10) / (size * size))
    }

    /**
     * [text] as a QR code at [level]: the modules row by row, `true` for dark, in the smallest version that holds it.
     *
     * @throws IllegalArgumentException when it is too long for any version at [level].
     */
    fun encode(
        text: String,
        level: QeetrixQRCodeLevel = QeetrixQRCodeLevel.M,
    ): List<BooleanArray> {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val l = level.ordinal
        val ecc = ECC_PER_BLOCK[l]
        val blocks = BLOCKS[l]
        var version = 1
        while (bytes.size > capacity(version, level)) {
            require(++version <= 40) { "QR code: ${bytes.size} bytes is too long for level ${level.value}." }
        }
        val bits = ArrayList<Int>()

        fun put(
            value: Int,
            length: Int,
        ) {
            for (i in length - 1 downTo 0) bits += (value ushr i) and 1
        }
        val room = dataCodewords(version, l) * 8
        put(4, 4)
        put(bytes.size, if (version < 10) 8 else 16)
        for (byte in bytes) put(byte.toInt() and 0xFF, 8)
        put(0, minOf(4, room - bits.size))
        put(0, (8 - bits.size % 8) % 8)
        var pad = 236
        while (bits.size < room) {
            put(pad, 8)
            pad = pad xor 253
        }
        val data = (0 until bits.size step 8).map { i -> (i until i + 8).fold(0) { byte, at -> (byte shl 1) or bits[at] } }
        val count = blocks[version]
        val degree = ecc[version]
        val short = codewords(version) / count - degree
        val longs = codewords(version) % count
        val parts = ArrayList<List<Int>>()
        var at = 0
        for (b in 0 until count) {
            val length = short + if (b >= count - longs) 1 else 0
            parts += data.subList(at, at + length)
            at += length
        }
        val fixes = parts.map { correction(it, degree) }
        val stream = ArrayList<Int>()
        for (i in 0..short) for (part in parts) if (i < part.size) stream += part[i]
        for (i in 0 until degree) for (fix in fixes) stream += fix[i]

        val size = version * 4 + 17
        val dark = IntArray(size * size)
        val reserved = BooleanArray(size * size)

        fun set(
            x: Int,
            y: Int,
            on: Boolean,
        ) {
            dark[y * size + x] = if (on) 1 else 0
            reserved[y * size + x] = true
        }
        for (i in 0 until size) {
            set(6, i, i % 2 == 0)
            set(i, 6, i % 2 == 0)
        }
        for ((cx, cy) in listOf(3 to 3, size - 4 to 3, 3 to size - 4)) {
            for (dy in -4..4) {
                for (dx in -4..4) {
                    val d = maxOf(Math.abs(dx), Math.abs(dy))
                    val x = cx + dx
                    val y = cy + dy
                    if (x in 0 until size && y in 0 until size) set(x, y, d != 2 && d != 4)
                }
            }
        }
        val centres = alignment(version, size)
        val last = centres.size - 1
        centres.forEachIndexed { i, ay ->
            centres.forEachIndexed { j, ax ->
                if (!((i == 0 && (j == 0 || j == last)) || (i == last && j == 0))) {
                    for (dy in -2..2) for (dx in -2..2) set(ax + dx, ay + dy, maxOf(Math.abs(dx), Math.abs(dy)) != 1)
                }
            }
        }

        fun drawFormat(word: Int) {
            fun bit(i: Int) = (word ushr i) and 1 == 1
            for (i in 0..5) set(8, i, bit(i))
            set(8, 7, bit(6))
            set(8, 8, bit(7))
            set(7, 8, bit(8))
            for (i in 9 until 15) set(14 - i, 8, bit(i))
            for (i in 0 until 8) set(size - 1 - i, 8, bit(i))
            for (i in 8 until 15) set(8, size - 15 + i, bit(i))
            set(8, size - 8, true)
        }
        drawFormat(0)
        if (version >= 7) {
            var rem = version
            for (i in 0 until 12) rem = (rem shl 1) xor ((rem ushr 11) * 7973)
            val word = (version shl 12) or rem
            for (i in 0 until 18) {
                val a = size - 11 + i % 3
                val b = i / 3
                set(a, b, (word ushr i) and 1 == 1)
                set(b, a, (word ushr i) and 1 == 1)
            }
        }
        var i = 0
        var right = size - 1
        while (right >= 1) {
            if (right == 6) right = 5
            for (v in 0 until size) {
                for (j in 0 until 2) {
                    val x = right - j
                    val y = if (((right + 1) and 2) == 0) size - 1 - v else v
                    if (!reserved[y * size + x] && i < stream.size * 8) {
                        dark[y * size + x] = (stream[i ushr 3] ushr (7 - (i and 7))) and 1
                        i++
                    }
                }
            }
            right -= 2
        }

        fun masked(mask: Int): IntArray {
            val out = dark.copyOf()
            val fn = MASKS[mask]
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (!reserved[y * size + x] &&
                        fn(x, y)
                    ) {
                        out[y * size + x] = 1 - out[y * size + x]
                    }
                }
            }
            return out
        }
        var best = dark
        var lowest = Int.MAX_VALUE
        val plain = dark.copyOf()
        for (mask in 0 until 8) {
            plain.copyInto(dark)
            drawFormat(formatBits(l, mask))
            val candidate = masked(mask)
            val score = penalty(candidate, size)
            if (score < lowest) {
                lowest = score
                best = candidate
            }
        }
        return List(size) { y -> BooleanArray(size) { x -> best[y * size + x] == 1 } }
    }
}
