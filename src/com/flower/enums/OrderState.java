/**
 * create by 朱施健
 */
package com.flower.enums;

/**
 * @author 朱施健
 *
 */
public enum OrderState {
	未支付(1),
	已支付未送货(2),
	已支付需要送货(3),
	已支付正在送货(4),
	已支付完成收货(5),
	;
	
	public final byte state;
	OrderState(int state){
		this.state = (byte) state;
	}
	
	public String toString(){
		return Byte.toString(state);
	}
	
	public static OrderState getEnum(int state){
		for (OrderState t : OrderState.values()) {
			if(t.state == state) return t;
		}
		throw new NullPointerException("订单状态["+state+"]无对应的枚举");
	}
}
