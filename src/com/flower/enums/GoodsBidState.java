package com.flower.enums;

public enum GoodsBidState {
	竞拍中(1),
	竞拍成功(2),
	竞拍失败(3),
	;
	
	public final byte state;
	GoodsBidState(int state){
		this.state = (byte) state;
	}
	
	public String toString(){
		return Byte.toString(state);
	}
	
	public static GoodsBidState getEnum(int state){
		for (GoodsBidState t : GoodsBidState.values()) {
			if(t.state == state) return t;
		}
		throw new NullPointerException("竞拍状态["+state+"]无对应的枚举");
	}
}
