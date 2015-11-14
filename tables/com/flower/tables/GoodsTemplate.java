/**
 * create by 朱施健
 */
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

import org.apache.commons.lang3.StringUtils;
import org.guyou.util.StringUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.flower.enums.CityEnum;

/**
 * @author 朱施健
 * 商品模板
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class GoodsTemplate {
	public int id;
	public CityEnum city;
	public int curPrice;
	public int oldPrice;
	public String name;
	//是否是特价商品1是0否
	public boolean isBargain;
	//运费
	public int delivery;
	//评分
	public float grade;
	//月销量
	public int monthSales;
	//花模板数据
	public FlowerTemplate flower;
	//细分类
	public String[] detailClassify=new String[]{"官方标配"};
	//标签集合(便于查找)
    public String [] labels ;
	
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
	
	@Column(length=5,nullable=false)
	public String getCity() {
		return city.area;
	}
	public void setCity(String city) {
		this.city = CityEnum.getEnum(city);
	}
	
	@Column(nullable=false)
	public int getCurPrice() {
		return curPrice;
	}
	public void setCurPrice(int curPrice) {
		this.curPrice = curPrice;
	}
	
	@Column(nullable=false)
	public int getOldPrice() {
		return oldPrice;
	}
	public void setOldPrice(int oldPrice) {
		this.oldPrice = oldPrice;
	}
	@Column(length=50,nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column
	public boolean isBargain() {
		return isBargain;
	}
	public void setBargain(boolean isBargain) {
		this.isBargain = isBargain;
	}
	
	@Column
	public int getDelivery() {
		return delivery;
	}
	public void setDelivery(int delivery) {
		this.delivery = delivery;
	}
	
	@Column(nullable=false,columnDefinition="float(11,1) not null default 0")
	public float getGrade() {
		return grade;
	}
	public void setGrade(float grade) {
		this.grade = grade;
	}
	
	@Column
	public int getMonthSales() {
		return monthSales;
	}
	public void setMonthSales(int monthSales) {
		this.monthSales = monthSales;
	}
	
	@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name ="flowerid",nullable=false)
	public FlowerTemplate getFlower() {
		return flower;
	}
	public void setFlower(FlowerTemplate flower) {
		this.flower = flower;
	}
	
	
	@Column
	public String getDetailClassify() {
		return StringUtils.join(detailClassify, ",");
	}
	public void setDetailClassify(String detailClassify) {
		if(!StringUtil.isNullValue(detailClassify)){
			this.labels = StringUtil.toStringArray(detailClassify, ",");
		}
	}
	
	@Column
	public String getLabels() {
		return StringUtils.join(labels, ",");
	}
	public void setLabels(String labels) {
		if(!StringUtil.isNullValue(labels)){
			this.labels = StringUtil.toStringArray(labels, ",");
		}
	}
	
}
