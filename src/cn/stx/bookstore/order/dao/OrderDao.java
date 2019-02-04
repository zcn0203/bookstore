package cn.stx.bookstore.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import cn.stx.bookstore.book.domain.Book;
import cn.stx.bookstore.order.domain.Order;
import cn.stx.bookstore.order.domain.OrderItem;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();

	/*
	 * 添加订单
	 */
	public void addOrder(Order order) {
		try {
			String sql = "insert into orders values(?,?,?,?,?,?)";
			/*
			 * 处理Utils的Date转换成sql的Timestamp
			 */
			Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
			Object[] params = { order.getOid(), timestamp, order.getTotal(), order.getState(),
					order.getOwner().getUid(), order.getAddress() };
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * 添加订单条目
	 */
	public void addOrderItemList(List<OrderItem> orderItemList) {
		/*
		 * QueryRunner 的batch(String sql, Object[][] params)
		 * 其中params是多个一维数组
		 * 每个一维数组都与sql在一起执行一次，多个一维数组，就执行多次
		 */
		try {
			String sql = "insert into orderitem values(?,?,?,?,?)";
			//把orderItemList转换成二维数组
			//把一个orderItem对象，转换成一个一维数组
			Object[][] params = new Object[orderItemList.size()][];
			for(int i=0; i<orderItemList.size(); i++) {
				OrderItem item = orderItemList.get(i);
				params[i] = new Object[] {item.getIid(), item.getCount(),
						item.getSubtotal(), item.getOrder().getOid(),
						item.getBook().getBid()};
			}
			qr.batch(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * 根据uid查询
	 */
	public List<Order> findByUid(String uid) {
		/*
		 * 通过uid查询出当前用户的所有的List<Order>
		 * 循环遍历每一个order，为其加载他的所有OrderItem
		 */
		try {
			/*
			 * 得到当前用户的所有订单
			 */
			String sql = "select * from orders where uid = ?";
			List<Order> orderList = 
					qr.query(sql, new BeanListHandler<Order>(Order.class), uid);
			/*
			 * 循环遍历每个Order，为其加载他自己所有的订单条目
			 */
			for(Order order:orderList) {
				loadOrderItems(order);
			}
			/*
			 * 返回订单列表
			 */
			return orderList;
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * 加载指定的订单所有的订单条目
	 */
	public void loadOrderItems(Order order) {
		try {
			String sql = "select * from orderitem i inner join book b on i.bid = b.bid and oid = ?";
			/*
			 * 因为一行结果集对应的不是一个javabean，所以不能在使用BeanListHandler，而是MapListHandler
			 */
			List<Map<String, Object>> mapList = 
					qr.query(sql, new MapListHandler(), order.getOid());
			/*
			 * MapList是多个map，每个map对应一行结果集
			 * 一行：
			 * 	key（列名）=value（列值）  ps：如果有重复的会发生覆盖
			 * 我们需要使用一个Map生成两个对象，OrderItem，Book，
			 * 然后建立两者的关系（把Book设置给OrderItem）
			 */
			/*
			 * 遍历循环每个Map，使用map生成两个对象，然后建立关系（最终生成一个orderItem）
			 * 把orderItem保存起来
			 */
			List<OrderItem> orderItemList = toOrderItemList(mapList);
			order.setOrderItemList(orderItemList);
			
			
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * 把mapList中每个Map转换成两个对象，并建立关系
	 */
	public List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String, Object> map:mapList) {
			OrderItem item = toOrderItem(map);
			orderItemList.add(item);
		}
		return orderItemList;
	}

	/*
	 * 把一个map转换成一个OrderItem对象
	 */
	public OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		//建立两者关系
		orderItem.setBook(book);
		return orderItem;
		
	}

	/*
	 * 加载订单
	 */
	public Order load(String oid) {
		try {
			String sql = "select * from orders where oid = ?";
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
			//为Order加载所有条目
			loadOrderItems(order);
			return order;
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 通过oid查看订单状态
	 */
	public int getStateByOid(String oid) {
		try {
			String sql = "select state from orders where oid = ?";
			Number num = (Number)qr.query(sql, new ScalarHandler(), oid);
			return num.intValue();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 修改订单状态
	 */
	public int updateState(String oid, int state) {
		try {
			String sql = "update orders set state=? where oid=?";
			return qr.update(sql, state, oid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
