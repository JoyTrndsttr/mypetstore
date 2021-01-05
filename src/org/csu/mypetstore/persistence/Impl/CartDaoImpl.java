package org.csu.mypetstore.persistence.Impl;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.persistence.CartDao;
import org.csu.mypetstore.persistence.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CartDaoImpl implements CartDao {
//    private static final String = "seclect";
    private static final String insertCartString = "insert into cart (username, cartitem,cartitemnum,total) VALUES (?, '','',0)";
    private static final String findCartString = "SELECT * FROM cart WHERE username = ? ";
    private static final String updateString = "UPDATE cart SET cartitem = ? , cartitemnum = ? , total = ? WHERE username = ?";
//  private static final String insertCartString = "insert into cart (username ) VALUES (?)";

    @Override
    public Cart getCartByUsername(String username) {

       Cart cart = null;
        try {
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(findCartString);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                cart = new Cart();
                cart.setUsername(resultSet.getString(1));
                cart.setCartitem(resultSet.getString(2));
                cart.setCartimenums(resultSet.getString(3));
                cart.setTotal(resultSet.getDouble(4));
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }


        return cart;
    }

    //根据用户名this.username插入,其余字段均为空
    @Override
    public int insertCart(Cart cart) {
        int result = 0;
        try {
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertCartString);
            if(cart != null){
                preparedStatement.setString(1, cart.getUsername());

            }
            result = preparedStatement.executeUpdate();

            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }



    //更新购物车
    @Override
    public int updateCart(Cart cart,String username) {
        int result = 0;
        try {
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateString);
            preparedStatement.setString(1, cart.getCartitem());
            preparedStatement.setString(2,cart.getCartimenums());
            preparedStatement.setDouble(3,cart.getTotal());
            preparedStatement.setString(4,username);

            result = preparedStatement.executeUpdate();
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

    @Override
    public void removeCart(Cart cart) {

    }

    public static void main(String[] args) {
        CartDao cartDao = new CartDaoImpl();
        System.out.println(cartDao.getCartByUsername("new").getUsername());
//        System.out.println(cartDao.insertCart());
//        System.out.println(cartDao.updateCart(new Cart("new","new","new",9),"new"));
    }
}
