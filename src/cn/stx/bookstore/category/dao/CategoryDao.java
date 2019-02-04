package cn.stx.bookstore.category.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import cn.itcast.jdbc.TxQueryRunner;
import cn.stx.bookstore.category.domain.Category;

public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();

	/*
	 * 查询所有分类
	 */
	public List<Category> findAll() {
		String sql = "select * from category";
		try {
			return qr.query(sql, new BeanListHandler<Category>(Category.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * 添加分类
	 */
	public void add(Category category) {
		String sql = "insert into category values(?,?)";
		try {
			qr.update(sql, category.getCid(), category.getCname());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	/*
	 * 根据cid删除分类
	 */
	public void delete(String cid) {
		try {
			String sql = "delete from category where cid = ?";
			qr.update(sql, cid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * 加载分类
	 */
	public Category load(String cid) {
		try {
			String sql = "select * from category where cid=?";
			return qr.query(sql, new BeanHandler<Category>(Category.class), cid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * 修改分类
	 */
	public void edit(Category category) {
		try {
			String sql = "update category set cname = ? where cid = ?";
			qr.update(sql, category.getCname(), category.getCid());
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
