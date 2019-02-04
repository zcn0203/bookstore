package cn.stx.bookstore.book.service;

import java.util.List;

import cn.stx.bookstore.book.dao.BookDao;
import cn.stx.bookstore.book.domain.Book;

public class BookService {
	private BookDao bookDao = new BookDao();

	/*
	 * 查询所有图书
	 */
	public List<Book> findAll() {
		return bookDao.findAll();
	}

	/*
	 * 根据分类查询图书
	 */
	public List<Book> findByCategory(String cid) {
		// TODO Auto-generated method stub
		return bookDao.findByCategory(cid);
	}

	/*
	 * 根据bid查找图书
	 */
	public Book load(String bid) {
		return bookDao.load(bid);
	}

	/*
	 * 添加图书
	 */
	public void add(Book book) {
		bookDao.add(book);
	}
	/*
	 * 删除图书
	 */
	public void delete(String bid) {
		bookDao.delete(bid);
	}

	/*
	 * 修改图书
	 */
	public void edit(Book book) {
		bookDao.edit(book);
	}
	
}
