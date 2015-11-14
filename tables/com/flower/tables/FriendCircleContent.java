/**
 * 
 */
package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.guyou.util.StringUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Index;

import com.flower.enums.FriendCircleOpt;

/**
 * @author 朱施健
 * 朋友圈内容
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
@Entity
@DynamicUpdate
@DynamicInsert
public class FriendCircleContent {
	//内容编号
	public String id;
	//发布者
	public String publisher;
	//发布时间
	public String time;
	//操作
	public FriendCircleOpt opt;
	//根内容编号
	public String rootid;
	//回复内容编号
	public String replyid;
	//图片
	public String[] images;
	//内容
	public String content;
	
	@Id
	@Column(length = 50, nullable = false, updatable = false)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Index(name="index_publisher")
	@Column(length = 50, nullable = false, updatable = false)
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	@Index(name="index_time")
	@Column(length = 19, nullable = false, updatable = false)
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	@Index(name="index_opt")
	@Column(nullable = false, updatable = false)
	public byte getOpt() {
		return opt.opt;
	}
	public void setOpt(byte opt) {
		this.opt = FriendCircleOpt.getEnum(opt);
	}
	
	@Index(name="index_rootid")
	@Column(length = 50, updatable = false)
	public String getRootid() {
		return rootid;
	}
	public void setRootid(String rootid) {
		this.rootid = rootid;
	}
	
	@Index(name="index_replyid")
	@Column(length = 50,updatable = false)
	public String getReplyid() {
		return replyid;
	}
	public void setReplyid(String replyid) {
		this.replyid = replyid;
	}
	
	@Column(length=1024)
	public String getImages() {
		return (images==null || images.length==0) ? null : StringUtils.join(images,",");
	}
	public void setImages(String images) {
		if(!StringUtil.isNullValue(images)){
			this.images = StringUtil.toStringArray(images, ",");
		}
	}
	
	@Column(length=4096)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
