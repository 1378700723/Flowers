/**
 * 
 */
package org.guyou.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author 朱施健
 *
 */
public class StringArrayList extends ArrayList<String> {
	private static final long	serialVersionUID	= -6223076256476354236L;

	public StringArrayList(){
	}
	
	public StringArrayList(Collection<String> c) {
		super(c);
	}
	
	public StringArrayList(String values){
		if(values!=null&&!"".equals(values)){
			String[] datas = values.split(",");
			for ( String string : datas ) {
				if("".equals(string)) continue;
				add(string);
			}
		}
	}
	
	public StringArrayList addElement(String e){
		add(e);
		return this;
	}
	
	public StringArrayList addElements(String values){
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
