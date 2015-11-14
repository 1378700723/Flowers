/**
 * create by 朱施健
 */
package com.flower.admin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.guyou.util.ConfigUtil;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.WebStartListening;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.flower.admin.beans.AdminMenu;
import com.flower.tables.Administrator;
import com.flower.tables.Permission;

/**
 * @author 朱施健
 *
 */
@WebStartListening
public class AdminManager {
	
	private static final Logger log = Logger.getLogger(AdminManager.class);
	
	private static Map<String,AdminMenu> _menuList = new LinkedHashMap<String,AdminMenu>();
	
	/**
	 * 初始化
	 */
	public static void init() throws Exception{
		initAdminUser();
	}
	
	private static void initAdminUser(){
		String admin_username = ConfigUtil.getConfigParam("ADMIN.SUPER.USERNAME");
		String admin_password = ConfigUtil.getConfigParam("ADMIN.SUPER.PASSWORD");
		
		Session session = HibernateSessionFactory.getSession();
		Administrator admin = (Administrator) session.get(Administrator.class,admin_username);
		if(admin==null){
			admin = new Administrator();
			admin.setUsername(admin_username);
			admin.setPasswd(admin_password);
			admin.permissions = new ArrayList<Permission>();
			Permission p = new Permission();
			p.name = ConfigUtil.getConfigParam("ADMIN.SUPER.PERMISSION.NAME");
			admin.permissions.add(p);
			Transaction t = session.beginTransaction();
			session.save(admin);
			t.commit();
			log.error("创建默认超级管理员 => username:"+admin_username+" password:"+admin_password);
		}
		session.close();
	}
}
