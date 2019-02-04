package cn.stx.bookstore.cart.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;
import cn.stx.bookstore.book.domain.Book;
import cn.stx.bookstore.book.service.BookService;
import cn.stx.bookstore.cart.domain.Cart;
import cn.stx.bookstore.cart.domain.CartItem;
@WebServlet(name="CartServlet", urlPatterns="/CartServlet")
public class CartServlet extends BaseServlet {
	/**
	 * 添加购物条目
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.得到购物车
		 * 2.在用户登录成功后，在session中添加购物车
		 * 3.得到条目
		 * 4.把条目添加到车中
		 */
		
		//得到车
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		/*
		 * 得到条目
		 * 	1.得到图书的数量
		 * 	2.先得到图书的bid，通过bid查询数据库得到Book
		 * 	3.从表单中获取数量
		 */
		String bid = request.getParameter("bid");
		Book book = new BookService().load(bid);
		int count = Integer.parseInt(request.getParameter("count"));
		CartItem cartItem = new CartItem();
		cartItem.setBook(book);
		cartItem.setCount(count);
		
		/*
		 * 把条目添加到车中
		 */
		cart.add(cartItem);
		
		return "f:/jsps/cart/list.jsp";
	}
	
	/**
	 * 清空购物条目
	 */
	public String clear(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		/*
		 * 1.得到车
		 * 2.调用车的clear
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		cart.clear();
		return "f:/jsps/cart/list.jsp";
	}
	
	/**
	 * 删除购物条目
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		/*
		 * 1.得到车
		 * 2.得到要删除的bid
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		String bid = request.getParameter("bid");
		cart.delete(bid);
		return "f:/jsps/cart/list.jsp";
	}
}
