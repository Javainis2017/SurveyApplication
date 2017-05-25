package com.javainis.data_export_import.data_converters;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.entities.*;
import org.apache.deltaspike.core.api.future.Futureable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ejb.AsyncResult;
import javax.enterprise.context.Dependent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Dependent
public class XLSXDataImporter implements DataImporter{

    @Override
    @Futureable
    public Future<Survey> importSurvey(File selectedFile, Survey survey) {
        SurveyPage surveyPage = new SurveyPage();
        surveyPage.setNumber(1); // always on first page
        surveyPage.setSurvey(survey);
        List<SurveyPage> surveyPages = new ArrayList<>();
        surveyPages.add(surveyPage);
        survey.setPages(surveyPages);
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(selectedFile));
            XSSFSheet sheet = workbook.getSheet("Survey");
            if (workbook.getSheet("Survey") == null) return new AsyncResult<>(null);
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

            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if (row == null) break;
                if (row.getRowNum() == 0) continue; // header avoid reading
                if (row.getCell(column.get("$questionNumber")).getCellTypeEnum()== CellType.BLANK) break;
                if (row.getCell(column.get("$questionNumber")).getCellTypeEnum()== CellType.STRING){
                    if (row.getCell(column.get("$questionNumber")).getStringCellValue().trim().isEmpty()) break;
                }

                /*if (row.getCell(column.get("$questionType")).getCellTypeEnum() != CellType.STRING && row.getCell(column.get("$question")).getCellTypeEnum() != CellType.STRING && row.getCell(column.get("$mandatory")).getCellTypeEnum() != CellType.STRING && row.getCell(column.get("$questionNumber")).getCellTypeEnum()!= CellType.NUMERIC){
                    return null;
                }*/
                String questionType = row.getCell(column.get("$questionType")).getStringCellValue();
                String questionName = row.getCell(column.get("$question")).getStringCellValue();
                String questionMandatory = row.getCell(column.get("$mandatory")).getStringCellValue();
                double questionNumber = row.getCell(column.get("$questionNumber")).getNumericCellValue();
                if (questionNumber < 1 ) return new AsyncResult<>(null);

