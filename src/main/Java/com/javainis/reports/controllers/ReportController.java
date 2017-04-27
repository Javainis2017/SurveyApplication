package com.javainis.reports.controllers;


import com.javainis.user_management.controllers.UserController;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;

@ViewScoped
public class ReportController {
    @Inject
    private UserController userController;

}
