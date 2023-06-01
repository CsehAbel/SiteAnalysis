package com.example.siteanalysis.services
import DirectoryService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest(classes = [DirectoryService::class])
class DirectoryServiceTest {

    @Autowired
    lateinit var directoryService: DirectoryService

    @Test
    fun testFindExcelFile(): File? {
        val dir = "C:\\UserData\\z004a6nh\\Documents\\OneDrive - Siemens AG\\DE\\DRS O"
        //create a regex pattern to match a file with .xlsx extension and the word categorised in it
        val pattern= "^.*ategor.*\\.xlsx$"
        //case insensitive
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        val file = directoryService.findExcelFile(dir,regex)
        assert(file != null)
        return file
    }

}