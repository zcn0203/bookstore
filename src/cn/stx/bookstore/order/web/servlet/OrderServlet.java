package cn.stx.bookstore.order.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import cn.stx.bookstore.cart.domain.Cart;
import cn.stx.bookstore.cart.domain.CartItem;
import cn.stx.bookstore.order.domain.Order;
import cn.stx.bookstore.order.domain.OrderItem;
import cn.stx.bookstore.order.service.OrderException;
import cn.stx.bookstore.order.service.OrderService;
import cn.stx.bookstore.user.domain.User;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet(name="OrderServlet", urlPatterns="/OrderServlet")
public class OrderServlet extends BaseServlet {
	OrderService orderService = new OrderService();
	/*
	 * 这个方法是易宝回调方法，我们必须判断调用本方法的是不是易宝
	 */
	public String back(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 获取11+1个参数
		 */
		String p1_MerId = request.getParameter("p1_MerId");
		String r0_Cmd = request.getParameter("r0_Cmd");
		String r1_Code = request.getParameter("r1_Code");
		String r2_TrxId = request.getParameter("r2_TrxId");
		String r3_Amt = request.getParameter("r3_Amt");
		String r4_Cur = request.getParameter("r4_Cur");
		String r5_Pid = request.getParameter("r5_Pid");
		String r6_Order = request.getParameter("r6_Order");
		String r7_Uid = request.getParameter("r7_Uid");
		String r8_MP = request.getParameter("r8_MP");
		String r9_BType = request.getParameter("r9_BType");

		String hmac = request.getParameter("hmac");
		
		/*
		 * 校验访问者是否为易宝
		 */
		Properties props = new Properties();
		InputStream input = this.getClass().getClassLoader()
				.getResourceAsStream("merchantInfo.properties");
		props.load(input);
		String keyValue = props.getProperty("keyValue");
		
		Boolean bool = 
				PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd, 
						r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, 
						r6_Order, r7_Uid, r8_MP, r9_BType, keyValue);
		/*
		 * 判断是否为易宝请求的连接
		 */
		if(!bool) {
			request.setAttribute("mdg", "您不是什么好动洗");
			return "f:/jsps/msg.jsp";
		}
		
		/*
		 * 获取订单状态，决定是否需要修改数据库，以及添加积分等业务操作
		 */
		orderService.zhiFu(r6_Order);
		/*
		 * 判断当前回调方式
		 * 如果为点对点，需要回馈success开头的字符串
		 */
		if(r9_BType.equals("2")) {
			response.getWriter().print("success");
		}
		/*
		 * 保存成功信息，转发到msg
		 */
		request.setAttribute("msg", "支付成功，等待卖家发货");
		
		return "f:/jsps/msg.jsp";
	}
	/*
	 * 支付之去银行
	 */
	public String zhiFu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 准备13个参数
		 */
		Properties prop = new Properties();
		InputStream input = 
				this.getClass().getClassLoader().getResourceAsStream("merchantInfo.properties");
		prop.load(input);
		String p0_Cmd = "Buy";
		String p1_MerId = prop.getProperty("p1_MerId");
		String p2_Order = request.getParameter("oid");
		String p3_Amt = "0.01";
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		String p8_Url = prop.getProperty("p8_Url");
		String p9_SAF = "";
		String pa_MP = "";
		String pd_FrpId = request.getParameter("pd_FrpId");
		String pr_NeedResponse = "1";
		/*
		 * 计算hmac
		 */
		String keyValue = prop.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, 
				p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, 
				p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
		/*
		 * 连接易宝的支付网关
		 */
		StringBuilder url = new StringBuilder(prop.getProperty("url"));
		url.append("?p0_Cmd=").append(p0_Cmd);
		url.append("&p1_MerId=").append(p1_MerId);
		url.append("&p2_Order=").append(p2_Order);
		url.append("&p3_Amt=").append(p3_Amt);
		url.append("&p4_Cur=").append(p4_Cur);
		url.append("&p5_Pid=").append(p5_Pid);
		url.append("&p6_Pcat=").append(p6_Pcat);
		url.append("&p7_Pdesc=").append(p7_Pdesc);
		url.append("&p8_Url=").append(p8_Url);
		url.append("&p9_SAF=").append(p9_SAF);
		url.append("&pa_MP=").append(pa_MP);
		url.append("&pd_FrpId=").append(pd_FrpId);
		url.append("&pr_NeedResponse=").append(pr_NeedResponse);
		url.append("&hmac=").append(hmac);
		System.out.println(url);
		/*
		 * 重定向到易宝url
		 */
		response.sendRedirect(url.toString());
		return null;
	}
	/*
	 * 确认收货
	 */
	public String confirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 获取oid参数
		 * 调用service方法
		 *  如果有异常，保存异常信息，转发到msg
		 *  保存成功信息，转发到msg
		 */
		String oid = request.getParameter("oid");
		try {
			orderService.confirm(oid);
			request.setAttribute("msg", "恭喜交易成功");
		} catch(OrderException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
	
	/*
	 * 加载订单方法
	 */
	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.得到oid参数
		 * 2.使用oid调用service方法得到order
		 * 3.保存到request域，转发到/jsps/order/desc.jsp
		 */
		request.setAttribute("order", orderService.load(request.getParameter("oid")));
		return "f:/jsps/order/desc.jsp";
	}
	
	
	public String myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.从session获取当前用户，再获取其uid
		 * 2.使用uid调用orderService#myOrders（uid）得到所有订单List<Order>
		 * 3.把订单列表保存到request中，转发到jsps/order/list.jsp
		 */
		User user = (User)request.getSession().getAttribute("session_user");
		List<Order> orderList = orderService.myOrders(user.getUid());
		request.setAttribute("orderList", orderList);
		return "f:/jsps/order/list.jsp";
	}
	
	/*-
	 * 添加订单
	 * 把session中的车用来生成order对象
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.从session中得到cart
		 * 2.使用cart生成order对象
		 * 3.调用service方法完成添加订单
		 * 4.保存order到request中，转发到jsps/order/desc.jsp
		 */
		//从session中获取cart
		Cart cart = (Cart)request.getSession().getAttribute("cart");
		//把cart转换成order对象
		/*
		 * 创建order对象，并设置属性
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(new Date());//设置下蛋时间
		order.setState(1);//设置状态为1，表示未付款
		User user = (User)request.getSession().getAttribute("session_user");
		order.setOwner(user);//设置订单所有者
		order.setTotal( cart.getTotal());//从车中获取合计
		/*
		 * 创建订单条目集合
		 * 把cartItem转换成orderItem
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		//循环遍历Cart中的所有CartItem，使用每一个CartItem对象创建OrderItem对象，并添加到集合中
		for(CartItem cartItem:cart.getCartItems()) {
			OrderItem oi = new OrderItem();//创建订单条目
			
			oi.setIid(CommonUtils.uuid());
			oi.setCount(cartItem.getCount());//设置条目的数量
			oi.setBook(cartItem.getBook());//设置条目的图书
			oi.setSubtotal(cartItem.getSubtotal());//设置条目的小计
			oi.setOrder(order);//设置所属订单
			
			orderItemList.add(oi);//把订单条目添加到集合中
		}
		//把所有的订单条目添加到订单中
		order.setOrderItemList(orderItemList);
		
		//清空购物车
		cart.clear();
		/*
		 * 调用orderService 添加订单
		 */
		orderService.add(order);
		/*
		 * 保存order到request域，转发到jsps/order/desc.jsp
		 */
		System.out.println(order.getOrderItemList().get(0).getBook().getBname());
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}

}
