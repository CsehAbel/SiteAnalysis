package com.example.siteanalysis.services

import ch.qos.logback.core.util.FileUtil
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.io.FileUtils
import org.apache.commons.net.util.SubnetUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import java.io.File
import java.io.FileReader
import java.io.IOException


@SpringBootTest
public class AssignToIPServiceTest {

//    {
//        dst:10.220.15.48/28 OR src:10.220.15.48/28 AND NOT action:Drop
//        dst:10.220.21.120/29 OR src:10.220.21.120/29 AND NOT action:Drop
//        dst:10.220.29.200/29 OR src:10.220.29.200/29 AND NOT action:Drop
//        dst:10.220.29.208/29 OR src:10.220.29.208/29 AND NOT action:Drop
//        dst:10.220.43.112/28 OR src:10.220.43.112/28 AND NOT action:Drop
//        dst:10.220.214.224/28 OR src:10.220.214.224/28 AND NOT action:Drop
//        dst:10.220.225.0/26 OR src:10.220.225.0/26 AND NOT action:Drop
//        dst:10.220.241.128/27 OR src:10.220.241.128/27 AND NOT action:Drop
//        dst:10.220.241.160/27 OR src:10.220.241.160/27 AND NOT action:Drop
//        dst:10.221.6.224/29 OR src:10.221.6.224/29 AND NOT action:Drop
//        dst:10.221.6.232/29 OR src:10.221.6.232/29 AND NOT action:Drop
//        dst:10.221.6.240/29 OR src:10.221.6.240/29 AND NOT action:Drop
//        dst:10.221.6.248/29 OR src:10.221.6.248/29 AND NOT action:Drop
//        dst:10.221.7.0/29 OR src:10.221.7.0/29 AND NOT action:Drop
//        dst:10.221.7.8/29 OR src:10.221.7.8/29 AND NOT action:Drop
//        dst:10.221.7.16/29 OR src:10.221.7.16/29 AND NOT action:Drop
//        dst:10.221.7.24/29 OR src:10.221.7.24/29 AND NOT action:Drop
//        dst:10.221.152.48/28 OR src:10.221.152.48/28 AND NOT action:Drop
//        dst:146.254.3.128/26 OR src:146.254.3.128/26 AND NOT action:Drop
//        dst:146.254.3.192/26 OR src:146.254.3.192/26 AND NOT action:Drop
//        dst:147.54.0.0/27 OR src:147.54.0.0/27 AND NOT action:Drop
//        dst:147.54.0.64/27 OR src:147.54.0.64/27 AND NOT action:Drop
//        dst:147.54.15.128/27 OR src:147.54.15.128/27 AND NOT action:Drop
//        dst:147.54.27.144/29 OR src:147.54.27.144/29 AND NOT action:Drop
//        dst:147.54.28.64/27 OR src:147.54.28.64/27 AND NOT action:Drop
//        dst:147.54.119.32/27 OR src:147.54.119.32/27 AND NOT action:Drop
//        dst:147.54.127.176/28 OR src:147.54.127.176/28 AND NOT action:Drop
//        dst:147.54.139.96/27 OR src:147.54.139.96/27 AND NOT action:Drop
//        dst:147.54.142.0/26 OR src:147.54.142.0/26 AND NOT action:Drop
//        dst:147.54.143.160/27 OR src:147.54.143.160/27 AND NOT action:Drop
//        dst:147.54.153.12/30 OR src:147.54.153.12/30 AND NOT action:Drop
//        dst:147.54.153.160/27 OR src:147.54.153.160/27 AND NOT action:Drop
//        dst:147.54.154.128/27 OR src:147.54.154.128/27 AND NOT action:Drop
//        dst:147.54.158.80/28 OR src:147.54.158.80/28 AND NOT action:Drop
//        dst:147.54.182.0/27 OR src:147.54.182.0/27 AND NOT action:Drop
//        dst:147.54.189.80/28 OR src:147.54.189.80/28 AND NOT action:Drop
//        dst:147.54.208.96/27 OR src:147.54.208.96/27 AND NOT action:Drop
//        dst:147.54.212.72/29 OR src:147.54.212.72/29 AND NOT action:Drop
//        dst:147.54.214.128/26 OR src:147.54.214.128/26 AND NOT action:Drop
//        dst:147.54.220.0/24 OR src:147.54.220.0/24 AND NOT action:Drop
//        dst:147.54.223.128/25 OR src:147.54.223.128/25 AND NOT action:Drop
//    }
//create a list of string from ip ranges



