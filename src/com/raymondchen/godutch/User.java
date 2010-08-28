package com.raymondchen.godutch;

/**
 * �û���
 * @author Raymond
 *
 */
public class User {
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * �û�ΨһID
	 */
	private Long userId;
	/**
	 * �û�����
	 */
	private String name;
	/**
	 * �û��ʼ�
	 */
	private String email;
	
	/**
	 * �û�ͷ��
	 */
	private byte[] avatar;
	public byte[] getAvatar() {
		return avatar;
	}
	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User u=(User)o;
			return this.userId!=null && this.userId.equals(u.getUserId());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.userId==null?0:this.userId.hashCode();
	}
	
	
}
