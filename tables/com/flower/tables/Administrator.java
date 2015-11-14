/**
 * create by 朱施健
 */
package com.flower.tables;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.flower.admin.beans.AdminMenu;
import com.flower.admin.beans.AdminMenu.AdminModule;

/**
 * @author 朱施健
 * 后台管理员
 */
// @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
@Entity
@DynamicUpdate
@DynamicInsert
public class Administrator {
	public String username;
	public String passwd;
	public List<Permission> permissions;

	@Id
	@Column(length = 60, nullable = false, updatable = false)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(length = 50, nullable = false)
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinTable(name = "Relations_Administrator_Permission", joinColumns = { @JoinColumn(name = "administrator_username") }, inverseJoinColumns = { @JoinColumn(name = "permission_id") })
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	private List<AdminMenu> __cacheMenu;
	public List<AdminMenu> menus(){
		if(__cacheMenu==null){
			Map<String,AdminMenu> list = new LinkedHashMap<String,AdminMenu>();
			for (Permission p : permissions) {
				for (AdminMenu menu : p.menuList.values()) {
					AdminMenu cache_menu = list.get(menu.menuID);
					if(cache_menu==null){
						cache_menu = new AdminMenu();
						cache_menu.menuID = menu.menuID;
						list.put(menu.menuID, cache_menu);
					}
					cache_menu.menuName = menu.menuName;
					for(AdminModule md : menu.modules.values()){
						if(!cache_menu.modules.containsKey(md.moduleID))cache_menu.modules.put(md.moduleID, md);
					}
				}
			}
			__cacheMenu = new ArrayList<AdminMenu>(list.values());
		}
		return __cacheMenu;
	}
}
