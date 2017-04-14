package com.javainis;

import com.javainis.ejb.MailSenderBean;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "MailDispatcherServlet", urlPatterns = {"/MailDispatcherServlet"})
public class MailDispatcherServlet extends HttpServlet
{
    @EJB
    private MailSenderBean mailSenderBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");

        String toEmail = request.getParameter("email");
        String subject = "Password reminder";
        String message = "Your password reminder link: ";

        String fromEmail = "javainis2017@gmail.com";
        String username = "javainis2017@gmail.com";
        String password = "javainiai";

        try(PrintWriter out = response.getWriter())
        {
            mailSenderBean.sendEmail(fromEmail, username, password, toEmail, subject, message);

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Mail Status</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Mail Status</h1>");
            out.println("<b>Message sent</b></br>");
            out.println("Click here <a href='login-page.xhtml'> to go back");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
