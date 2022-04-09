package gomule.files

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class FileReaderTest {
    @Test
    fun testReadTsv() {
        val tempFile = File.createTempFile("fileReaderTest", null)
        tempFile.deleteOnExit()
        tempFile.writeText("header1\theader2\nval1\tval2\nval3\tval4\nignore\tignore")
        val actual = readTsv(FileInputStream(tempFile), lineParser)
        val expected = listOf(Pair("val2", "val1"), Pair("val4", "val3"))
        assertEquals(expected, actual)
    }

    private val lineParser = object : LineParser<Pair<String, String>?> {
        override fun parseLine(line: Line): Pair<String, String>? =
            if (line[0] == "ignore") null else Pair(line[1], line[0])
    }
}
