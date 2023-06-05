package com.example.siteanalysis.services

import DirectoryService
import com.example.siteanalysis.controller.GetController
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.File
import java.io.FileWriter
import java.io.InputStream


@SpringBootTest(classes = [ExcelReader::class, DirectoryService::class, GetController::class, ConvertResponse::class])
class ExcelReaderTest {

    @Autowired
    lateinit var excelReader: ExcelReader

    @Autowired
    lateinit var directoryService: DirectoryService

    @Autowired
    lateinit var getController: GetController

    @Autowired
    lateinit var convertResponse: ConvertResponse

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

    @Test
    fun convertResponse() {
        val resources: Array<Resource> = PathMatchingResourcePatternResolver().getResources("classpath*:LWFR/*.json")
        //for each resource expose an input stream
        //add the MutableList<Map<String, Any>> to a list
        val list = ArrayList<MutableList<Map<String, Any>>>()
        //for each resource expose an input stream
        for (resource in resources) {
            val inputStream: InputStream = resource.getInputStream()
            //parse the input stream
            val converted=convertResponse.convertToListOfMaps(inputStream)
            val fileName = resource.filename
            //convert 10.220.211.80_28.json to 10.220.211.80_28.csv by replacing .json with .csv
            val fileNameCSV = fileName!!.replace(".json", ".csv")

//            {
//                "Time": "Jun 1, 2023 5:06:52 PM",
//                "Destination": "SAG_192.129.41.35 (192.129.41.35)",
//                "Rule": 169,
//                "Interface Direction": "inbound",
//                "rule_uid": "7fbdc415-750a-4f01-93aa-81c2cffc1f1b",
//                "Type": "Connection",
//                "Interface": "bond2.1959",
//                "Policy Date": "2023-06-01T10:55:42Z",
//                "Service ID": "domain-udp",
//                "Action": "Accept",
//                "ID": "05e629d8-efbc-20e8-6478-b40c0000004a",
//                "Interface Name": "bond2.1959",
//                "Layer Name": "Network",
//                "Source Port": 20968,
//                "Product Family": "Access",
//                "Blade": "Firewall",
//                "Sequence Number": 1079,
//                "Source Zone": "Internal",
//                "Source": "10.220.211.90",
//                "Access Rule Name": "white_DNS",
//                "Policy Name": "sag-se-bp",
//                "id_generated_by_indexer": "FALSE",
//                "Destination Zone": "External",
//                "Database Tag": "{3775D95A-DBDF-3545-A954-76ECA915476E}",
//                "Log Server Origin": "DLS-P-SAG-Energy (155.45.240.229)",
//                "Service": "domain-udp",
//                "Origin": "FW-SAG-Energy-DE-01",
//                "Marker": "@A@@B@1685626284@C@15678463",
//                "Destination Port": 53,
//                "Domain": "CST-P-SAG-Energy",
//                "Protocol": "UDP (17) (17)",
//                "logid": 0,
//                "first": "TRUE",
//                "Policy Management": "DMS-P-SAG-Energy",
//                "Direction of Connection": null,
//                "log_delay": null
//            }

            val headers = listOf("Time","Destination","Rule","Interface Direction","rule_uid","Type","Interface","Policy Date","Service ID","Action","ID","Interface Name","Layer Name","Source Port","Product Family","Blade","Sequence Number","Source Zone","Source","Access Rule Name","Policy Name","id_generated_by_indexer","Destination Zone","Database Tag","Log Server Origin","Service","Origin","Marker","Destination Port","Domain","Protocol","logid","first","Policy Management","Direction of Connection","log_delay")
            if (fileName != null) {
                convertResponse.writeListToCsv(converted, headers.toTypedArray(), fileNameCSV)
                println("File $fileNameCSV written")
            }

        }


    }
}