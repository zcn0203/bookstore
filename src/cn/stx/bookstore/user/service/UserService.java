package cn.stx.bookstore.user.service;

import cn.stx.bookstore.user.dao.UserDao;
import cn.stx.bookstore.user.domain.User;

public class UserService {
	
	private UserDao userDao = new UserDao();
/*
 * 注册功能
 */
	public void regist(User form) throws UserException {
		//校验用户名
		User user = userDao.findByUserName(form.getUsername());
		if(user!=null)
			throw new UserException("用户名已存在");
		
		//校验Email
		user = userDao.findByUserEmail(form.getEmail());
		if(user != null)
			throw new UserException("Email已经被注册");
		
		//插入用户到数据库
		userDao.add(form);
	}
	
	/*
	 * 激活功能
	 */
	public void active(String code) throws UserException {
		/*
		 * 1.使用code查询数据库，得到user
		 */
		User user = userDao.findByCode(code);
		/*
		 * 2.若果user不存在，说明激活码错误
		 */
		if(user == null) {
			throw new UserException("激活码无效");
		}
		/*
		 * 校验用户的状态是否为激活状态，如果已经激活，抛出异常
		 */
		if(user.isState()) {
			throw new UserException("你已经激活激活码已存在");
		}
		/*
		 * 修改用户的状态
		 */
		userDao.updateState(user.getUid(), true);
		
	}
	
	/**
	 * 登录功能
	 * @throws UserException 
	 */
	public User login(User form) throws UserException {
		/*
		 * 使用username查询，得到User
		 * 如果user为null，抛出异常（用户名不存在）
		 * 比较form的user的密码，若不同	，抛出密码错误异常
		 * 查看用户的状态，如果为false，抛出尚未激活异常
		 * 返回user
		 */
		User user = userDao.findByUserName(form.getUsername());
		if(user == null) throw new UserException("用户名不存在");
		if(!user.getPassword().equals(form.getPassword()))
			throw new UserException("密码错误");
		if(!user.isState()) throw new UserException("用户尚未激活");
		
		return user;
		
	}
}
