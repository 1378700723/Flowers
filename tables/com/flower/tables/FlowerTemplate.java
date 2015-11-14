/**
 * create by 朱施健
 */
package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.guyou.util.StringHashSet;
import org.guyou.util.StringUtil;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.flower.enums.FlowerType;
import com.flower.enums.ProductType;

/**
 * @author 朱施健
 * 花模板
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class FlowerTemplate {
	//鲜花包括：花名称 花语 种类 场景 图片 尺寸 粗度 卖价 地区
	public int id;
	//名称
	public String name;
	//产品类型
    public ProductType ptype;
    public FlowerType ftype;
    //花语
    public String flowerLanguage;
    //图标
    public String icon;
    //图片
    public String[] images;
    //主材
    public String mainMaterial;
    //辅材
    public String auxiliaryMaterial;
    //工艺
    public String craft;
    //适用场景
    public String scenario;
    //适用对象
    public String suitable;
    //尺寸规格
    public String dimension;
    //描述
    public String des;
    
	
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
	
	@Column(length=50,nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(nullable=false)
	public byte getPtype() {
		return ptype.type;
	}
	public void setPtype(byte type) {
		this.ptype = ProductType.getEnum(type);
	}
	
	@Column
	public String getFtype() {
		return ftype.type;
	}
	public void setFtype(String ftype) {
		this.ftype = FlowerType.getEnum(ftype);
	}
	
	@Column(length=100)
	public String getFlowerLanguage() {
		return flowerLanguage;
	}
	public void setFlowerLanguage(String flowerLanguage) {
		this.flowerLanguage = flowerLanguage;
	}
	
	@Column(nullable=false)
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Column
	public String getImages() {
		return StringUtils.join(images, ",");
	}
	public void setImages(String images) {
		if(!StringUtil.isNullValue(images)){
			this.images = StringUtil.toStringArray(images, ",");
		}
	}
	
	@Column(length=60)
	public String getMainMaterial() {
		return mainMaterial;
	}
	public void setMainMaterial(String mainMaterial) {
		this.mainMaterial = mainMaterial;
	}
	
	@Column(length=60)
	public String getAuxiliaryMaterial() {
		return auxiliaryMaterial;
	}
	public void setAuxiliaryMaterial(String auxiliaryMaterial) {
		this.auxiliaryMaterial = auxiliaryMaterial;
	}
	
	@Column(length=50)
	public String getCraft() {
		return craft;
	}
	public void setCraft(String craft) {
		this.craft = craft;
	}
	
	@Column(length=60)
	public String getSuitable() {
		return suitable;
	}
	public void setSuitable(String suitable) {
		this.suitable = suitable;
	}
	
	@Column(length=60)
	public String getDimension() {
		return dimension;
	}
	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
 
	
	@Column(length=100)
	public String getScenario() {
		return scenario;
	}
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}
	
	@Column
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
}
