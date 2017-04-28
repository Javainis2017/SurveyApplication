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
import java.util.Iterator;
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
                double quesstionNumber = row.getCell(0).getNumericCellValue();

                switch (questionType){
                    case "TEXT":
                    case "TEXT*":
                        FreeTextQuestion freeTextQuestion = new FreeTextQuestion();
                        if (questionType.contains("*")) freeTextQuestion.setRequired(true);
                        else freeTextQuestion.setRequired(false);

                        freeTextQuestion.setSurvey(survey); //need this?
                        //survey.setQuestions();
                        System.out.println("TEXT");
                        break;
                    case "CHECKBOX":
                    case "CHECKBOX*":
                        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                        if (questionType.contains("*")) multipleChoiceQuestion.setRequired(true);
                        else multipleChoiceQuestion.setRequired(false);

                        List<Choice> choiceList = new ArrayList<>();
                        for (Cell cell : row){
                            if(cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() == 2) continue;
                            Choice choice = new Choice();

                            if (cell.getCellTypeEnum() == CellType.STRING){
                                String option = cell.getStringCellValue();
                                choice.setText(option);
                            }
                            if (cell.getCellTypeEnum() == CellType.NUMERIC){
                                double optionNumber = cell.getNumericCellValue();
                                choice.setText(String.valueOf(optionNumber));
                            } else{
                                System.out.println("Gali būti kazkoks kitas formatas???");
                            }
                            choice.setQuestion(multipleChoiceQuestion); //taip ar ne?
                            choiceList.add(choice);
                        }
                        multipleChoiceQuestion.setChoices(choiceList);
                        multipleChoiceQuestion.setText(quesstionName);
                        multipleChoiceQuestion.setSurvey(survey); //need this?

                        System.out.println("CHECKBOX");
                        break;
                    case "MULTIPLECHOICE":
                    case "MULTIPLECHOICE*":
                        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();
                        if (questionType.contains("*")) singleChoiceQuestion.setRequired(true);
                        else singleChoiceQuestion.setRequired(false);
                        System.out.println("MULTIPLECHOICE");
                        break;
                    case "SCALE":
                    case "SCALE*":
                        IntervalQuestion intervalQuestion = new IntervalQuestion();
                        if (questionType.contains("*")) intervalQuestion.setRequired(true);
                        else intervalQuestion.setRequired(false);
                        System.out.println("SCALE");

                        break;
                }


                for (Cell cell : row){
                    System.out.print(cell.getColumnIndex() + ". ");
                    if(cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() == 2) continue; // only option


                    if (cell.getCellTypeEnum() == CellType.STRING) System.out.print(cell.getStringCellValue());
                    if (cell.getCellTypeEnum() == CellType.NUMERIC) System.out.print(cell.getNumericCellValue());

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
