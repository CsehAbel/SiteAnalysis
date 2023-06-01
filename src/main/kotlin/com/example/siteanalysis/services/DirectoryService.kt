import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.File


@Component
class DirectoryService{

    //example usage C:\UserData\z004a6nh\Documents\OneDrive - Siemens AG\DE\DRS O
    fun findExcelFile(directory: String, pattern: Regex): File? {
        // TODO: Implement directory scanning and file matching logic
        // Use the 'directory' parameter to access the specified directory
        // Use the 'pattern' parameter to match against file names
        val file = File(directory)
        val files = file.listFiles()
        for (f in files) {
            if(f.isFile && pattern.matches(f.name)) {
                return f
            }
        }

        // TODO: Return the matched Excel file or null if not found
        return null
    }

    fun processExcelFile(file: File) {
        // TODO: Implement further processing logic for the Excel file
        // Use the 'file' parameter to access the matched Excel file
    }

}