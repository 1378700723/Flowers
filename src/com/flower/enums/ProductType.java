package com.flower.enums;

public enum ProductType {
	鲜花(1),
	绿植(2),
	服务(3),
	;
	public final byte type;
	ProductType(int type){
		this.type = (byte) type;
	}
	
	public static ProductType getEnum(int type){
		for (ProductType t : ProductType.values()) {
			if(t.type == type) return t;
		}
		throw new NullPointerException("产品类型["+type+"]无对应的枚举");
	}
}
