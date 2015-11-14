/**
 * create by 朱施健
 */
package com.flower.enums;

/**
 * @author 朱施健
 *
 */
public enum FlowerType {
	玫瑰("meigui"),
	百合("baihe"),
	康乃馨("kangnaixin"),
	菊花("juhua"),
	兰花("lanhua"),
	;
	
	public final String type;
	FlowerType(String type){
		this.type=type;
	}
	
	public String toString(){
		return this.name();
	}
	
	public static FlowerType getEnum(String type){
		for (FlowerType t : FlowerType.values()) {
			if(t.type.equals(type)) return t;
		}
		throw new NullPointerException("鲜花类型["+type+"]无对应的枚举");
	}
}
