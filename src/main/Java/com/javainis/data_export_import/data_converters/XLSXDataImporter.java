package com.javainis.data_export_import.data_converters;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.entities.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.enterprise.context.Dependent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Dependent
public class XLSXDataImporter implements DataImporter{

    private File file;

    @Override
    public Survey importSurvey(File selectedFile) {
        Survey survey = new Survey();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(selectedFile));
            XSSFSheet sheet = workbook.getSheet("Survey");
            if (workbook.getSheet("Survey") == null) return null;
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> column = new HashMap<String, Integer>();
            int iterator = 0;
            for (Cell cell : headerRow){
                if (cell.getCellTypeEnum() == CellType.STRING){
                    column.put(cell.getStringCellValue(), iterator);
                    iterator++;
                }
            }

            List<Question> questions = new ArrayList<>();

            for (Row row : sheet){
                if (row.getRowNum() == 0) continue; // header avoid reading

                if (row.getCell(column.get("$questionNumber")).getCellTypeEnum()== CellType.BLANK) break;
                if (row.getCell(column.get("$questionNumber")).getCellTypeEnum()== CellType.STRING){
                    if (row.getCell(column.get("$questionNumber")).getStringCellValue().trim().equals("")) break;
                }

                if (row.getCell(column.get("$questionType")).getCellTypeEnum() != CellType.STRING && row.getCell(column.get("$question")).getCellTypeEnum() != CellType.STRING && row.getCell(column.get("$mandatory")).getCellTypeEnum() != CellType.STRING && row.getCell(column.get("$questionNumber")).getCellTypeEnum()!= CellType.NUMERIC){
                    return null;
                }
                String questionType = row.getCell(column.get("$questionType")).getStringCellValue();
                String questionName = row.getCell(column.get("$question")).getStringCellValue();
                String questionMandatory = row.getCell(column.get("$mandatory")).getStringCellValue();
                double questionNumber = row.getCell(column.get("$questionNumber")).getNumericCellValue();


                switch (questionType){
                    case "TEXT":
                        FreeTextQuestion freeTextQuestion = new FreeTextQuestion();
                        if (questionMandatory.equals("YES")) freeTextQuestion.setRequired(true);
                        else freeTextQuestion.setRequired(false);

                        freeTextQuestion.setText(questionName);
                        freeTextQuestion.setSurvey(survey);
                        questions.add(freeTextQuestion);
                        break;
                    case "CHECKBOX":
                        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                        if (questionMandatory.equals("YES")) multipleChoiceQuestion.setRequired(true);
                        else multipleChoiceQuestion.setRequired(false);

                        List<Choice> choiceListMulti = new ArrayList<>();
                        for (Cell cell : row){
                            if(cell.getColumnIndex() == column.get("$questionNumber") || cell.getColumnIndex() == column.get("$mandatory") || cell.getColumnIndex() ==  column.get("$question") || cell.getColumnIndex() == column.get("$questionType")) continue;
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
                                break;
                            }
                            choice.setQuestion(multipleChoiceQuestion);
                            choiceListMulti.add(choice);
                        }
                        multipleChoiceQuestion.setChoices(choiceListMulti);
                        multipleChoiceQuestion.setText(questionName);
                        multipleChoiceQuestion.setSurvey(survey);
                        questions.add(multipleChoiceQuestion);
                        break;
                    case "MULTIPLECHOICE":
                        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();
                        if (questionMandatory.equals("YES")) singleChoiceQuestion.setRequired(true);
                        else singleChoiceQuestion.setRequired(false);

                        List<Choice> choiceListSingle = new ArrayList<>();
                        for (Cell cell : row){
                            if(cell.getColumnIndex() == column.get("$questionNumber") || cell.getColumnIndex() == column.get("$mandatory") || cell.getColumnIndex() ==  column.get("$question") || cell.getColumnIndex() == column.get("$questionType")) continue;                            Choice choice = new Choice();
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
                                break;
                            }
                            choice.setQuestion(singleChoiceQuestion);
                            choiceListSingle.add(choice);
                        }
                        singleChoiceQuestion.setChoices(choiceListSingle);
                        singleChoiceQuestion.setText(questionName);
                        singleChoiceQuestion.setSurvey(survey);
                        questions.add(singleChoiceQuestion);
                        break;
                    case "SCALE":
                        IntervalQuestion intervalQuestion = new IntervalQuestion();
                        if (questionMandatory.equals("YES")) intervalQuestion.setRequired(true);
                        else intervalQuestion.setRequired(false);

