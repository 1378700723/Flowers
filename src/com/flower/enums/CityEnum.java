/**
 * create by 朱施健
 */
package com.flower.enums;

/**
 * @author 朱施健
 *
 */
public enum CityEnum {
	北京("010"),
	上海("021"),
	天津("022"),
	广州("020"),
	;
	//区号
	public final String area;
	CityEnum(String area){
		this.area = area;
	}
	
	public static CityEnum getEnum(String area){
		for (CityEnum t : CityEnum.values()) {
			if(t.area.equals(area)) return t;
		}
		throw new NullPointerException("城市区号["+area+"]无对应的枚举");
	}
}
