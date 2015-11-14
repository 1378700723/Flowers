/**
 * create by 朱施健
 */
package com.flower.tables;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.apache.commons.lang3.StringUtils;
import org.guyou.util.StringUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.flower.customer.vo.CustomerIndexVo;
import com.flower.enums.RegisteChannel;
import com.flower.enums.SexType;

/**
 * @author 朱施健
 * 普通用户
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
@Entity
@DynamicUpdate
@DynamicInsert
//@Table(uniqueConstraints={@UniqueConstraint(name="unique_index_userName_phone",columnNames={"userName", "phone"})})
public class Customer {
	//用户编号
	public String uid;
	public String phone;
	public String email;
	public String passwd;
	//昵称
	public String nickname;
	public String headIcon;
	public String[] images;
	public String regtime;
	public SexType sex = SexType.无;
	public RegisteChannel channel = RegisteChannel.手机注册;
	//喜欢的花
	public String likeFlower;
	//签名
	public String signature;
	//花籽
	public int flowerSeed;
	//是否开启消息提醒
	public boolean isOpenMsgAlert;
	//是否接收陌生人消息
	public boolean isReceiveStrangerMsg;
	//是否可以通过手机号找到我
	public boolean isCanSearchByPhone;
	//常用收货地址
	public Map<Integer,DeliveryAddress> addresses;
	//收藏的商品
	public Map<Integer,GoodsTemplate> favorites;
	
	
	@Id
	@Column(length = 50, nullable = false, updatable = false)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(length = 50, nullable = false, updatable = false,unique=true)
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Column(length = 100)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(length = 60, nullable = false)
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	@Column(length = 60)
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	@Column
	public String getHeadIcon() {
		return headIcon;
	}
	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}
	
	@Column(length=2048)
	public String getImages() {
		return (images==null || images.length==0) ? "" : StringUtils.join(images,",");
	}
	public void setImages(String images) {
		if(!StringUtil.isNullValue(images)){
			this.images = images.split(",");
		}
	}
	
	@Column(length = 19, nullable = false,updatable=false)
	public String getRegtime() {
		return regtime;
	}
	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}
	
	@Column(nullable = false)
	public byte getSex() {
		return sex.flag;
	}
	public void setSex(byte flag) {
		this.sex = SexType.getSex(flag);
	}
	
	@Column(nullable = false)
	public byte getChannel() {
		return channel.channel;
	}
	public void setChannel(byte channel) {
		this.channel = RegisteChannel.getEnum(channel);
	}
	
	@Column(length = 50)
	public String getLikeFlower() {
		return likeFlower;
	}
	public void setLikeFlower(String likeFlower) {
		this.likeFlower = likeFlower;
	}
	
	@Column
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	@Column(nullable = false)
	public int getFlowerSeed() {
		return flowerSeed;
	}
	public void setFlowerSeed(int flowerSeed) {
		this.flowerSeed = flowerSeed;
	}
	
	@Column
	public boolean isOpenMsgAlert() {
		return isOpenMsgAlert;
	}
	public void setOpenMsgAlert(boolean isOpenMsgAlert) {
		this.isOpenMsgAlert = isOpenMsgAlert;
	}
	
	@Column
	public boolean isReceiveStrangerMsg() {
		return isReceiveStrangerMsg;
	}
	public void setReceiveStrangerMsg(boolean isReceiveStrangerMsg) {
		this.isReceiveStrangerMsg = isReceiveStrangerMsg;
	}
	
	@Column
	public boolean isCanSearchByPhone() {
		return isCanSearchByPhone;
	}
	public void setCanSearchByPhone(boolean isCanSearchByPhone) {
		this.isCanSearchByPhone = isCanSearchByPhone;
	}
	
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="uid")
	@MapKey(name="id")
	@OrderBy("id")
	public Map<Integer,DeliveryAddress> getAddresses() {
		return addresses;
	}
	public void setAddresses(Map<Integer,DeliveryAddress> addresses) {
		this.addresses = addresses;
	}
	
	@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "Relations_Customer_GoodsTemplate", joinColumns = { @JoinColumn(name = "uid") }, inverseJoinColumns = { @JoinColumn(name = "goods_template_id") })
	@MapKey(name="id")
	@OrderBy("id")
	public Map<Integer,GoodsTemplate> getFavorites() {
		return favorites;
	}
	public void setFavorites(Map<Integer,GoodsTemplate> favorites) {
		this.favorites = favorites;
	}
	
	public Map<String,Object> toJsonObject(){
		Map<String, Object> jsonObject = new HashMap<String, Object>();
		jsonObject.put("uid",uid);
		jsonObject.put("phone",StringUtil.getNotNull(phone));
		jsonObject.put("email",StringUtil.getNotNull(email));
		jsonObject.put("passwd",StringUtil.getNotNull(passwd));
		jsonObject.put("nickname",StringUtil.getNotNull(nickname));
		jsonObject.put("headIcon",StringUtil.getNotNull(headIcon));
		jsonObject.put("images",images==null?new String[0]:images);
		jsonObject.put("sex",sex.flag);
		jsonObject.put("likeFlower",StringUtil.getNotNull(likeFlower));
		jsonObject.put("signature",StringUtil.getNotNull(signature));
		jsonObject.put("flowerSeed",flowerSeed);
		jsonObject.put("isOpenMsgAlert",isOpenMsgAlert);
		jsonObject.put("isReceiveStrangerMsg",isReceiveStrangerMsg);
		jsonObject.put("isCanSearchByPhone",isCanSearchByPhone);
		return jsonObject;
	}
	
	public CustomerIndexVo toCustomerIndexVo(){
		CustomerIndexVo vo = new CustomerIndexVo();
		vo.uid = this.uid;
		vo.phone = this.phone;
		vo.nickname = this.nickname;
		vo.headIcon = this.headIcon;
		vo.sex = this.sex.flag;
		return vo;
	}
}