    @Test
    @Throws(IOException::class)
    fun readAllCsvFiles() {
        var listOfExcelFiles = mutableListOf<Pair<String,MutableList<Map<String, Any>>>>()
        val resolver: ResourcePatternResolver = PathMatchingResourcePatternResolver()
        val resources: Array<Resource> = resolver.getResources("classpath*:logs/*.csv")
        for (resource in resources) {
            val file: File = resource.getFile()
            val file_name = file.getName()
            val tempExcelFile = readCSVFile(file,file_name)
            val excelFile = extractIPFromSource(tempExcelFile)
            //replace SAG_IP(IP) with IP by matching a regex
            listOfExcelFiles.add(Pair(file_name,excelFile))
        }
        val ipRanges = mutableListOf(
                "10.244.80.128/27",
                "129.73.18.160/27",
                "129.73.82.64/26",
                "129.73.147.192/26",
                "129.73.190.128/27",
                "129.73.244.128/25"
        )
        for(ipRange in ipRanges){
            val pair = findExcelFile(listOfExcelFiles, ipRange)
            if(pair != null){
                val file_name = pair.first
                renameAndMoveFile(file_name, ipRange)
            }
        }
    }

    private fun extractIPFromSource(excelFile: MutableList<Map<String, Any>>): MutableList<Map<String, Any>> {
        val header1 = "Source"
        val header2 = "Destination"

        val ipRegex = """(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})""".toRegex()

        val modifiedExcelFile = mutableListOf<Map<String, Any>>()
        for (map in excelFile) {
            val sourceValue = map[header1]?.toString() ?: continue

            val ipMatchResult = ipRegex.find(sourceValue)
            val ipAddress = ipMatchResult?.value

            val destinationValue = map[header2]?.toString() ?: continue

            val ipMatchResult2 = ipRegex.find(destinationValue)
            val ipAddress2 = ipMatchResult2?.value

            if (ipAddress != null && ipAddress2 != null) {
                val modifiedMap = map.toMutableMap()
                modifiedMap[header1] = ipAddress
                modifiedMap[header2] = ipAddress2
                modifiedExcelFile.add(modifiedMap)
            } else if (ipAddress2 != null) {
                val modifiedMap = map.toMutableMap()
                modifiedMap[header2] = ipAddress2
                modifiedExcelFile.add(modifiedMap)
            } else if (ipAddress != null) {
                val modifiedMap = map.toMutableMap()
                modifiedMap[header1] = ipAddress
                modifiedExcelFile.add(modifiedMap)
            }
        }

        return modifiedExcelFile
    }

//    private fun renameAndMoveFile(fileName: String, ipRange: String) {
//        val file = File("src/main/resources/logs/$fileName")
//        //replace the slash with underscore
//        val ipRange = ipRange.replace("/", "_")
//        val newFile = File("src/main/resources/logs/$ipRange.csv")
//        FileUtils.copyFile(file, newFile)
//    }

    private fun renameAndMoveFile(fileName: String, ipRange: String) {
        val file = File("src/main/resources/logs/$fileName")
        //replace the slash with underscore
        val ipRange = ipRange.replace("/", "_")
        val newFile = File("src/main/resources/logs/$ipRange.csv")
        if (file.exists()) {
            file.renameTo(newFile)
        } else {
            println("File does not exist $fileName cant rename to $ipRange.csv")
        }
    }

    private fun findExcelFile(listOfExcelFiles: MutableList<Pair<String, MutableList<Map<String, Any>>>>, ipRange: String): Pair<String, MutableList<Map<String, Any>>>? {

        //iterate over the list of pairs of excelFile and file_name
        for (pair in listOfExcelFiles) {
            val excelFile = pair.second
            val file_name = pair.first
            val matches: Boolean = ipRangeMatches(excelFile, ipRange)
            if (matches) {
                //return the excelFile and file_name
                return pair
            }
        }
        println("No match found for $ipRange")
        return null
    }

