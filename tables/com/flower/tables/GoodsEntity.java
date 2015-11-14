/**
 * create by 朱施健
 */
package com.flower.tables;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.flower.enums.GoodsState;

/**
 * @author 朱施健
 * 商品实体
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class GoodsEntity {
	public String id;
	//用户编号
	public String uid;
	//获得时间(yyyy-MM-dd HH:mm:ss)
	public String gainTime;
	//实际付款额
	public int actualPay;
	//状态
	public GoodsState state;
	//商品模板
	public GoodsTemplate goodsTemplate;
	
	@Id
	@Column(length=50,nullable=false,updatable=false)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(length = 50, nullable = false)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(length = 19, nullable = false)
	public String getGainTime() {
		return gainTime;
	}
	public void setGainTime(String gainTime) {
		this.gainTime = gainTime;
	}
	
	@Column(nullable = false)
	public int getActualPay() {
		return actualPay;
	}
	public void setActualPay(int actualPay) {
		this.actualPay = actualPay;
	}
	
	@Column(nullable = false)
	public byte getState() {
		return state.state;
	}
	public void setState(byte state) {
		this.state = GoodsState.getEnum(state);
	}
	
	@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name ="goods_template_id",nullable=false)
	public GoodsTemplate getGoodsTemplate() {
		return goodsTemplate;
	}
	public void setGoodsTemplate(GoodsTemplate goodsTemplate) {
		this.goodsTemplate = goodsTemplate;
	}
}
