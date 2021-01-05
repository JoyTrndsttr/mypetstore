package org.csu.mypetstore.web.servlets;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.service.LogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainServlet extends javax.servlet.http.HttpServlet {

    //WEB-INF里面的页面只能通过servlet进行跳转访问
    private static final String MAIN = "/WEB-INF/jsp/catalog/Main.jsp";
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {



        HttpSession session = request.getSession();
        //添加日志
        Account account = (Account)session.getAttribute("account");
        if(account != null){
            //获取时间
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            System.out.println(formatter.format(date));

            LogService logService = new LogService();
            String logInfo = formatter.format(date) +" 进入主界面";
            logService.insertLogInfo(account.getUsername(), logInfo);
        }


        request.getRequestDispatcher(MAIN).forward(request,response);
    }
}