    private fun ipRangeMatches(excelFile: MutableList<Map<String, Any>>, ipRange: String): Boolean {
        val header1 = "Destination"
        val header2 = "Source"
        var matchesDestination = 0
        //only check the first 5 rows
        var i = 0
        for (row in excelFile) {
            if (i == 5) {
                break
            }
            val destination = row[header1]
            val isInRange: Boolean = isInRange(destination.toString(), ipRange)
            if (isInRange) {
                matchesDestination++
            }
        }

        var matchesSource = 0
        i = 0
        for (row in excelFile) {
            if(i == 5){
                break
            }
            val source = row[header2]
            val isInRange: Boolean = isInRange(source.toString(), ipRange)
            if (isInRange) {
                matchesSource++
            }
        }
        return matchesDestination >= 1 || matchesSource >= 1
    }

    private fun isInRange(ip: String, ipRange: String): Boolean {
        val subnetUtils = SubnetUtils(ipRange)
        val networkAddress = subnetUtils.info.networkAddress
        val broadcastAddress = subnetUtils.info.broadcastAddress
        //check if ip is in range
        val ipNumber = ipToNumber(ip)
        val networkAddressNumber = ipToNumber(networkAddress)
        val broadcastAddressNumber = ipToNumber(broadcastAddress)
        return ipNumber in networkAddressNumber..broadcastAddressNumber
    }

    private fun ipToNumber(ipRange: String): Long {
        val ipRegex = """(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})""".toRegex()
        val ipMatchResult = ipRegex.find(ipRange)
        val ipAddress = ipMatchResult?.value
        val ip = ipAddress!!.split(".")
        val ipNumber = ip[0].toLong() * 256 * 256 * 256 + ip[1].toLong() * 256 * 256 + ip[2].toLong() * 256 + ip[3].toLong()
        return ipNumber
    }

    fun readExcelFile(file: File?, file_name: String): MutableList<Map<String, Any>> {
        if (file == null) {
            return mutableListOf()
        }

        val workbook = XSSFWorkbook(file.inputStream())

        val sheet = workbook.getSheetAt(0) // Assuming the first sheet

        val headerRow = sheet.getRow(0)
        val startRow = 1 // Assuming the data starts from the second row
        val lastRow = sheet.lastRowNum

        val objectList = mutableListOf<Map<String, Any>>()

        for (rowNum in startRow..lastRow) {
            val row = sheet.getRow(rowNum)

            val objectProperties = mutableMapOf<String, Any>()
            var shouldSkipRow = false

            for (colNum in 0 until row.lastCellNum) {
                val headerCell = headerRow.getCell(colNum)

                val cell = row.getCell(colNum)
                if (cell == null) continue

                //fill the objectProperties map with the values from the excel file
                val cellValue = this.getCellValue(cell)
                objectProperties[headerCell.stringCellValue] = cellValue as Any

            }

            workbook.close()


        }
        return objectList
    }

    fun readCSVFile(file: File?, file_name: String): MutableList<Map<String, Any>> {
        if (file == null) {
            return mutableListOf()
        }

        val csvParser = CSVParser(FileReader(file), CSVFormat.DEFAULT.withHeader())

        val objectList = mutableListOf<Map<String, Any>>()

        var rowNum = 0
        for (record in csvParser) {
            val objectProperties = mutableMapOf<String, Any>()
            var shouldSkipRow = false

            for (header in csvParser.headerMap.keys) {
                try {
                    val cellValue = record[header]
                    objectProperties[header] = cellValue as Any
                } catch (ex: Exception) {
                    var destExists = objectProperties["Destination"] != null
                    var sourceExists = objectProperties["Source"] != null
                    if (destExists && sourceExists) {
                        "".isEmpty()
                    } else {
                        shouldSkipRow = true
                        break
                    }
                }
            }

            if (!shouldSkipRow) {
                objectList.add(objectProperties)
            }
            rowNum++
            if (rowNum == 10) {
                break
            }
        }

        csvParser.close()

        return objectList
    }

    fun getCellValue(cell: Cell): Any? {
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue
            CellType.BOOLEAN -> cell.booleanCellValue
            CellType.FORMULA -> cell.cellFormula
            else -> null
        }
    }
}