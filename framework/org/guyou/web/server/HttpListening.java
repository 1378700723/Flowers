/**
 * @author 朱施健
 */
package org.guyou.web.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 朱施健
 * 消息监听注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpListening {
	/**
	 * contextpath
	 * @return
	 */
	String urlPattern();
	
	/**
	 * 是否认证session信息是否存在
	 */
	boolean isCheckSession() default false;
}
