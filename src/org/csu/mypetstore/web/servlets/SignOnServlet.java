package org.csu.mypetstore.web.servlets;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.service.AccountService;
import org.csu.mypetstore.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignOnServlet extends HttpServlet {
    private static final String MAIN = "/WEB-INF/jsp/catalog/Main.jsp";
    private static final String SIGNONFORM = "/WEB-INF/jsp/account/SignonForm.jsp";

    private Account account;
    private AccountService accountService;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        accountService = new AccountService();
        account = accountService.getAccount(username, password);

        HttpSession session = request.getSession();
        session.setAttribute("account", account);

        if(account != null){
            //获取时间
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            System.out.println(formatter.format(date));

            LogService logService = new LogService();
            String logInfo = formatter.format(date) +" 用户登录";
            logService.insertLogInfo(account.getUsername(), logInfo);
        }


        //获得输入的验证码值
        String value1=request.getParameter("vCode");
        /*获取图片的值*/
        String value2=(String)session.getAttribute("verificationCode");
        Boolean isSame = false;
        /*对比两个值（字母不区分大小写）*/
        if(value2.equalsIgnoreCase(value1)){
            isSame = true;
        }


        if (account == null || !isSame){
            if(!isSame){
                session.setAttribute("messageSignOn", "验证码错误！");
            }else{
                session.setAttribute("messageSignOn", "账号密码错误，登录失败！");
            }
            session.setAttribute("account", null);
            request.getRequestDispatcher(SIGNONFORM).forward(request, response);
        }else{
            account.setPassword(null);
            //将购物车清空
            session.setAttribute("cart",null);
            request.getRequestDispatcher(MAIN).forward(request, response);
        }
    }
}
