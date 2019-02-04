package cn.stx.bookstore.category.service;

import java.util.List;

import cn.stx.bookstore.book.dao.BookDao;
import cn.stx.bookstore.category.dao.CategoryDao;
import cn.stx.bookstore.category.domain.Category;
import cn.stx.bookstore.category.web.servlet.admin.CategoryException;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	private BookDao bookDao = new BookDao();
	

	/*
	 * 查询所有分类
	 */
	public List<Category> findAll() {
		// TODO Auto-generated method stub
		return categoryDao.findAll();
	}

	/*
	 * 添加分类
	 */
	public void add(Category category) {
		categoryDao.add(category);
	}

	/*
	 * 根据cid删除分类
	 */
	public void delete(String cid) throws CategoryException {
		int count = bookDao.getCountByCid(cid);
		//如果该分类下存在图书，则不能删除
		if(count > 0) {
			throw new CategoryException("该分类下有图书，不能删除！");
		}
		categoryDao.delete(cid);
	}

	/*
	 * 根据cid加载分类
	 */
	public Category load(String cid) {
		return categoryDao.load(cid);
	}

	public void edit(Category category) {
		categoryDao.edit(category);
	}
}
