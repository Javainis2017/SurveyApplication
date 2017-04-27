package com.javainis.data_export_import.data_converters;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.entities.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ignas on 2017-04-26.
 */
@ApplicationScoped
public class XLSXDataImporter implements DataImporter{

    private File file;

    @Override
    public Survey importSurvey(File selectedFile) {
        Survey survey = new Survey();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(selectedFile));
            XSSFSheet sheet = workbook.getSheetAt(0); //reiks tobulint
//            XSSFRow row = sheet.getRow(0);
//            String data = row.getCell(0).getStringCellValue();
//            System.out.println(data);
//            row = sheet.getRow(1);
//            double number = row.getCell(0).getNumericCellValue();
            Row headerRow = sheet.getRow(0);
            List<String> headerColumn = new ArrayList<>();
            for (Cell cell : headerRow){
                if (cell.getCellTypeEnum() == CellType.STRING){
                    headerColumn.add(cell.getStringCellValue());
                }
            }

            for (Row row : sheet){
                System.out.println(row.getRowNum());
                if (row.getRowNum() == 0) continue; // header avoid reading

                String questionType = row.getCell(2).getStringCellValue(); // Question format
                String quesstionName = row.getCell(1).getStringCellValue();
                String quesstionNumber = row.getCell(0).getStringCellValue();

                switch (questionType){
                    case "TEXT":
                    case "TEXT*":
                        FreeTextQuestion freeTextQuestion = new FreeTextQuestion();
                        if (questionType.contains("*")) freeTextQuestion.setRequired(true);
                        //freeTextQuestion.

                        break;
                    case "CHECKBOX":
                    case "CHECKBOX*":
                        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();

                        break;
                    case "MULTIPLECHOICE":
                    case "MULTIPLECHOICE*":
                        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();

                        break;
                    case "SCALE":
                    case "SCALE*":
                        IntervalQuestion intervalQuestion = new IntervalQuestion();
                        break;
                }


                for (Cell cell : row){
                    System.out.print(cell.getColumnIndex() + ". ");



                    if (cell.getCellTypeEnum() == CellType.STRING) System.out.print(cell.getStringCellValue());
                    if (cell.getCellTypeEnum() == CellType.NUMERIC) System.out.print(cell.getNumericCellValue());
                    if (cell.getCellTypeEnum() == CellType.STRING) {

                    }
                }
                System.out.println();
            }

            //for (int i = 0; i<)


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Answer> importAnswers(File selectedFile) {
        return null;
    }
}
