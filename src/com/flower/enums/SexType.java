/**
 * 
 */
package com.flower.enums;

/**
 * @author 朱施健
 *
 */
public enum SexType {
	无(0),
	男(1),
	女(2),
	;
	public final byte flag;
	SexType(int flag){
		this.flag = (byte) flag;
	}
	
	public String toString(){
		return Byte.toString(flag);
	}
	
	public static SexType getSex(int flag){
		switch (flag) {
			case 1:
				return 男;
			case 2:
				return 女;
			default:
				return 无;
		}
	}
}
