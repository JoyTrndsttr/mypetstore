package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;

import java.math.BigDecimal;
import java.util.*;

public interface CartDao {
    Cart getCartByUsername(String username);

    //插入一项购物车
    int insertCart(Cart cart);

    int updateCart(Cart cart,String username);

    void removeCart(Cart cart);



}
