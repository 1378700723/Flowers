package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 加入合作
 * @author 王雪冬
 *
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class Teamwork {

	public int id;
	
	public String flowerStoreName;
	
	public String userName;
	
	public String phone;
	
	public String qqWinx;
	
	public String picture;
	
	public String address;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "persistenceGenerator", strategy = "increment")
	@Column(nullable=false,updatable=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Column
	public String getFlowerStoreName() {
		return flowerStoreName;
	}

	public void setFlowerStoreName(String flowerStoreName) {
		this.flowerStoreName = flowerStoreName;
	}
	@Column
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Column
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column
	public String getQqWinx() {
		return qqWinx;
	}

	public void setQqWinx(String qqWinx) {
		this.qqWinx = qqWinx;
	}
	@Column
	public String getPicture() {
		return picture;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	@Column
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
