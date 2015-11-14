package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
/**
 * @author 王雪冬
 * 对应 百度Lbs 
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class UserPoi {

	public int userId;
	
	public int poiId;
	
	@Id
	@Column
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	 
	@Column
	public int getPoiId() {
		return poiId;
	}

	public void setPoiId(int poiId) {
		this.poiId = poiId;
	}
	
	
	
	
}
