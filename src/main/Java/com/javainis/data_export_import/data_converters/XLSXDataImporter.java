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

            List<Question> questions = new ArrayList<>();

            for (Row row : sheet){
                System.out.println(row.getRowNum());
                if (row.getRowNum() == 0) continue; // header avoid reading

                String questionType = row.getCell(3).getStringCellValue();
                String questionName = row.getCell(2).getStringCellValue();
                String questionMandatory = row.getCell(1).getStringCellValue();
                double questionNumber = row.getCell(0).getNumericCellValue(); //id set?


                switch (questionType){
                    case "TEXT":
                        FreeTextQuestion freeTextQuestion = new FreeTextQuestion();
                        if (questionMandatory.equals("YES")) freeTextQuestion.setRequired(true);
                        else freeTextQuestion.setRequired(false);

                        freeTextQuestion.setText(questionName);
                        freeTextQuestion.setSurvey(survey);
                        questions.add(freeTextQuestion);
                        System.out.println("TEXT");
                        break;
                    case "CHECKBOX":
                        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                        if (questionMandatory.equals("YES")) multipleChoiceQuestion.setRequired(true);
                        else multipleChoiceQuestion.setRequired(false);

                        List<Choice> choiceListMulti = new ArrayList<>();
                        for (Cell cell : row){
                            if(cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() ==  2 || cell.getColumnIndex() == 3) continue;
                            Choice choice = new Choice();

                            if (cell.getCellTypeEnum() == CellType.STRING){
                                String option = cell.getStringCellValue();
                                choice.setText(option);
                            }
                            else if (cell.getCellTypeEnum() == CellType.NUMERIC){
                                double optionNumber = cell.getNumericCellValue();
                                choice.setText(String.valueOf(optionNumber));
                            } else if (cell.getCellTypeEnum() == CellType.BLANK){
                                break;
                            } else {
                                System.out.println("Gali būti kazkoks kitas formatas???"); // ?
                            }
                            choice.setQuestion(multipleChoiceQuestion);
                            choiceListMulti.add(choice);
                        }
                        multipleChoiceQuestion.setChoices(choiceListMulti);
                        multipleChoiceQuestion.setText(questionName);
                        multipleChoiceQuestion.setSurvey(survey);
                        questions.add(multipleChoiceQuestion);
                        System.out.println("CHECKBOX");
                        break;
                    case "MULTIPLECHOICE":
                        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();
                        if (questionMandatory.equals("YES")) singleChoiceQuestion.setRequired(true);
                        else singleChoiceQuestion.setRequired(false);

                        List<Choice> choiceListSingle = new ArrayList<>();
                        for (Cell cell : row){
                            if(cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() ==  2 || cell.getColumnIndex() == 3) continue;

                            Choice choice = new Choice();
                            if (cell.getCellTypeEnum() == CellType.STRING){
                                String option = cell.getStringCellValue();
                                choice.setText(option);
                            }
                            else if (cell.getCellTypeEnum() == CellType.NUMERIC){
                                double optionNumber = cell.getNumericCellValue();
                                choice.setText(String.valueOf(optionNumber));
                            } else if (cell.getCellTypeEnum() == CellType.BLANK){
                                break;
                            } else {
                                System.out.println("Gali būti kazkoks kitas formatas??"); // ?
                                System.out.println(cell.getCellTypeEnum());
                            }

                            choice.setQuestion(singleChoiceQuestion);
                            choiceListSingle.add(choice);
                        }
                        singleChoiceQuestion.setChoices(choiceListSingle);
                        singleChoiceQuestion.setText(questionName);
                        singleChoiceQuestion.setSurvey(survey);
                        questions.add(singleChoiceQuestion);
                        System.out.println("MULTIPLECHOICE");
                        break;
                    case "SCALE":
                        IntervalQuestion intervalQuestion = new IntervalQuestion();
                        if (questionMandatory.equals("YES")) intervalQuestion.setRequired(true);
                        else intervalQuestion.setRequired(false);

                        //try ?
                        double intervalMin = row.getCell(4).getNumericCellValue();
                        double intervalMax = row.getCell(5).getNumericCellValue();

                        intervalQuestion.setMin((int) intervalMin);
                        intervalQuestion.setMax((int) intervalMax);

                        intervalQuestion.setText(questionName);
                        intervalQuestion.setSurvey(survey);
                        questions.add(intervalQuestion);

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

            survey.setQuestions(questions);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return survey;
    }

    @Override
    public List<Answer> importAnswers(File selectedFile) {
        return null;
    }
}
