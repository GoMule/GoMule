@file:JvmName("FileReader")

package gomule.files

import java.io.InputStream

fun <T : Any> readTsv(inputStream: InputStream, lineParser: LineParser<T?>): List<T> =
    inputStream.bufferedReader().useLines { lines ->
        val iterator = lines.iterator()
        val header = parseHeader(iterator.next())
        iterator.asSequence().map { it.split('\t') }
            .map { lineParser.parseLine(Line(it, header)) }
            .filterNotNull()
            .toList()
    }

fun parseHeader(headerLine: String): Map<String, Int> =
    headerLine.split('\t')
        .mapIndexed { index, s -> s to index }
        .toMap()

interface LineParser<T> {
    fun parseLine(line: Line): T?
}

fun getResource(name: String) =
    object {}.javaClass.getResourceAsStream("/$name") ?: throw IllegalStateException("Failed to load $name")

data class Line(
    private val line: List<String>,
    private val header: Map<String, Int>
) {
    operator fun get(i: Int) = line[i]
    operator fun get(s: String) = line[header.getValue(s)]

    fun coalesce(vararg fieldNames: String) = fieldNames
        .mapNotNull { header[it] }
        .map { line[it] }
        .first()
}