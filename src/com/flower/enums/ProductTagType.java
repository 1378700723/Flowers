/**
 * create by 朱施健
 */
package com.flower.enums;

/**
 * @author 朱施健
 * 产品标签
 */
public enum ProductTagType {
	送妈妈("smm"),
	情人节("qrj"),
	教师节("jsj"),
	探病("tb"),
	缅怀("mh"),
	特价("tj"),
	;
	
	public final String tag;
	ProductTagType(String tag){this.tag = tag;}
	
	public static ProductTagType getEnum(String tag){
		for (ProductTagType t : ProductTagType.values()) {
			if(t.tag.equals(tag)) return t;
		}
		throw new NullPointerException("标签["+tag+"]无对应的枚举");
	}
	
	public static String[] getLabelNames(String[] labels){
		if(labels==null || labels.length==0) return new String[0];
		String[] labelNames = new String[labels.length];
		for (int i = 0; i < labels.length; i++) {
			labelNames[i] = getEnum(labels[i]).name();
		}
		return labelNames;
	}
}
