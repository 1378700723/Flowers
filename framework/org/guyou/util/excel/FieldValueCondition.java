/**
 * 
 */
package org.guyou.util.excel;

/**
 * @author 朱施健
 *
 */
public class FieldValueCondition {
	public String Field;
	public Object value;
	
	public FieldValueCondition(String Field,Object value){
		this.Field = Field;
		this.value = value;
	}
	
	public boolean paramCheck(){
		return Field!=null && !Field.equals("") && value!=null && !value.toString().equals("");
	}
	
	public static boolean paramsCheck(FieldValueCondition[] conditions){
		if(conditions==null || conditions.length==0) return false;
		for(FieldValueCondition c : conditions){
			if(!c.paramCheck()) return false;
		}
		return true;
	}
}
