package com.flower.enums;

public enum GoodsState {
	普通状态(1),
	配送状态(2),
	提货完成(3),
	;
	
	public final byte state;
	GoodsState(int state){
		this.state = (byte) state;
	}
	
	public String toString(){
		return Byte.toString(state);
	}
	
	public static GoodsState getEnum(int state){
		for (GoodsState t : GoodsState.values()) {
			if(t.state == state) return t;
		}
		throw new NullPointerException("商品状态["+state+"]无对应的枚举");
	}
}
