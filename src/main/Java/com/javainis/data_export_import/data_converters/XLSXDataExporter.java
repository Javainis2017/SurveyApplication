package com.javainis.data_export_import.data_converters;

import com.javainis.data_export_import.interfaces.DataExporter;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.*;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.hibernate.Hibernate;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dependent
public class XLSXDataExporter implements DataExporter, Serializable{

    @Inject
    private SurveyResultDAO surveyResultDAO;

    //Isimena kurioj Excel dokumento eiluteje dabar yra
    private int answerRowNumber = 0;

    //Reikia kad isimintu koks answer questionNumber
    private Map<Question, Integer> questionNumberMap;

    //Reikia kad isimintu koks answer choice numeris (Single ir multi choice klausimams)
    private Map<Choice, Integer> choiceNumberMap;

    private void exportChoices(XSSFRow row, List<Choice> choices)
    {
        int cell = 4;
        for(int i = 0; i < choices.size(); i++)
        {
            Choice c = choices.get(i);
            choiceNumberMap.put(c, i+1);
            row.createCell(cell++).setCellValue(c.getText());
        }
    }

    private void exportInterval(XSSFRow row, int min, int max)
    {
        row.createCell(4).setCellValue(min);
        row.createCell(5).setCellValue(max);
    }

    private void createStatusRow(XSSFSheet sheet)
    {
        XSSFRow statusRow = sheet.createRow(0);
        statusRow.createCell(0).setCellValue("$questionNumber");
        statusRow.createCell(1).setCellValue("$mandatory");
        statusRow.createCell(2).setCellValue("$question");
        statusRow.createCell(3).setCellValue("$questionType");
        statusRow.createCell(4).setCellValue("$optionsList");
    }

    @Override
    public void exportSurvey(Survey survey, OutputStream destination)
    {
        System.out.println("Exporting survey");
        XSSFWorkbook wb = new XSSFWorkbook();
        exportSurveyQuestions(survey, wb);
        List<SurveyResult> results = surveyResultDAO.getResultsBySurveyId(survey.getId());
        if (results != null && results.size() != 0)
        {
            exportAnswers(results, wb);
        }
        try {
            wb.write(destination);
            System.out.println("Export finished");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
            answerRowNumber = 0;
        }
//        return new AsyncResult<>(null);
    }

    @Override
    public void exportAnswers(List<SurveyResult> answers, OutputStream destination)
    {
        if (questionNumberMap == null && choiceNumberMap == null)
        {
            return;
        }
        XSSFWorkbook wb = new XSSFWorkbook();
        exportAnswers(answers, wb);
        try {
            wb.write(destination);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void exportSurveyQuestions(Survey survey, XSSFWorkbook wb)
    {

        XSSFSheet surveySheet = wb.createSheet("Survey");
        createStatusRow(surveySheet);

        List<Question> surveyQuestions = survey.getQuestions();
        questionNumberMap = new HashMap<>();
        choiceNumberMap = new HashMap<>();
        for(int i = 0; i < surveyQuestions.size(); i++)
        {
            String questionType;
            Question question = surveyQuestions.get(i);
            questionNumberMap.put(question, i+1);
            XSSFRow row = surveySheet.createRow(i+1);

            //Question number
            row.createCell(0).setCellValue(i+1);
            row.createCell(2).setCellValue(question.getText());
            XSSFCell requiredCell = row.createCell(1);
            if (question.getRequired())
            {
                requiredCell.setCellValue("YES");
            }
            else {
                requiredCell.setCellValue("NO");
            }

            if (question instanceof MultipleChoiceQuestion)
            {
                questionType = "CHECKBOX";
                exportChoices(row, ((MultipleChoiceQuestion) question).getChoices());
            }
            else if (question instanceof SingleChoiceQuestion)
            {
                questionType = "MULTIPLECHOICE";
                exportChoices(row, ((SingleChoiceQuestion) question).getChoices());
            }
            else if (question instanceof IntervalQuestion)
            {
                questionType = "SCALE";
                IntervalQuestion iq = (IntervalQuestion) question;
                exportInterval(row, iq.getMin(), iq.getMax());
            }
            else{
                questionType = "TEXT";
            }
            row.createCell(3).setCellValue(questionType);
        }
    }

    private void exportAnswers(List<SurveyResult> answers, XSSFWorkbook wb)
    {
        XSSFSheet surveySheet = wb.createSheet("Answer");
        XSSFRow statusRow = surveySheet.createRow(answerRowNumber++);
        statusRow.createCell(0).setCellValue("$answerID");
        statusRow.createCell(1).setCellValue("$questionNumber");
        statusRow.createCell(2).setCellValue("$answer");

        int answerId = 1;
        for(SurveyResult result : answers)
        {
            exportSingleAnswer(result.getAnswers(), answerId++, surveySheet);
        }
    }

    private void exportSingleAnswer(List<Answer> answers, int answerId, XSSFSheet sheet)
    {
        int question = 1;
        for(Answer answer: answers)
        {
            XSSFRow answerRow = sheet.createRow(answerRowNumber++);

            //answerID
            answerRow.createCell(0).setCellValue(answerId);

            //questionNumber
            answerRow.createCell(1).setCellValue(questionNumberMap.get(answer.getQuestion()));
            if (answer instanceof TextAnswer)
            {
                String text = ((TextAnswer) answer).getText();
                answerRow.createCell(2).setCellValue(text);
            }
            else if (answer instanceof SingleChoiceAnswer)
            {
                int choiceNum = choiceNumberMap.get(((SingleChoiceAnswer) answer).getChoice());
                answerRow.createCell(2).setCellValue(choiceNum);
            }
            else if (answer instanceof MultipleChoiceAnswer)
            {
                List<Choice> choices = ((MultipleChoiceAnswer) answer).getChoices();
                for(int i = 0; i < choices.size(); i++)
                {
                    int choiceNum = choiceNumberMap.get(choices.get(i));
                    answerRow.createCell(i+2).setCellValue(choiceNum);
                }
            }
            else
            {
                int min = ((NumberAnswer) answer).getNumber();
                answerRow.createCell(2).setCellValue(min);
            }
        }
    }

}
