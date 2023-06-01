package com.example.siteanalysis.services

import DirectoryService
import com.example.siteanalysis.controller.GetController
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileWriter

@SpringBootTest(classes = [ExcelReader::class, DirectoryService::class, GetController::class])
class ExcelReaderTest {

    @Autowired
    lateinit var excelReader: ExcelReader

    @Autowired
    lateinit var directoryService: DirectoryService

    @Autowired
    lateinit var getController: GetController

    @Test
    fun testFindExcelFile(): File? {
        val dir = "C:\\UserData\\z004a6nh\\Documents\\OneDrive - Siemens AG\\DE\\NBG K"
        //create a regex pattern to match a file with .xlsx extension and the word categorised in it
        val pattern= "^.*progress.*\\.xlsx$"
        //case insensitive
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        val file = directoryService.findExcelFile(dir,regex)
        assert(file != null)
        return file
    }

    @Test
    fun readExcelFile(): ArrayList<String> {
        val file = this.testFindExcelFile()
        val objectList = excelReader.readExcelFile(file!!)


        // convert it into a List of Strings
        val list = ArrayList<String>()
        for (obj in objectList) {
            // get the association IP range/CIDR -> 10.39.238.0/23
            val associationValue = obj["IP range/CIDR"]
            list.add(associationValue.toString())
        }

        return list


    }

    @Test
    fun bulkSendRequestAndProcessResponse(){
        val list = this.readExcelFile()
        //for each of the Strings in the list, spit it into two parts by the slash
        //then send a request to the API with the first part as the IP address and the second part as the CIDR
        for (item in list){
            val split = item.split("/")
            val ipAddress = split[0]
            val cidr = split[1]
            val response = getController.sendRequestAndProcessResponse(ipAddress, cidr)
            //write the response to a file into the same package as this test
            //name the file after the IP address and CIDR
            val fileName = ipAddress + "_" + cidr + ".json"
            val file = File(fileName)
            val fileWriter = FileWriter(file)
            fileWriter.write(response)
            fileWriter.close()
            println("Response written to file $fileName")
        }
    }

    @Test
    fun sendRequestAndProcessResponse() {
        val ipAddress = "10.221.6.160"
        val cidr = "29"
        val response = getController.sendRequestAndProcessResponse(ipAddress, cidr)
        println(response)
    }
}