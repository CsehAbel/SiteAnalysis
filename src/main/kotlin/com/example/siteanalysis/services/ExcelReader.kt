package com.example.siteanalysis.services

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Color
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.File
import kotlin.experimental.and

@Service
class ExcelReader {

    fun readExcelFile(file: File?): MutableList<Map<String, Any>> {
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

                if(cell == null) continue
                val cellValue = this.getCellValue(cell)
                val dontKeepIt = shouldSkip(cell, cellValue, headerCell, objectProperties)
                if (dontKeepIt){
                    shouldSkipRow = true
                    break
                }




            }

            if (!shouldSkipRow) {
                objectList.add(objectProperties)
            }

        }

        workbook.close()

        return objectList
    }

    private fun shouldSkip(cell: XSSFCell, cellValue: Any?, headerCell: XSSFCell, objectProperties: MutableMap<String, Any>): Boolean {
        var cellColor = getCellBackgroundColor(cell)

        if (!isGrayShade(cellColor)) {
            // If the cell is not gray, then add it to the object properties, possible method name
            addCellToProperties(headerCell, cellValue, objectProperties)
        } else {
            println("Skipping cell with gray background color")
            return true
        }
        return false
    }

    private fun addCellToProperties(headerCell: XSSFCell, cellValue: Any?, objectProperties: MutableMap<String, Any>) {
        if (headerCell != null && cellValue != null) {
            objectProperties[headerCell.stringCellValue] = cellValue
        }
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

    fun getCellBackgroundColor(cell: Cell) : IntArray {
        val cellStyle = cell.cellStyle
        val fillForegroundColorColor = cellStyle.fillForegroundColorColor
        val signedByteArrayOfThree : ByteArray = fillForegroundColorColor.javaClass.getMethod("getRGB").invoke(fillForegroundColorColor) as ByteArray

        val unsignedByteArrayOfThree : IntArray = IntArray(signedByteArrayOfThree.size)
        // convert signed byte array to unsigned byte array
        for (i in signedByteArrayOfThree.indices) {
            val j : Int = signedByteArrayOfThree[i].toInt() and 0xFF
            unsignedByteArrayOfThree[i] = j
        }

        return unsignedByteArrayOfThree
    }

//    fun isGrayShade(colorIndex: Short): Boolean {
//        return colorIndex == IndexedColors.GREY_25_PERCENT.index ||
//                colorIndex == IndexedColors.GREY_40_PERCENT.index ||
//                colorIndex == IndexedColors.GREY_50_PERCENT.index ||
//                colorIndex == IndexedColors.GREY_80_PERCENT.index
//    }
    fun isGrayShade(ia : IntArray): Boolean {
        //check if all pairs of values have a difference of less than 10
        val diff1 = Math.abs(ia[0] - ia[1])
        val diff2 = Math.abs(ia[0] - ia[2])
        val diff3 = Math.abs(ia[1] - ia[2])
        //if its white than return false
        if (ia[0] == 255 && ia[1] == 255 && ia[2] == 255) {
            return false
        }
        if( diff1 < 10 && diff2 < 10 && diff3 < 10) {
            return true
        }
        return false
    }

}