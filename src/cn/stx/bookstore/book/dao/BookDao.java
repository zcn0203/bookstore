package cn.stx.bookstore.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import cn.stx.bookstore.book.domain.Book;
import cn.stx.bookstore.category.domain.Category;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	/*
	 * 查询所有图书
	 */
	public List<Book> findAll(){
		try {
			String sql = "select * from book where del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class));
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 根据分类查询图书
	 */
	public List<Book> findByCategory(String cid) {
		try {
			String sql = "select * from book where cid = ? and del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class), cid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 加载图书内容
	 */
	public Book load(String bid) {
		try {
			String sql = "select * from book where bid = ?";
			Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
			Category category = CommonUtils.toBean(map, Category.class);
			Book book = CommonUtils.toBean(map, Book.class);
			book.setCategory(category);
			return book;
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 查询指定分类下的图书本数
	 */
	public int getCountByCid(String cid) {
		try {
			String sql = "select count(*) from book where cid = ? and del=false";
			Number count = (Number) qr.query(sql, new ScalarHandler(), cid);
			return count.intValue();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 添加图书
	 */
	public void add(Book book) {
		try {
			String sql = "insert into book values(?,?,?,?,?,?,?)";
			Object[] params = {book.getBid(), book.getBname(), book.getPrice(),book.getAuthor(), book.getImage(), book.getCategory().getCid(),book.isDel()};
			qr.update(sql, params);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 删除图书
	 */
	public void delete(String bid) {
		try {
			String sql = "update book set del=true where bid = ?";
			qr.update(sql, bid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 修改图书
	 */
	public void edit(Book book) {
		try {
			String sql = "update book set bname=?, price=?, author=?, image=?, cid=?  where bid = ?";
			Object[] params = { book.getBname(), book.getPrice(),
					book.getAuthor(), book.getImage(), book.getCategory().getCid(),
					book.getBid()};
			qr.update(sql, params);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

}