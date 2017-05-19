package com.javainis.utility.Logs;

import com.javainis.survey.controllers.create.NewSurveyController;
import com.javainis.user_management.controllers.UserController;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.sql.Timestamp;

@Logged
@Interceptor
public class LoggedInterceptor implements Serializable {

    @Inject
    UserController userController;

    @Inject
    LogToDB logToDB;

    @AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
        Object context;
        try{
            context = invocationContext.proceed();
        } finally {
            Log log = new Log();
            log.setTime(new Timestamp(System.currentTimeMillis()));
            log.setUserName(userController.getUser().getFirstName() + " " + userController.getUser().getLastName());
            log.setClass_name(invocationContext.getMethod().getDeclaringClass().getSimpleName());
            log.setMethod_name(invocationContext.getMethod().getName());
            log.setRights(userController.getUser().getUserType().getName());
            log.setUserEmail(userController.getUser().getEmail());
            logToDB.saveLog(log);
        }
        return context;
    }
}