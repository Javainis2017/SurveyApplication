package com.javainis.utility.Logs;

import com.javainis.survey.controllers.create.NewSurveyController;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Timestamp;

@Logged
@Interceptor
public class LoggedInterceptor implements Serializable {

    @Inject
    LogDAO logDAO;

    @AroundInvoke
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
        if (invocationContext.getTarget() instanceof NewSurveyController){
            NewSurveyController controller = (NewSurveyController) invocationContext.getTarget();
            Log log = new Log();
            log.setTime(new Timestamp(System.currentTimeMillis()));
            log.setClass_method("class: " + invocationContext.getMethod().getDeclaringClass().getSimpleName() + " method: " + invocationContext.getMethod().getName());
            log.setSurveyName(controller.getSurvey(). getTitle());
            log.setUserName(controller.getUserController().getUser().getFirstName() + " " + controller.getUserController().getUser().getLastName());
            log.setRights(controller.getUserController().getUser().getUserType().getName());
            log.setSurvey(controller.getSurvey());
            logDAO.createLog(log);
        }
        return invocationContext.proceed();
    }
}