                switch (questionType){
                    case "TEXT":
                        FreeTextQuestion freeTextQuestion = new FreeTextQuestion();
                        if (questionMandatory.equals("YES")) freeTextQuestion.setRequired(true);
                        else freeTextQuestion.setRequired(false);

                        freeTextQuestion.setText(questionName);
                        freeTextQuestion.setPosition((int)questionNumber);
                        freeTextQuestion.setPage(surveyPage);
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
                        multipleChoiceQuestion.setPosition((int)questionNumber);
                        multipleChoiceQuestion.setPage(surveyPage);
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
                        singleChoiceQuestion.setPosition((int)questionNumber);
                        singleChoiceQuestion.setPage(surveyPage);
                        singleChoiceQuestion.setText(questionName);
                        singleChoiceQuestion.setSurvey(survey);
                        questions.add(singleChoiceQuestion);
                        break;
                    case "SCALE":
                        IntervalQuestion intervalQuestion = new IntervalQuestion();
                        if (questionMandatory.equals("YES")) intervalQuestion.setRequired(true);
                        else intervalQuestion.setRequired(false);

                        try {
                            if (row.getCell(column.get("$optionsList")).getCellTypeEnum() != CellType.NUMERIC || row.getCell(column.get("$optionsList") + 1).getCellTypeEnum() != CellType.NUMERIC){
                                return new AsyncResult<>(null);
                            }
                            double intervalMin = row.getCell(column.get("$optionsList")).getNumericCellValue();
                            double intervalMax = row.getCell(column.get("$optionsList") +1).getNumericCellValue();


                            if (intervalMin > intervalMax) {
                                double temp = intervalMin;
                                intervalMin = intervalMax;
                                intervalMax = temp;
                            }


                            intervalQuestion.setMin((int) intervalMin);
                            intervalQuestion.setMax((int) intervalMax);
                        } catch (Exception e) {
                            return new AsyncResult<>(null);
                        }

                        intervalQuestion.setText(questionName);
                        intervalQuestion.setPosition((int)questionNumber);
                        intervalQuestion.setPage(surveyPage);
                        intervalQuestion.setSurvey(survey);
                        questions.add(intervalQuestion);
                        break;
                    default:

                        break;
                }
            }
            survey.setQuestions(questions);
        } catch (IOException e) {
            return new AsyncResult<>(null);
        }
        if (validateSurvey(survey)) return new AsyncResult<>(survey);
        else return new AsyncResult<>(null);
    }

    private Boolean validateSurvey(Survey survey){

        if (survey == null) return false;

        for (Question q : survey.getQuestions()){
            if (q.getText().isEmpty()) return false;

            if (q instanceof FreeTextQuestion){

            } else if (q instanceof SingleChoiceQuestion){
                if (((SingleChoiceQuestion) q).getChoices().isEmpty()) return false;
                List<Choice> choiceListSingle = ((SingleChoiceQuestion) q).getChoices();
                for (Choice c : choiceListSingle){
                    int countSameChoices = 0;
                    for (Choice  otherChoice: choiceListSingle){
                        if (c.getText().equals(otherChoice.getText())) countSameChoices++;
                    }
                    if (countSameChoices > 1) return false;
                }
            } else if (q instanceof MultipleChoiceQuestion){
                if (((MultipleChoiceQuestion) q).getChoices().isEmpty()) return false;
                List<Choice> choiceListMulti = ((MultipleChoiceQuestion) q).getChoices();
                for (Choice c : choiceListMulti){
                    int countSameChoices = 0;
                    for (Choice  otherChoice: choiceListMulti){
                        if (c.getText().equals(otherChoice.getText())) countSameChoices++;
                    }
                    if (countSameChoices > 1) return false;
                }
            } else if (q instanceof IntervalQuestion){
                if (((IntervalQuestion) q).getMin() > ((IntervalQuestion) q).getMax()) return false;
            }
        }
        return true;
    }

    @Override
    @Futureable
    public Future<List<SurveyResult>> importAnswers(File selectedFile, Survey survey, List<SurveyResult> surveyResultList) {
        List<Answer> answerList = new ArrayList<>();
        Map<Double, SurveyResult> surveyResultMap = new HashMap<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(selectedFile));
            XSSFSheet sheet = workbook.getSheet("Answer");
            if (workbook.getSheet("Answer") == null) return new AsyncResult<>(null);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> column = new HashMap<String, Integer>();
            int iterator = 0;
            for (Cell cell : headerRow) {
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    column.put(cell.getStringCellValue(), iterator);
                    iterator++;
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum();i++){
                Row row = sheet.getRow(i);
                if (row == null) break;
                if (row.getRowNum() == 0) continue; // header avoid reading
                if (row.getCell(column.get("$answerID")).getCellTypeEnum()== CellType.BLANK) break;
                if (row.getCell(column.get("$answerID")).getCellTypeEnum()== CellType.STRING){
                    if (row.getCell(column.get("$answerID")).getStringCellValue().trim().isEmpty()) break;
                }

                if (row.getCell(column.get("$answerID")).getCellTypeEnum() != CellType.NUMERIC && row.getCell(column.get("$questionNumber")).getCellTypeEnum() != CellType.NUMERIC){
                    return new AsyncResult<>(null);
                }

                double questionNumber = 0;
                if (row.getCell(column.get("$questionNumber")).getCellTypeEnum() == CellType.STRING){
                    questionNumber = Double.parseDouble(row.getCell(column.get("$questionNumber")).getStringCellValue());
                } else if (row.getCell(column.get("$questionNumber")).getCellTypeEnum() == CellType.NUMERIC){
                    questionNumber = row.getCell(column.get("$questionNumber")).getNumericCellValue();
                }

                double answerID = row.getCell(column.get("$answerID")).getNumericCellValue();
                SurveyResult surveyResult;
                if (surveyResultMap.containsKey(answerID)) {
                    surveyResult = surveyResultMap.get(answerID);
                    for (Answer a : surveyResult.getAnswers()){
                        if (a.getQuestion().getPosition() == questionNumber){
                            return null;
                        }
                    }
                }
                else{
                    surveyResult = new SurveyResult();
                    surveyResult.setSurvey(survey);
                    answerList = new ArrayList<>();
                    surveyResult.setAnswers(answerList);
                    surveyResultMap.put(answerID, surveyResult);
                }
                if (survey == null) return new AsyncResult<>(null);
                Question question = survey.getQuestions().get((int)questionNumber - 1);

                if (question instanceof FreeTextQuestion){
                    TextAnswer textAnswer = new TextAnswer();
                    if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.STRING) {
                        textAnswer.setText(row.getCell(column.get("$answer")).getStringCellValue());
                    } else if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.NUMERIC) {
                        textAnswer.setText(String.valueOf(row.getCell(column.get("$answer")).getNumericCellValue()));
                    } else if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.BLANK) {
                        textAnswer.setText("");
                        if (question.getRequired()) return new AsyncResult<>(null);
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
                            if (number < 0 || number > ((MultipleChoiceQuestion) question).getChoices().size()) return new AsyncResult<>(null);
                            if (choices.contains(((MultipleChoiceQuestion) question).getChoices().get((int)number - 1))) return new AsyncResult<>(null);
                            Choice choice = ((MultipleChoiceQuestion) question).getChoices().get((int)number - 1);
                            choices.add(choice);

                        } else if (row.getCell(cell.getColumnIndex()).getCellTypeEnum() == CellType.BLANK) {
                            break;
                        } else {
                            return new AsyncResult<>(null);
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
                        if (number < 0 || number > ((SingleChoiceQuestion) question).getChoices().size()) return new AsyncResult<>(null);
                        singleChoiceAnswer.setChoice(((SingleChoiceQuestion) question).getChoices().get((int)number - 1));
                    } else if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.BLANK) {
                        singleChoiceAnswer.setChoice(null);
                    } else {
                        return new AsyncResult<>(null);
                    }
                    singleChoiceAnswer.setQuestion(question);
                    singleChoiceAnswer.setResult(surveyResult);
                    answerList.add(singleChoiceAnswer);
                } else if (question instanceof IntervalQuestion){
                    NumberAnswer numberAnswer = new NumberAnswer();
                    if (row.getCell(column.get("$answer")).getCellTypeEnum() == CellType.NUMERIC) {
                        double number = row.getCell(column.get("$answer")).getNumericCellValue();

                        if  (number > ((IntervalQuestion) question).getMax() || number < ((IntervalQuestion) question).getMin()) return new AsyncResult<>(null);

                        numberAnswer.setNumber((int)number);


                    } else {
                        return new AsyncResult<>(null);
                    }
                    numberAnswer.setQuestion(question);
                    numberAnswer.setResult(surveyResult);
                    answerList.add(numberAnswer);
                }

            }
            surveyResultList = new ArrayList<SurveyResult>(surveyResultMap.values());
            survey.setSurveyResults(surveyResultList);
        }
        catch (IOException e){
            return new AsyncResult<>(null);
        }
        catch (Exception e){
            return new AsyncResult<>(null);
        }
        if (validateSurveyAnswers(surveyResultList)) return new AsyncResult<>(surveyResultList);
        else return new AsyncResult<>(null);
    }

    private Boolean validateSurveyAnswers(List<SurveyResult> surveyResults){

        if (surveyResults == null || surveyResults.isEmpty()) return false;
        int countMandatoryQuestions = 0;
        for (Question q : surveyResults.get(0).getSurvey().getQuestions()){
            if (q.getRequired()) countMandatoryQuestions++;
        }
        for (SurveyResult sr : surveyResults){
            int countMandatoryAnswers = 0;
            for (Answer a : sr.getAnswers()){

                if(a.getQuestion().getRequired()){
                    countMandatoryAnswers++;
                }

                if (a.getQuestion().getRequired() && a.getResult() == null) return false;
                if (a instanceof TextAnswer){
                    if (a.getQuestion().getRequired() && (((TextAnswer) a).getText().trim().isEmpty())) return false;
                } else if (a instanceof SingleChoiceAnswer) {
                    if (a.getQuestion().getRequired() && ((SingleChoiceAnswer) a).getChoice() == null) return false;
                } else if (a instanceof MultipleChoiceAnswer) {
                    if (a.getQuestion().getRequired() && ((MultipleChoiceAnswer) a).getChoices() == null) return false;
                } else if (a instanceof NumberAnswer){
                    if (a.getQuestion().getRequired() && (((NumberAnswer) a).getNumber() == null)) return false;
                }
            }
            if (countMandatoryQuestions != countMandatoryAnswers){
                return false;
            }
        }
        return true;
    }
}