                        try {
                            double intervalMin = row.getCell(4).getNumericCellValue();
                            double intervalMax = row.getCell(5).getNumericCellValue();


                            if (intervalMin > intervalMax) {
                                double temp = intervalMin;
                                intervalMin = intervalMax;
                                intervalMax = temp;
                            }


                            intervalQuestion.setMin((int) intervalMin);
                            intervalQuestion.setMax((int) intervalMax);
                        } catch (Exception e) {
                            return null;
                        }

                        intervalQuestion.setText(questionName);
                        intervalQuestion.setSurvey(survey);
                        questions.add(intervalQuestion);
                        break;
                }
            }
            survey.setQuestions(questions);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (validateSurvey(survey)) return survey;
        else return null;
    }

    private Boolean validateSurvey(Survey survey){

        if (survey == null) return false;

        for (Question q : survey.getQuestions()){
            if (q.getText().equals("")) return false;

            if (q instanceof FreeTextQuestion){

            } else if (q instanceof SingleChoiceQuestion){
                if (((SingleChoiceQuestion) q).getChoices().isEmpty()) return false;
            } else if (q instanceof MultipleChoiceQuestion){
                if (((MultipleChoiceQuestion) q).getChoices().isEmpty()) return false;
            } else if (q instanceof IntervalQuestion){
                if (((IntervalQuestion) q).getMin() > ((IntervalQuestion) q).getMax()) return false;
            }


        }

        return true;
    }

    @Override
    public List<SurveyResult> importAnswers(File selectedFile, Survey survey) {
        List<SurveyResult> surveyResultList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(selectedFile));
            XSSFSheet sheet = workbook.getSheet("Answer");
            if (workbook.getSheet("Answer") == null) return null;
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> column = new HashMap<String, Integer>();
            int iterator = 0;
            for (Cell cell : headerRow) {
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    column.put(cell.getStringCellValue(), iterator);
                    iterator++;
                }
            }
            List<Answer> answerList = new ArrayList<>();
            SurveyResult surveyResult = new SurveyResult();

            surveyResult.setSurvey(survey);
            double oldAnswerID = -1;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // header avoid reading
                if (row.getCell(column.get("$answerID")).getCellTypeEnum()== CellType.BLANK) break;
                if (row.getCell(column.get("$answerID")).getCellTypeEnum()== CellType.STRING){
                    if (row.getCell(column.get("$answerID")).getStringCellValue().trim().equals("")) break;
                }

                if (row.getCell(column.get("$answerID")).getCellTypeEnum() != CellType.NUMERIC && row.getCell(column.get("$questionNumber")).getCellTypeEnum() != CellType.NUMERIC){
                    return null;
                }

                double questionNumber = 0;
                if (row.getCell(column.get("$questionNumber")).getCellTypeEnum() == CellType.STRING){
                    questionNumber = Double.parseDouble(row.getCell(column.get("$questionNumber")).getStringCellValue());
                } else if (row.getCell(column.get("$questionNumber")).getCellTypeEnum() == CellType.NUMERIC){
                    questionNumber = row.getCell(column.get("$questionNumber")).getNumericCellValue();
                }
                double answerID = row.getCell(column.get("$answerID")).getNumericCellValue();
                if (answerID != oldAnswerID){
                    if (oldAnswerID != -1){
                        surveyResult.setAnswers(answerList);
                        surveyResult.setSurvey(survey);
                        surveyResultList.add(surveyResult);
                    }
                    surveyResult.setAnswers(null);
                    surveyResult = new SurveyResult(); //naujas survey result kitam answer id
                    surveyResult.setSurvey(survey);
                    oldAnswerID = answerID;
                }

                Question question = survey.getQuestions().get((int)questionNumber - 1);

                if (question instanceof FreeTextQuestion){
                    TextAnswer textAnswer = new TextAnswer();
                    if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.STRING) {
                        textAnswer.setText(row.getCell(column.get("$answer")).getStringCellValue());
                    } else if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.NUMERIC) {
                        textAnswer.setText(String.valueOf(row.getCell(column.get("$answer")).getNumericCellValue()));
                    } else if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.BLANK) {
                        textAnswer.setText("");
                        if (question.getRequired()) return null;
                    }
                    textAnswer.setQuestion(question);
                    textAnswer.setResult(surveyResult);
                    answerList.add(textAnswer);
                } else if (question instanceof MultipleChoiceQuestion){
                    MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer();
                    List<Choice> choices = new ArrayList<>();
                    for (Cell cell : row) {
                        if (cell.getColumnIndex() == column.get("$answerID") || cell.getColumnIndex() == column.get("$questionNumber")) continue;
                        if (row.getCell(cell.getColumnIndex()).getCellTypeEnum() == CellType.NUMERIC) {
                            double number = row.getCell(cell.getColumnIndex()).getNumericCellValue();
                            if (number < 0 || number > ((MultipleChoiceQuestion) question).getChoices().size()) return null;
                            Choice choice = ((MultipleChoiceQuestion) question).getChoices().get((int)number - 1);
                            choices.add(choice);

                        } else if (row.getCell(cell.getColumnIndex()).getCellTypeEnum() == CellType.BLANK) {
                            break;
                        }
                    }
                    multipleChoiceAnswer.setChoices(choices);
                    multipleChoiceAnswer.setQuestion(question);
                    multipleChoiceAnswer.setResult(surveyResult);
                    answerList.add(multipleChoiceAnswer);
                } else if (question instanceof SingleChoiceQuestion){
                    SingleChoiceAnswer singleChoiceAnswer = new SingleChoiceAnswer();
                    if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.NUMERIC) {
                        double number = row.getCell(column.get("$answer")).getNumericCellValue();
                        if (number < 0 || number > ((SingleChoiceQuestion) question).getChoices().size()) return null;
                        singleChoiceAnswer.setChoice(((SingleChoiceQuestion) question).getChoices().get((int)number - 1));
                    } else if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.BLANK) {
                        singleChoiceAnswer.setChoice(null); //tikrai null?
                    }
                    singleChoiceAnswer.setQuestion(question);
                    singleChoiceAnswer.setResult(surveyResult);
                    answerList.add(singleChoiceAnswer);
                } else if (question instanceof IntervalQuestion){
                    NumberAnswer numberAnswer = new NumberAnswer();
                    if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.NUMERIC) {
                        double number = row.getCell(column.get("$answer")).getNumericCellValue();
                        numberAnswer.setNumber((int)number);

                        if  (number > ((IntervalQuestion) question).getMax() || number < ((IntervalQuestion) question).getMin()) return null;

                    } else if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.BLANK) {
                        numberAnswer.setNumber(null);
                    }
                    numberAnswer.setQuestion(question);
                    numberAnswer.setResult(surveyResult);
                    answerList.add(numberAnswer);
                }

            }
            surveyResult.setAnswers(answerList);
            surveyResult.setSurvey(survey);
            surveyResultList.add(surveyResult);
            survey.setSurveyResults(surveyResultList);

        }
        catch (IOException e){
            return null;
        }
        catch (Exception e){
            return null;
        }

        if (validateSurveyAnswers(surveyResultList)) return surveyResultList;
        else return null;
    }

    private Boolean validateSurveyAnswers(List<SurveyResult> surveyResults){

        if (surveyResults == null) return false;

        /*
        for (SurveyResult sr : surveyResults){
            System.out.println(sr.getAnswers() + " da?");
            for (Answer a : sr.getAnswers()){
                if (a instanceof TextAnswer){
                    System.out.println(a.getQuestion() + "*");
                    System.out.println(a.getResult() + "***");
                    if (a.getQuestion().getRequired() && a.getResult() == null) return false;
                } /*else if (a instanceof SingleChoiceAnswer) {
                    if (a.getQuestion().getRequired() && a.getResult() == null) return false;
                } else if (a instanceof MultipleChoiceAnswer) {
                    if (a.getQuestion().getRequired() && a.getResult() == null) return false;
                } else if (a instanceof NumberAnswer){
                    if (a.getQuestion().getRequired() && a.getResult() == null) return false;
                }
            }
        }
        */
        return true;
    }
}
