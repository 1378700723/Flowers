/**
 * create by 朱施健
 */
package com.flower.tables;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.log4j.Logger;
import org.guyou.util.ConfigUtil;
import org.guyou.util.SerializeUtil;
import org.guyou.util.StringUtil;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.flower.Application;
import com.flower.admin.beans.AdminMenu;

/**
 * @author 朱施健
 * 权限
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class Permission {
	public int id;
	public String name;
	public Map<String,AdminMenu> menuList = new LinkedHashMap<String,AdminMenu>();
	
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
	
	@Column(length=50,nullable=false,unique=true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if(this.menuList.isEmpty() && ____pmodules_db_datas!=null){
			_setPmodules();
		}
	}
	
	private String ____pmodules_db_datas = null;
	
	@Column(name="pmodules",length=2048)
	public String getPmodules() {
		if(ConfigUtil.getConfigParam("ADMIN.SUPER.PERMISSION.NAME").equals(name)){
			return "";
		}else{
			return SerializeUtil.objectToJsonString(menuList.values().toArray(new AdminMenu[0]));
		}
	}
	public void setPmodules(String modules) {
		____pmodules_db_datas = modules;
		if(!StringUtil.isNullValue(name)){
			_setPmodules();
		}
	}
	
	private void _setPmodules(){
		if(ConfigUtil.getConfigParam("ADMIN.SUPER.PERMISSION.NAME").equals(name)){
			this.menuList.putAll(Application.getMenuTemplates());
		}else{
			AdminMenu[] menus = SerializeUtil.jsonStringToObject(____pmodules_db_datas, AdminMenu[].class);
			for (AdminMenu m : menus) {
				this.menuList.put(m.menuID,m);
			}
		}
		____pmodules_db_datas = null;
	}
}
