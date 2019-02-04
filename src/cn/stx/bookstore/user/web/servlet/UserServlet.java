package cn.stx.bookstore.user.web.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;
import cn.stx.bookstore.cart.domain.Cart;
import cn.stx.bookstore.user.domain.User;
import cn.stx.bookstore.user.service.UserException;
import cn.stx.bookstore.user.service.UserService;

/*
 * User表述层
 */
@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	
	/*
	 * 退出功能
	 */
	public String quit(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().invalidate();
		return "r:/index.jsp";
	}
	
	/*
	 *	登录功能 
	 */
	public String login(HttpServletRequest request, HttpServletResponse response) {
		/*
		 * 封装表单数据到form中
		 * 输入校验
		 * 调用service完成激活
		 *   保存错误信息，form到request，转发到login
		 * 保存用户信息到session中，然后重定向到index。jsp
		 */
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		
		try {
			User user = userService.login(form);
			request.getSession().invalidate();
			request.getSession().setAttribute("session_user", user);
			/*
			 * 在session中添加购物车，即向session中保存cart对象
			 */
			request.getSession().setAttribute("cart", new Cart());
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/login.jsp";
		}
	}
	/**
	 * 激活功能
	 */
	public String active(HttpServletRequest request, HttpServletResponse response) {
		/*
		 * 1.获取参数激活码
		 * 2.调用service方法完成激活
		 * 	保存异常信息到request域，转发到msg。jsp
		 * 3.保存成功信息到request域，转发到msg.jsp
		 */
		String code = request.getParameter("code");
		try {
			userService.active(code);
			request.setAttribute("msg", "恭喜您已经激活成功！");
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}

	/**
	 * 注册功能
	 */
	public String regist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 封装表单数据到form对象中 补全uid和code 输入校验 保存错误信息，form到request域，转发到regist 调用service方法完成注册
		 * 保存错误信息到form到request域，转发到regist 发邮件 保存成功信息转发到msg。jsp
		 */
		// 封装表单数据
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		// 补全
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		/*
		 * 输入校验 创建一个map用来封装错误信息，其中key为表单字段名称，值为错误信息
		 */
		Map<String, String> errors = new HashMap<String, String>();
		// 获取form中的username，password，email进行校验
		String username = form.getUsername();
		if (username == null || username.trim().isEmpty()) {
			errors.put("username", "用户名不能为空！");
		} else if (username.length() < 3 || username.length() > 10) {
			errors.put("username", "用户名长度必须在3-10之间 ");
		}

		String password = form.getPassword();
		if (password == null || username.trim().isEmpty()) {
			errors.put("password", "密码不能为空！");
		} else if (password.length() < 3 || password.length() > 10) {
			errors.put("password", "密码长度必须在3-10之间 ");
		}

		String email = form.getEmail();
		if (email == null || email.trim().isEmpty()) {
			errors.put("email", "邮箱不能为空！");
		} else if (!email.matches("\\w+@\\w+\\.\\w+")) {
			errors.put("email", "邮箱格式错误");
		}
		/*
		 * 判断是否存在错误信息
		 */
		if(errors.size()>0) {
			//保存错误信息
			request.setAttribute("errors", errors);
			//保存表单数据
			request.setAttribute("form", form);
			//转发到regist
			return "f:/jsps/user/regist.jsp";
		}
		
		/*
		 * 调用service的regist方法
		 */
		try {
			userService.regist(form);
			/*
			 * 执行到这里说明userService执行成功，没有抛出异常
			 */
		} catch (UserException e) {
			//保存异常信息
			request.setAttribute("msg", e.getMessage());
			//保存form
			request.setAttribute("form", form);
			//转发
			return "f:jsps/user/regist.jsp";
		}
		/*
		 * 发送激活邮件
		 * 准备配置文件
		 */
		//获取配置文件内容
		Properties prop = new Properties();
		prop.load(this.getClass()
				.getClassLoader()
				.getResourceAsStream("email_template.properties"));
		String host = prop.getProperty("host");//获取服务器主机
		String uname = prop.getProperty("uname");
		String pwd = prop.getProperty("pwd");
		String from = prop.getProperty("from");//获取发件人
		String to = form.getEmail();//获取收件人
		String subject = prop.getProperty("subject");//获取主题
		String content = prop.getProperty("content");//获取内容
		//替换配置文件中的占位符{0}
		content = MessageFormat.format(content, form.getCode());
		System.out.println(content);
		//发送邮件
		Session session = MailUtils.createSession(host, uname, pwd);//得到Session
		Mail mail = new Mail(from, to, subject, content);
		try {
			MailUtils.send(session, mail);//发邮件
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		//保存成功信息，转发到msg。jsp
		request.setAttribute("msg", "恭喜，注册成功，请到邮箱中激活");
		return "f:jsps/msg.jsp";
	}
}