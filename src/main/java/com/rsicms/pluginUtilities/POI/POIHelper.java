package com.rsicms.pluginUtilities.POI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Helper class for using the Apache POI API for accessing/writing Office
 * documents. You might find this handy:
 * http://poi.apache.org/spreadsheet/quick-guide.html
 */
public class POIHelper {

    private static Log log = LogFactory.getLog(POIHelper.class);

    /**
     * Find the first cell in top-bottom, left-right order that matches the
     * specified pattern.
     * 
     * @param sheet
     *            Spreadsheet to search.
     * @param regex
     *            Regular expression to match cells agains.
     * @return Cell that matches, or null if not found
     */
    public static Cell findCellMatching(Sheet sheet, String regex) {
        for (Iterator<Row> iter = sheet.rowIterator(); iter.hasNext();) {
            Row row = iter.next();
            for (Iterator<Cell> cIter = row.cellIterator(); cIter.hasNext();) {
                Cell cell = cIter.next();
                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    String text = cell.getStringCellValue();
                    if (Pattern.matches(regex, text)) {
                        return cell;
                    }
                    break;
                default:
                    // Not a text cell, skip
                }
            }
        }
        return null;
    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";
        String result = null;
        DataFormatter dataFormatter = new DataFormatter();
        result = dataFormatter.formatCellValue(cell);
        return result;
    }

    /**
     * Return the value as a Java Date, if it is a date format.
     * 
     * @param cell
     *            The cell that you think is a date.
     * @return The Date value or null if it's not a date-valued cell.
     */
    public static Date getCellValueAsDate(Cell cell) {
        if (HSSFDateUtil.isCellDateFormatted(cell)) {
            Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
            return date;
        }
        return null;
    }

    public static Sheet createNewWorkbookAndSheet(String sheetTitle) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(sheetTitle);
        return sheet;
    }

    public static Row addRow(Sheet sheet, Short rowNum) {
        Row row = sheet.createRow((short) rowNum);
        return row;
    }

    public static Cell addCell(Sheet sheet, Row row, Short cellNum, String cellValue, Boolean isHeader) {
        Cell cell = addCell(sheet, row, cellNum, cellValue, isHeader, null);
        return cell;
    }

    public static Cell addCell(Sheet sheet, Row row, Short cellNum, String cellValue, Boolean isHeader, String cellType) {
        Cell cell = row.createCell(cellNum);
        CellStyle style = null;
        if (isHeader) {
        	style = getHeaderCellStyle(sheet);
        } else {
        	style = getBaseCellStyle(sheet);
        }
        if (cellType != null) {
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat(cellType));
        }

        if (cellValue.equals("")) {
            cell.setCellValue("");
        } else if (cellType != null && cellType.equals("0")) {
            cell.setCellValue(Integer.parseInt(cellValue));
        } else if (cellType != null && cellType.contains("yy")) {
            CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
            style.setDataFormat(createHelper.createDataFormat().getFormat(cellType));
            //using Excel format will map to SimpleDateFormat for many but not all date formats
            SimpleDateFormat dt = new SimpleDateFormat(cellType);
            try {
                Date thisDate = dt.parse(cellValue);
                cell.setCellValue(thisDate);
            } catch (ParseException e) {
                log.error("Date format could not be parsed. Outputting with General format. " + cellType + " " + cellValue);
                cell.setCellValue(cellValue);
            }    
        } else {
            cell.setCellValue(cellValue);
        }
        cell.setCellStyle(style);
        return cell;
    }

    public static CellStyle getHeaderCellStyle(Sheet sheet) {
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        style.setBorderBottom(CellStyle.BORDER_THICK);
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
    }
    
    public static CellStyle getBaseCellStyle(Sheet sheet) {
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        return style;
    }
    
    public static Row addRowWithCells(Sheet sheet, Short rowNum, Map<Short, String> cellValues, Boolean isHeader) {
        Row row = addRowWithCells(sheet, rowNum, cellValues, isHeader, null);
        return row;
    }

    public static Row addRowWithCells(Sheet sheet, Short rowNum, Map<Short, String> cellValues, Boolean isHeader, Map<Short, String> cellTypes) {
        Row row = sheet.createRow((short) rowNum);
        for (Short key : cellValues.keySet()) {
            String cellType = null;
            if (cellTypes != null) {
                cellType = cellTypes.get(key);
            }
            if (cellValues.get(key).startsWith("http")) {
                makeLinkCell(sheet.getWorkbook(), row.createCell(key), cellValues.get(key));
            } else {
                addCell(sheet, row, key, cellValues.get(key), isHeader, cellType);
            }
        }
        return row;
    }

    public static String saveWorkbookToFile(Workbook wb, File tempDir) throws IOException {
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            sheet.autoSizeColumn((short) i);
            //every now and then the autowidth isn't quite wide enough; this gives a little and also pads a little
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            if (sheet.getColumnWidth(i) < 500) {
            	//Don't know why text columns autosetting to 0 or close to it, this quick workaround
            	sheet.setColumnWidth(i, 6000);
            } else if (sheet.getColumnWidth(i) > 20000) {
                sheet.setColumnWidth(i, 20000);
            }
        }

        String tempFile = tempDir.getAbsolutePath() + "/" + "workbook.xlsx";
        FileOutputStream fileOut = new FileOutputStream(tempFile);
        wb.write(fileOut);
        fileOut.close();
        return tempFile;
    }

    public static void makeLinkCell(Workbook wb, Cell cell, String cellValue) {
        String[] values = cellValue.split("%%");
        String linkUrl = "";
        String value = "";
        if (values.length > 0) linkUrl = values[0];
        if (values.length > 1) value = values[1];
        if (value.isEmpty()) value = "View";
        
    	CreationHelper createHelper = wb.getCreationHelper();
        CellStyle hlink_style = wb.createCellStyle();
        hlink_style.setWrapText(true);
        hlink_style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        Font hlink_font = wb.createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
        hlink_style.setFont(hlink_font);
        cell.setCellValue(value);
        Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
        link.setAddress(linkUrl);
        cell.setHyperlink(link);
        cell.setCellStyle(hlink_style);
    }

}
