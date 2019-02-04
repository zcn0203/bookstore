package cn.stx.bookstore.book.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;
import cn.stx.bookstore.book.domain.Book;
import cn.stx.bookstore.book.service.BookService;
@WebServlet("/BookServlet")
public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();

	/*
	 * 查询所有图书
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Book> list = bookService.findAll();
		request.setAttribute("bookList", list);
		return "f:/jsps/book/list.jsp";
	}
	
	/*
	 * 按照分类查询图书
	 */
	public String findByCategory(HttpServletRequest request, HttpServletResponse response) {
		String cid = request.getParameter("cid");
		List<Book> bookList = bookService.findByCategory(cid);
		request.setAttribute("bookList", bookList);
		return "f:/jsps/book/list.jsp";
	}
	
	/*
	 * 按照bid加载图书
	 */
	public String load(HttpServletRequest request, HttpServletResponse response) {
		String bid = request.getParameter("bid");
		request.setAttribute("book", bookService.load(bid));
		return "f:/jsps/book/desc.jsp";
	}

}
