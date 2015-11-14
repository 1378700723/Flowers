/**
 * create by 朱施健
 */
package com.flower.tables;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.ArrayUtils;
import org.guyou.util.SerializeUtil;
import org.guyou.util.StringUtil;
import org.guyou.web.server.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.alibaba.fastjson.JSONArray;
import com.flower.Application;

/**
 * @author 朱施健
 *
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
@Entity
@DynamicUpdate
@DynamicInsert
public class AppSetting {
	
	public static final int FIXED_ID = 1;
	
	private int id = FIXED_ID;
	//首页活动集合 key=ProductTagType枚举的tag，value=
	public int[] homePageActivitys;
	//首页特价商品模板编号集合
	public int[] homePageBargainGoods;
	//首页产品标签集合 key=ProductTagType枚举的tag，value=
	public Map<String,int[]> homePageProductTags;
	
	
	
	@Id
	@Column(nullable=false,updatable=false)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column
	public String getHomePageActivitys() {
		return (homePageActivitys==null || homePageActivitys.length==0)?null:SerializeUtil.objectToJsonString(homePageActivitys);
	}
	public void setHomePageActivitys(String homePageActivitys) {
		if(!StringUtil.isNullValue(homePageActivitys)){
			this.homePageActivitys = SerializeUtil.jsonStringToObject(homePageActivitys, int[].class);
		}
	}
	
	@Column
	public String getHomePageBargainGoods() {
		return (homePageBargainGoods==null || homePageBargainGoods.length==0)?null:SerializeUtil.objectToJsonString(homePageBargainGoods);
	}
	public void setHomePageBargainGoods(String homePageBargainGoods) {
		if(!StringUtil.isNullValue(homePageBargainGoods)){
			this.homePageBargainGoods = SerializeUtil.jsonStringToObject(homePageBargainGoods, int[].class);
		}
	}
	
	@Column
	public String getHomePageProductTags() {
		return (homePageProductTags==null || homePageProductTags.isEmpty())?null:SerializeUtil.objectToJsonString(homePageProductTags);
	}
	public void setHomePageProductTags(String homePageProductTags) {
		if(!StringUtil.isNullValue(homePageProductTags)){
			Map<String,JSONArray> tmp = SerializeUtil.jsonStringToObject(homePageProductTags, Map.class);
			if(this.homePageProductTags==null) this.homePageProductTags = new LinkedHashMap<String,int[]>();
			for (Iterator<Entry<String,JSONArray>> iterator = tmp.entrySet().iterator(); iterator.hasNext();) {
				Entry<String,JSONArray> e = iterator.next();
				this.homePageProductTags.put(e.getKey(), ArrayUtils.toPrimitive(e.getValue().toArray(new Integer[0])));
			}
		}
	}
	
	/**
	 * 更新
	 */
	public void update(){
		try {
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.getTransaction();
			session.update(Application.getAppSetting());
			t.commit();
		} finally {
			HibernateSessionFactory.closeSession();
		}
	}
}
