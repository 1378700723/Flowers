/**
 * create by 朱施健
 */
package com.flower;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.ServletContextKeyEnum;
import org.guyou.web.server.WebStartListening;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.flower.admin.beans.AdminMenu;
import com.flower.customer.beans.CustomerUser;
import com.flower.tables.AppSetting;

/**
 * @author 朱施健
 *
 */
@WebStartListening
public class Application {
	
	private static final Logger log = Logger.getLogger(Application.class);
	
	private static ServletContext _servletContext;
	private static Map<String,AdminMenu> _menuList = new LinkedHashMap<String,AdminMenu>();
	private static Map<String,String> _modulepath_menuid_relations = new HashMap<String, String>();
	private static AppSetting _appSetting;
	
	
	protected static void configure(ServletContext context) {
		_servletContext = context;
	}
	
	protected static void init() {
		initMenuTemplates();
		initAppSetting();
	}

	public static ServletContext currentServletContext(){
		return _servletContext;
	}
	
	public static Map<String,AdminMenu> getMenuTemplates(){
		return _menuList;
	}
	
	public static String getMenuidByPath(String modulePath){
		return _modulepath_menuid_relations.get(modulePath);
	}
	
	public static AppSetting getAppSetting(){
		return _appSetting;
	}
	
//	public static CustomerUser getCustomerUser(HttpServletRequest request){
//		
//		CHECK_SESSION_URI_LIST = (Set<String>) arg0.getServletContext().getAttribute(ServletContextKeyEnum.CHECK_SESSION_URI_LIST);
//		
//	}
	
	private static void initMenuTemplates(){
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream cfgFile = new FileInputStream(new File(_servletContext.getRealPath("/WEB-INF/config/menus.xml")));
			Document doc = builder.parse(cfgFile);
			cfgFile.close();
			NodeList nodeList = doc.getElementsByTagName("menu");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element item = (Element)nodeList.item(i);
				AdminMenu menu = new AdminMenu();
				menu.menuID = item.getAttribute("id");
				menu.menuName = item.getAttribute("name");
				NodeList nodeList2 = item.getElementsByTagName("module");
				for (int j = 0; j < nodeList2.getLength(); j++) {
					Element item2 = (Element)nodeList2.item(j);
					AdminMenu.AdminModule module = new AdminMenu.AdminModule();
					module.moduleID = item2.getAttribute("id");
					module.moduleName = item2.getAttribute("name");
					module.modulePath = item2.getAttribute("path");
					menu.modules.put(module.moduleID, module);
					_modulepath_menuid_relations.put(module.modulePath, menu.menuID);
				}
				_menuList.put(menu.menuID, menu);
			}
		} catch (Exception e) {
			log.error("初始化menus.xml异常",e);
		}
	}
	
	private static void initAppSetting() {
		try {
			Session session = HibernateSessionFactory.getSession();
			_appSetting = (AppSetting) session.get(AppSetting.class, AppSetting.FIXED_ID);
			if(_appSetting==null){
				_appSetting = new AppSetting();
				Transaction t = session.getTransaction();
				session.save(_appSetting);
				t.commit();
			}
		} finally {
			HibernateSessionFactory.closeSession();
		}
	}
}
