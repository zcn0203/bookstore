package cn.stx.bookstore.cart.domain;
/**
 * 条目类
 */

import java.math.BigDecimal;

import cn.stx.bookstore.book.domain.Book;

public class CartItem {

	private Book book;
	private int count;

	public double getSubtotal() {
		/*
		 * 小计方法，但是没有对应的成员
		 * 处理了二进制运算误差问题
		 */
		BigDecimal d1 = new BigDecimal(book.getPrice()+"");
		BigDecimal d2 = new BigDecimal(count+"");
		return d1.multiply(d2).doubleValue();
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public CartItem(Book book, int count) {
		super();
		this.book = book;
		this.count = count;
	}

	public CartItem() {
		super();
	}

	@Override
	public String toString() {
		return "CartItem [book=" + book + ", count=" + count + "]";
	}

}
