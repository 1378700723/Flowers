/**
 * 
 */
package org.guyou.util;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @author 朱施健
 *
 */
public class StringHashSet extends LinkedHashSet<String> {
	private static final long	serialVersionUID	= 4891031953898901893L;

	public StringHashSet(){
	}
	
	public StringHashSet(Collection<String> c) {
		super(c);
	}
	
	public StringHashSet(String values){
		if(values!=null&&!"".equals(values)){
			String[] datas = values.split(",");
			for ( String string : datas ) {
				if("".equals(string)) continue;
				add(string);
			}
		}
	}
	
	public StringHashSet addElement(String e){
		add(e);
		return this;
	}
	
	public StringHashSet addElements(String values){
		if(values!=null&&!"".equals(values)){
			String[] datas = values.split(",");
			for ( String string : datas ) {
				if("".equals(string)) continue;
				add(string);
			}
		}
		return this;
	}
	
	public String toString2(){
		StringBuilder sb = new StringBuilder();
		for ( String str : this ) {
			sb.append(",").append(str);
		}
		if(sb.length()>0){
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}
}
