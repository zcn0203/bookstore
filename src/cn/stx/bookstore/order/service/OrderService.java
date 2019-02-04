package cn.stx.bookstore.order.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.jdbc.JdbcUtils;
import cn.stx.bookstore.order.dao.OrderDao;
import cn.stx.bookstore.order.domain.Order;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	/*
	 * 支付
	 */
	public void zhiFu(String oid) {
		/*
		 * 获取订单状态
		 * 如果状态为1，那么执行下面代码
		 * 如果状态不为1，那么什么都不做
		 */
		int state = orderDao.getStateByOid(oid);
		if(state == 1) {
			//修改订单状态为2
			orderDao.updateState(oid, 2);
		}
		
	}
	/*
	 * 添加订单，处理事务
	 */
	public void add(Order order) {
		try {
			//开启事务
			JdbcUtils.beginTransaction();
			
			orderDao.addOrder(order);//插入订单
			orderDao.addOrderItemList(order.getOrderItemList());//插入订单中的所有条目
			
			//提交事务
			JdbcUtils.commitTransaction();
		} catch(Exception e) {
			//回滚事务
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException(e);
		}
	}
	/*
	 * 我的订单
	 */
	public List<Order> myOrders(String uid) {
		return orderDao.findByUid(uid);
	}
	/*
	 * 加载订单方法
	 */
	public Order load(String oid) {
		return orderDao.load(oid);
	}
	
	/*
	 * 修改订单状态
	 */
	public void confirm(String oid) throws OrderException{
		//通过oid校验订单状态
		int state = orderDao.getStateByOid(oid);
		if(state != 3) {
			throw new OrderException("订单确认失败");
		}
		//修改订单状态为4，表示交易成功
		orderDao.updateState(oid, 4);
	}
}
