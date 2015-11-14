package com.flower;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.ehcache.internal.util.HibernateUtil;
import org.hibernate.cfg.Settings;


public class EhCacheRegionFactory extends org.hibernate.cache.ehcache.EhCacheRegionFactory {
	@Override
	public void start(Settings settings, Properties properties) throws CacheException {
		String ehcache_xml = Application.currentServletContext().getRealPath("/WEB-INF/config/ehcache.xml");
		this.settings = settings;
        try {
            URL url = null;
            try {
                url = new URL(ehcache_xml);
            }
            catch ( MalformedURLException e ) {
                try {
					url = new File(ehcache_xml).toURL();
				} catch (MalformedURLException e1) {
					throw new CacheException("load ehcache.xml error",e1 );
				}
            }
            Configuration configuration = HibernateUtil.loadAndCorrectConfiguration( url );
            manager = new CacheManager( configuration );
            mbeanRegistrationHelper.registerMBean( manager, properties );
        } catch ( net.sf.ehcache.CacheException e ) {
            if ( e.getMessage().startsWith(
                    "Cannot parseConfiguration CacheManager. Attempt to create a new instance of " +
                            "CacheManager using the diskStorePath"
            ) ) {
                throw new CacheException(
                        "Attempt to restart an already started EhCacheRegionFactory. " +
                                "Use sessionFactory.close() between repeated calls to buildSessionFactory. " +
                                "Consider using SingletonEhCacheRegionFactory. Error from ehcache was: " + e.getMessage()
                );
            }
            else {
                throw new CacheException( e );
            }
        }
	}
}
