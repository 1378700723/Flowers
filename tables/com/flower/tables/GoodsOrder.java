package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.guyou.util.StringUtil;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.flower.enums.OrderState;
/**
 * @author 朱施健
 * 订单表
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class GoodsOrder {
    
	//订单号 
	public String orderid;
	//用户编号
	public String uid;
	//状态
	public OrderState state;
	//订单时间
	public String orderTime;
	//支付时间
	public String payTime;
    //商品实例编号
	public String[] goodsIds;	
	//数量
	public int goodsCount;
	//单价
	public float unitPrice;
	//总价
	public float totalPrice;
	//支付金额
	public float payMoney;
	//使用花籽数
	public int useFlowerSeedCount;
	//花籽抵扣金额
	public float deductibleMoney;
	
	@Id
	@Column(length=50,nullable=false,updatable=false)
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	
	@Column(length=50,nullable=false,updatable=false)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(nullable=false)
	public byte getState() {
		return state.state;
	}
	public void setState(byte state) {
		this.state = OrderState.getEnum(state);
	}
	
	@Column(length=19,nullable=false,updatable=false)
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	
	@Column(length=19,nullable=false)
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	
	@Column
	public String getGoodsIds() {
		return (goodsIds==null || goodsIds.length==0) ? null : StringUtils.join(goodsIds,",");
	}
	public void setGoodsIds(String goodsIds) {
		if(!StringUtil.isNullValue(goodsIds)){
			this.goodsIds = StringUtil.toStringArray(goodsIds, ",");
		}
	}
	
	@Column(nullable=false)
	public int getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}
	
	@Column(columnDefinition="float(11,2) not null default 0")
	public float getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	@Column(columnDefinition="float(11,2) not null default 0")
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	@Column(columnDefinition="float(11,2) not null default 0")
	public float getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(float payMoney) {
		this.payMoney = payMoney;
	}
	
	@Column
	public int getUseFlowerSeedCount() {
		return useFlowerSeedCount;
	}
	public void setUseFlowerSeedCount(int useFlowerSeedCount) {
		this.useFlowerSeedCount = useFlowerSeedCount;
	}
	
	@Column(columnDefinition="float(11,2) not null default 0")
	public float getDeductibleMoney() {
		return deductibleMoney;
	}
	public void setDeductibleMoney(float deductibleMoney) {
		this.deductibleMoney = deductibleMoney;
	}
}
