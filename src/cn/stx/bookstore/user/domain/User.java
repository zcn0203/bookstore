package cn.stx.bookstore.user.domain;

public class User {
	
	/*
	 * 对应数据库表
	 */
	private String uid;//主键
	private String username;//用户名
	private String password;//密码
	private String email;//邮箱
	private String code;//激活码
	private Boolean state;//状态
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Boolean isState() {
		return state;
	}
	public void setState(Boolean states) {
		this.state = states;
	}
	public User(String uid, String username, String password, String email, String code, Boolean state) {
		super();
		this.uid = uid;
		this.username = username;
		this.password = password;
		this.email = email;
		this.code = code;
		this.state = state;
	}
	public User() {
		super();
	}
	@Override
	public String toString() {
		return "User [uid=" + uid + ", username=" + username + ", password=" + password + ", email=" + email + ", code="
				+ code + ", states=" + state + "]";
	}

}
