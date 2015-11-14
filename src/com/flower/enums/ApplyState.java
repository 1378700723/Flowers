/**
 * create by 朱施健
 */
package com.flower.enums;

/**
 * @author 朱施健
 * 申请状态
 */
public enum ApplyState {
	等待认证(0),
	通过(1),
	拒绝(2),
	;
	public final byte flag;
	ApplyState(int flag){
		this.flag = (byte) flag;
	}
	
	public String toString(){
		return Byte.toString(flag);
	}
	
	public static ApplyState getEnum(byte flag){
		switch (flag) {
			case 1:
				return 通过;
			case 2:
				return 拒绝;
			default:
				return 等待认证;
		}
	}
}
