package cn.stx.bookstore.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.jdbc.TxQueryRunner;
import cn.stx.bookstore.user.domain.User;

/*
 * User持久层
 * 依赖QueryRunner
 */
public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/*
	 * 功能,按用户名查询
	 */
	public User findByUserName(String username) {
		try {
			String sql = "select * from tb_user where username=?";
			return qr.query(sql, new BeanHandler<User>(User.class), username);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 按邮箱查询 
	 */
	public User findByUserEmail(String email) {
		try {
			String sql = "select * from tb_user where email=?";
			return qr.query(sql, new BeanHandler<User>(User.class), email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 添加用户
	 */
	public void add(User user) {
		try {
			String sql = "insert into tb_user values(?,?,?,?,?,?)";
			Object[] params = {user.getUid(), user.getUsername(), user.getPassword(), user.getEmail(), user.getCode(), user.isState()};
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 功能,按激活码查询
	 */
	public User findByCode(String code) {
		try {
			String sql = "select * from tb_user where code=?";
			User user  =  qr.query(sql, new BeanHandler<User>(User.class), code);
			return user;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 修改指定用户的指定状态
	 */
	public void updateState(String uid, boolean state) {
		try {
			String sql = "update tb_user set state = ? where uid = ?";
			Object[] params = {state, uid};
			qr.update(sql,params);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
