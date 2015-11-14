/**
 * 
 */
package org.guyou.event;

/**
 * @author 朱施健
 *
 */
public interface Event extends Runnable {
	public Object getSource();
	public String getType();
}
