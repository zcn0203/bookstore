package cn.stx.bookstore.order.domain;

import java.util.Date;
import java.util.List;

import cn.stx.bookstore.user.domain.User;

public class Order {
	private String oid;
	private Date ordertime;// 下单时间
	private double total;// 合计
	private int state;// 订单状态，有四种：1.未付款2.已付款，未发货3.已发货，未收货4.已确认，交易成功
	private User owner;// 订单所有者
	private String address;// 收货地址

	private List<OrderItem> orderItemList;// 当前订单下所有条目


	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Date getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

	public Order(String oid, Date ordertime, double total, int state, User owner, String address,
			List<OrderItem> orderItemList) {
		super();
		this.oid = oid;
		this.ordertime = ordertime;
		this.total = total;
		this.state = state;
		this.owner = owner;
		this.address = address;
		this.orderItemList = orderItemList;
	}

	public Order() {
		super();
	}

}
