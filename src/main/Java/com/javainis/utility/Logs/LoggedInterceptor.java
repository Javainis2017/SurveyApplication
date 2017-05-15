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
            if (invocationContext.getTarget() instanceof NewSurveyController){
                NewSurveyController controller = (NewSurveyController) invocationContext.getTarget();
                Log log = new Log();
                log.setTime(new Timestamp(System.currentTimeMillis()));
                log.setClass_method("class: " + invocationContext.getMethod().getDeclaringClass().getSimpleName() + " method: " + invocationContext.getMethod().getName());
                log.setSurveyName(controller.getSurvey(). getTitle());
                log.setUserName(userController.getUser().getFirstName() + " " + userController.getUser().getLastName());
                log.setRights(userController.getUser().getUserType().getName());
                log.setUserEmail(userController.getUser().getEmail());
                logToDB.saveLog(log);
            }
        }
        return context;
    }
}