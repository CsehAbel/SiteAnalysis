package com.example.siteanalysis.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ConvertResponse {

    fun convertToListOfMaps(convertedObj: InputStream): MutableList<Map<String, Any>> {
        val objectMapper = jacksonObjectMapper()
        val listType = objectMapper.typeFactory.constructCollectionType(MutableList::class.java, Map::class.java)
        return objectMapper.readValue(convertedObj, listType)
    }

    fun writeListToCsv(list: MutableList<Map<String, Any>>, headers: Array<String>, filePath: String) {
        val fileWriter = FileWriter(File(filePath))
        val csvPrinter = CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(*headers))

        for (item in list) {
            //val values = headers.map { header -> item[header]?.toString() ?: "" }
            val values = headers.map { header ->
                if (header == "Time") {
                    formatDate(item[header]?.toString() ?: "")
                } else {
                    item[header]?.toString() ?: ""
                }
            }
            csvPrinter.printRecord(values)
        }

        csvPrinter.close()
        fileWriter.close()
    }

    fun formatDate(dateString: String): String {
        if(dateString == "") return dateString
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a", Locale.US)
        val dateTime = LocalDateTime.parse(dateString, inputFormat)
        val zonedDateTime = ZonedDateTime.of(dateTime, ZoneOffset.UTC)
        return outputFormat.format(zonedDateTime)
    }
}