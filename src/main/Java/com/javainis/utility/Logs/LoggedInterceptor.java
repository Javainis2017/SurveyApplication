package com.javainis.utility.Logs;

import com.javainis.survey.controllers.create.NewSurveyController;
import com.javainis.survey.entities.Survey;
import com.sun.javafx.collections.MappingChange;

import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Logged
@Interceptor
public class LoggedInterceptor implements Serializable {

    @Inject
    LogsDAO logsDAO;

    @AroundInvoke
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
        if (invocationContext.getTarget() instanceof NewSurveyController){
            NewSurveyController controller = (NewSurveyController) invocationContext.getTarget();
            Logs log = new Logs();
            log.setTime(new Timestamp(System.currentTimeMillis()));
            log.setClass_method("class: " + invocationContext.getMethod().getDeclaringClass().getSimpleName() + " method: " + invocationContext.getMethod().getName());
            log.setSurveyName(controller.getSurvey(). getTitle());
            log.setUserName(controller.getUserController().getUser().getFirstName() + " " + controller.getUserController().getUser().getLastName());
            log.setRights(controller.getUserController().getUser().getUserType().getName());
            logsDAO.createLog(log);
        }
        return invocationContext.proceed();
    }
}