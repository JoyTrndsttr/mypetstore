package org.csu.mypetstore.web.servlets;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
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

public class SignOffServlet extends HttpServlet {
    private static final String MAIN = "/WEB-INF/jsp/catalog/Main.jsp";

    private Account account;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account)session.getAttribute("account");



        if(account != null){
            //获取时间
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            System.out.println(formatter.format(date));

            LogService logService = new LogService();
            String logInfo = formatter.format(date) +" 用户退出登录";
            logService.insertLogInfo(account.getUsername(), logInfo);
        }

        //情空要这样？
        session.setAttribute("account",null);
        session.setAttribute("cart",null);
        account = null;
        //购物车也情空
        Cart cart = (Cart) session.getAttribute("cart");
        cart = null;

        //HttpSession session = request.getSession();
        session.setAttribute("account", account);

        request.getRequestDispatcher(MAIN).forward(request, response);
    }
}
