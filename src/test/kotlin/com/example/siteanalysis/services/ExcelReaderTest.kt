package com.example.siteanalysis.services

import DirectoryService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileWriter

@SpringBootTest(classes = [ExcelReader::class, DirectoryService::class])
class ExcelReaderTest {

    @Autowired
    lateinit var excelReader: ExcelReader

    @Autowired
    lateinit var directoryService: DirectoryService

    @Test
    fun testFindExcelFile(): File? {
        val dir = "C:\\UserData\\z004a6nh\\Documents\\OneDrive - Siemens AG\\DE\\DRS O"
        //create a regex pattern to match a file with .xlsx extension and the word categorised in it
        val pattern= "^.*progress.*\\.xlsx$"
        //case insensitive
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        val file = directoryService.findExcelFile(dir,regex)
        assert(file != null)
        return file
    }

    @Test
    fun readExcelFile() {
        val file = this.testFindExcelFile()
        val objectList = excelReader.readExcelFile(file!!)


        // convert it into a List of Strings
        val list = ArrayList<String>()
        for (obj in objectList) {
            // get the association IP range/CIDR -> 10.39.238.0/23
            val associationValue = obj["IP range/CIDR"]
            //and turn it into dst: 10.39.238.0/23 OR src: 10.39.238.0/23 AND NOT action:drop
            val dst = "dst:$associationValue OR src:$associationValue AND NOT action:Drop"
            list.add(dst)
        }
        //write the list to a file
        val filePath="C:\\UserData\\z004a6nh\\Documents\\testDRS2.txt"
        val file2 : File = File(filePath)
        val writer = file2.bufferedWriter()
        for (line in list) {
            writer.write(line)
            writer.newLine()
            println(line)
        }


    }
}