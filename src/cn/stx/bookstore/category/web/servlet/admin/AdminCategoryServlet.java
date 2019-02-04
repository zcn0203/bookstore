package cn.stx.bookstore.category.web.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import cn.stx.bookstore.category.domain.Category;
import cn.stx.bookstore.category.service.CategoryService;
@WebServlet("/admin/AdminCategoryServlet")
public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	/*
	 * 修改分类
	 */
	public String edit(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		/*
		 * 封装表单数据
		 * 调用service方法，完成修改工作
		 * 调用findAll
		 */
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		categoryService.edit(category);
		return findAll(request, response);
	}
	/*
	 * 修改准备
	 */
	public String editPre(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String cid = request.getParameter("cid");
		request.setAttribute("category", categoryService.load(cid));
		return "f:/adminjsps/admin/category/mod.jsp";
	}
	/*
	 * 删除分类
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 获取参数cid
		 * 调用service方法，传递cid参数
		 *  如果抛出异常，保存异常信息，转发到msg。jsp
		 * 调用findAll()
		 */
		String cid = request.getParameter("cid");
		try {
			categoryService.delete(cid);
			return findAll(request, response);
		} catch(CategoryException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
	}
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 封装表单数据
		 * 补全cid
		 * 调用service方法，我按成添加工作
		 * 调用findAll
		 */
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		category.setCid(CommonUtils.uuid());
		
		categoryService.add(category);
		return findAll(request, response);
	}
	
	/*
	 * 查询所有分类
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 调用service方法，得到所有分类
		 * 保存到request域，转发到/adminjsps/admin/list.jsp
		 */
		request.setAttribute("categoryList", categoryService.findAll());
		
		return "f:/adminjsps/admin/category/list.jsp";
	}
}
