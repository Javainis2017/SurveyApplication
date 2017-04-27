package com.javainis.reports.controllers;


import com.javainis.survey.controllers.create.IntervalQuestionController;
import com.javainis.survey.controllers.create.TextQuestionController;
import com.javainis.user_management.controllers.UserController;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;

@ViewScoped
public class ReportController {
    @Inject
    private UserController userController;
    @Inject
    private TextQuestionController textQuestionController;
    @Inject
    private IntervalQuestionController intervalQuestionController;

}
