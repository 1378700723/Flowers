package org.guyou.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.guyou.util.ClassUtil;
import org.guyou.util.FileUtil;
import org.guyou.util.StringUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

/**
 * Configures and provides access to Hibernate sessions, tied to the
 * current thread of execution.  Follows the Thread Local Session
 * pattern, see {@link http://hibernate.org/42.html }.
 */
public class HibernateSessionFactory {

	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
    private static org.hibernate.SessionFactory sessionFactory;
	
    private static String hibernate_cfg_xml;
    private static Configuration configuration = new Configuration();
    private static ServiceRegistry serviceRegistry; 
	
    private HibernateSessionFactory() {
    }
    
    public static void configure(String hibernate_cfg_xml_source) {
    	hibernate_cfg_xml = hibernate_cfg_xml_source;
    	rebuildSessionFactory();
    }
	
    public static Session getSession() throws HibernateException {
        Session session = threadLocal.get();

		if (session == null || !session.isOpen()) {
			if (sessionFactory == null) {
				rebuildSessionFactory();
			}
			session = (sessionFactory != null) ? sessionFactory.openSession()
					: null;
			threadLocal.set(session);
		}

        return session;
    }

	private static void rebuildSessionFactory() {
		try {
			configuration.configure(getHibernateCfgXmlDocument());
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Exception e) {
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}
	
	public static void shutdown(){
		C3P0ConnectionProvider connectionProvider = (C3P0ConnectionProvider) serviceRegistry.getService(ConnectionProvider.class);
		RegionFactory regionFactory = serviceRegistry.getService(RegionFactory.class);
		if(connectionProvider!=null) connectionProvider.stop();
		if(regionFactory!=null) regionFactory.stop();
		if(sessionFactory!=null) sessionFactory.close();
		for(Enumeration<Driver> drivers=DriverManager.getDrivers();drivers.hasMoreElements();){
			Driver driver =drivers.nextElement();
			if(driver!=null){
				if(driver.getClass().getName().toLowerCase().contains("mysql")){
					try {
						Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("com.mysql.jdbc.AbandonedConnectionCleanupThread");
						ClassUtil.excuteFunction(clazz, "shutdown");
					} catch (Exception e) {
						System.err.println("%%%% Error shutdown MySql connection thread %%%%");
						e.printStackTrace();
					}
				}
				try {
					DriverManager.deregisterDriver(driver);
				} catch (Exception ee) {
					System.err.println("%%%% Error unregister JBDC driver %%%%");
					ee.printStackTrace();
				}
			}
		}
		threadLocal.remove();
	}

    public static void closeSession() throws HibernateException {
        Session session = threadLocal.get();
        threadLocal.set(null);

        if (session != null) {
        	session.clear();
            session.close();
        }
    }

	public static org.hibernate.SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static Configuration getConfiguration() {
		return configuration;
	}
	
	private static Document getHibernateCfgXmlDocument() throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputStream cfgFile = FileUtil.getResourceAsStream(hibernate_cfg_xml);
		Document doc = builder.parse(cfgFile);
		cfgFile.close();
		NodeList nodeList = doc.getElementsByTagName("property");
		String mappings_source = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element item = (Element)nodeList.item(i);
			String mappings_attribute = item.getAttribute("name");
			if(mappings_attribute.equals("hibernate.mappings") || mappings_attribute.equals("mappings")){
				mappings_source = item.getFirstChild().getNodeValue();
				item.getParentNode().removeChild(item);
				break;
			}
		}
		if(!StringUtil.isNullValue(mappings_source)){
			cfgFile = FileUtil.getResourceAsStream(mappings_source);
			NodeList mappings = builder.parse(cfgFile).getElementsByTagName("mapping");
			cfgFile.close();
			Element session_factory = (Element) doc.getElementsByTagName("session-factory").item(0);
			for (int j = 0; j < mappings.getLength(); j++) {
				Element mapping=doc.createElement("mapping"); 
				mapping.setAttribute("class", ((Element) mappings.item(j)).getAttribute("class"));
				session_factory.appendChild(mapping);
			}
		}
		return doc;
	}
}