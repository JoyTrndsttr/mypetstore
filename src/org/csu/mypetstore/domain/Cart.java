package org.csu.mypetstore.domain;

//import com.ibatis.common.util.PaginatedArrayList;
import org.csu.mypetstore.persistence.CartDao;
import org.csu.mypetstore.persistence.Impl.CartDaoImpl;
import org.csu.mypetstore.service.CatalogService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*偷懒的设计：
  直接利用这个javabeaan，然后进行每一步操作时更新数据库就行了
 */

public class Cart implements Serializable {

  private static final long serialVersionUID = 8329559983943337176L;
  private final Map<String, CartItem> itemMap = Collections.synchronizedMap(new HashMap<String, CartItem>());
  private final List<CartItem> itemList = new ArrayList<CartItem>();

  /*----*/
  private String username = "";
  private String cartitem = "";
  private String cartimenums = "";
  private double total = 0;
  private boolean isLogin = false;//判断是否登录

  public Cart(String username, String cartitem, String cartimenums, double total) {
    this.username = username;
    this.cartitem = cartitem;
    this.cartimenums = cartimenums;
    this.total = total;

  }

  public Cart() {
  }

  public boolean isLogin() {
    return isLogin;
  }

  public void setLogin(boolean login) {
    isLogin = login;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCartitem() {
    return cartitem;
  }

  public void setCartitem(String cartitem) {
    this.cartitem = cartitem;
  }

  public String getCartimenums() {
    return cartimenums;
  }

  public void setCartimenums(String cartimenums) {
    this.cartimenums = cartimenums;
  }

  public void updateFromDB(){
    if (isLogin == false){
      System.out.println("updateFromDB:"+"用户未登录");
      return;
    }

    //获得数据库中的信息
    // 用户可能无购物车?
    CartDao cartDao = new CartDaoImpl();
    System.out.println("cartDao.getCartByUsername(username):"+username);
    Cart tempCart = cartDao.getCartByUsername(username);

    if(tempCart == null){
      //数据库中无购物车
      //向数据库新建购物车
      cartDao.insertCart(this);
      return;

      //

    }
    this.setCartimenums(tempCart.getCartimenums());
    this.setCartitem(tempCart.getCartitem());
    this.setTotal(tempCart.getTotal());

    //更新到map里面的
    String[] workingItemId = this.getCartitem().split("##");
    String[] nums = this.getCartimenums().split("##");
    CatalogService catalogService = new CatalogService();

    for (int i=0;i<workingItemId.length&&i<nums.length;i++){

      boolean isInStock = catalogService.isItemInStock(workingItemId[i]);//是否有库存
      Item item = catalogService.getItem(workingItemId[i]);//根据workingItemId获取Item
//      this.addItem(item, isInStock);//增加商品
      if(item != null)
      {
        CartItem cartItem = itemMap.get(item.getItemId());
        if (cartItem == null) {//如果购物车中不存在
          cartItem = new CartItem();
          cartItem.setItem(item);
          cartItem.setQuantity(Integer.parseInt(nums[i]));//设置数量，
          cartItem.setInStock(isInStock);

          //添加
          this.itemMap.put(item.getItemId(), cartItem);
          this.itemList.add(cartItem);
        }else
        {//如果有库存
          cartItem.setQuantity(Integer.parseInt(nums[i]));//设置数量，
          cartItem.setInStock(isInStock);
        }
      }

    }
    //更新价






  }
  public void updateToDB(){
    BigDecimal subTotal = new BigDecimal("0");
    String tempCartItem ="";
    String tempNum = "";
    Iterator<CartItem> items = getAllCartItems();
    while (items.hasNext()) {
      CartItem cartItem = (CartItem) items.next();
      Item item = cartItem.getItem();
      BigDecimal listPrice = item.getListPrice();
      BigDecimal quantity = new BigDecimal(String.valueOf(cartItem.getQuantity()));
      subTotal = subTotal.add(listPrice.multiply(quantity));

      //
      tempCartItem+=item.getItemId()+"##";
      tempNum +=quantity+"##";
    }

    this.setCartitem(tempCartItem);
    this.setCartimenums(tempNum);
    this.setTotal(subTotal.doubleValue());

    //更新到数据库
    new CartDaoImpl().updateCart(this,this.username);

  }
  /*---*/

  public Iterator<CartItem> getCartItems() {
    return itemList.iterator();
  }

  public List<CartItem> getCartItemList() {
    return itemList;
  }

  public int getNumberOfItems() {
    return itemList.size();
  }

  public Iterator<CartItem> getAllCartItems() {
    return itemList.iterator();
  }

  public boolean containsItemId(String itemId) {
    return itemMap.containsKey(itemId);
  }

  //增加item,首先增加到
  public void addItem(Item item, boolean isInStock) {
    CartItem cartItem = (CartItem) itemMap.get(item.getItemId());
    if (cartItem == null) {
      cartItem = new CartItem();
      cartItem.setItem(item);
      cartItem.setQuantity(0);//设置数量，数据不持久化时为零
      cartItem.setInStock(isInStock);

      //添加
      itemMap.put(item.getItemId(), cartItem);
      itemList.add(cartItem);
    }
    cartItem.incrementQuantity();

    //更新到数据库
    this.updateToDB();
  }
  //通过itemId移除Item
  public Item removeItemById(String itemId) {
    CartItem cartItem = (CartItem) itemMap.remove(itemId);
    if (cartItem == null) {
      return null;
    } else {
      itemList.remove(cartItem);
      //更新到数据库
      this.updateToDB();
      return cartItem.getItem();
    }

//    //更新到数据库
//    new CartDaoImpl().updateCart(this,this.username);
  }

  //已有该物品，数量加一
  //更新数量
  public void incrementQuantityByItemId(String itemId) {
    CartItem cartItem = (CartItem) itemMap.get(itemId);
    cartItem.incrementQuantity();

    //更新到数据库
    this.updateToDB();
  }

  public void setQuantityByItemId(String itemId, int quantity) {
    CartItem cartItem = (CartItem) itemMap.get(itemId);
    cartItem.setQuantity(quantity);

    //更新到数据库
    this.updateToDB();
  }

  public BigDecimal getSubTotal() {
    BigDecimal subTotal = new BigDecimal("0");
    Iterator<CartItem> items = getAllCartItems();
    while (items.hasNext()) {
      CartItem cartItem = (CartItem) items.next();
      Item item = cartItem.getItem();
      BigDecimal listPrice = item.getListPrice();
      BigDecimal quantity = new BigDecimal(String.valueOf(cartItem.getQuantity()));
      subTotal = subTotal.add(listPrice.multiply(quantity));
    }

    //更新到数据库
    this.updateToDB();

    return subTotal;

  }

}
