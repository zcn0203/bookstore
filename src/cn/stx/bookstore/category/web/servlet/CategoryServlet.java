package cn.stx.bookstore.category.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;
import cn.stx.bookstore.category.service.CategoryService;
@WebServlet("/CategoryServlet")
public class CategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();

	/*
	 * 查询所有分类
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("categoryList", categoryService.findAll());
		return "f:/jsps/left.jsp";
	}
	
	
}
