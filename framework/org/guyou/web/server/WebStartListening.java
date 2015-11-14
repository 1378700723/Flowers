/**
 * 
 */
package org.guyou.web.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 朱施健
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebStartListening {
	/**
	 * 优先级,值越小越优先加载
	 * @return
	 */
	int load_on_startup() default 0;
}
