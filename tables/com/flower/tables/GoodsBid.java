package com.flower.tables;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.flower.enums.GoodsBidState;
/**
 * @author 王雪冬
 * 竞拍记录
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class GoodsBid {

	public String bidid;
	//用户编号(竞拍者)
	public String uid;
	//竞拍价
    public int  bidPrice; 
    //竞拍时间
  	public String createTime;
    //竞拍商品
  	public GoodsAuction goodsAcution;
  	//竞拍状态
  	public GoodsBidState state;
  	//通知时间
  	public String informTime;
  	//是否支付
  	public boolean isPay;
	
  	@Id
	@Column(length=50,nullable=false,updatable=false)
  	public String getBidid() {
		return bidid;
	}
	public void setBidid(String bidid) {
		this.bidid = bidid;
	}
	@Column
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	@Column
	public int getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(int bidPrice) {
		this.bidPrice = bidPrice;
	}
	@Column
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name ="goodsAcutionId",nullable=false)
	public GoodsAuction getGoodsAcution() {
		return goodsAcution;
	}
	public void setGoodsAcution(GoodsAuction goodsAcution) {
		this.goodsAcution = goodsAcution;
	}
	@Column(nullable=false)
	public byte getState() {
		return state.state;
	}
	public void setState(byte state) {
		this.state = GoodsBidState.getEnum(state);
	}
	@Column
	public String getInformTime() {
		return informTime;
	}
	public void setInformTime(String informTime) {
		this.informTime = informTime;
	}
	@Column
	public boolean isPay() {
		return isPay;
	}
	public void setPay(boolean isPay) {
		this.isPay = isPay;
	}
  	
  	
  	
}
