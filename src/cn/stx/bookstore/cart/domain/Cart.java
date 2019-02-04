package cn.stx.bookstore.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 购物车类
 */
public class Cart {
	public Map<String, CartItem> map = new LinkedHashMap<String, CartItem>();
	
	/*
	 * 计算合计
	 *  合计等于所有条目的小计之和
	 *  处理二进制误差
	 */
	public double getTotal() {
		BigDecimal total = new BigDecimal(0);
		for(CartItem cartItem:map.values()) {
			BigDecimal subtotal = new BigDecimal(cartItem.getSubtotal()+"");
			total = total.add(subtotal);
		}
		return total.doubleValue();
	}
	
	/*
	 * 添加购物条目
	 */
	public void add(CartItem cartItem) {
		//判断原来库中是否存在该条目
		if(map.containsKey(cartItem.getBook().getBid())) {
			CartItem _cartItem = map.get(cartItem.getBook().getBid());//返回该条目
			//设置老条目的数量
			_cartItem.setCount(_cartItem.getCount()+cartItem.getCount());
			map.put(cartItem.getBook().getBid(), _cartItem);
		} else {
			map.put(cartItem.getBook().getBid(), cartItem);	
		}
	}
	
	/*
	 * 清空条目
	 */
	public void clear() {
		map.clear();
	}
	
	/*
	 *	删除指定条目 
	 */
	public void delete(String bid) {
		map.remove(bid);
	}
	
	/*
	 * 我的购物车
	 */
	public Collection<CartItem> getCartItems(){
		return map.values();
	}
	
}
