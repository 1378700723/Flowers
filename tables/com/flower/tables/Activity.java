/**
 * 
 */
package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

/**
 * @author 朱施健
 *
 */
@Entity
public class Activity {
	public int id;
	public String title;
	public String picture;
	public String url;
	public String publishTime;
	public boolean isDelete;
	
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
	
	@Column(nullable=false)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(nullable=false)
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	@Column(nullable=false)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Index(name="index_publishTime")
	@Column(length=19,nullable=false,updatable=false)
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	
	@Column(nullable=false)
	public byte getIsDelete() {
		return isDelete ? (byte)1 : (byte)0;
	}
	public void setIsDelete(byte isDelete) {
		this.isDelete = isDelete==1;
	}
}
