package org.csu.mypetstore.web.servlets;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewCartServlet extends HttpServlet {
    private static final String VIEW_CART = "/WEB-INF/jsp/cart/Cart.jsp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = (Cart)session.getAttribute("cart");

        Account account = (Account)session.getAttribute("account");

        if(cart == null){
            cart = new Cart();
            session.setAttribute("cart", cart);
            if(account != null){//如果账号已经登录
                cart.setLogin(true);
                cart.setUsername(account.getUsername());
                cart.updateFromDB();

            }
        }

        //HttpSession session = request.getSession();


//        if(account != null){
//            //获取时间
//            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = new Date(System.currentTimeMillis());
//            System.out.println(formatter.format(date));
//
//            LogService logService = new LogService();
//            String logInfo = formatter.format(date) +" 查看购物车";
//            logService.insertLogInfo(account.getUsername(), logInfo);
//        }


        request.getRequestDispatcher(VIEW_CART).forward(request, response);
    }
}
