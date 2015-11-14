/**
 * create by 朱施健
 */
package com.flower.enums;

/**
 * @author 朱施健
 * 申请状态
 */
public enum RegisteChannel {
	手机注册(0),
	QQ注册(1),
	微信注册(2),
	新浪注册(3),
	;
	public final byte channel;
	RegisteChannel(int channel){
		this.channel = (byte) channel;
	}
	
	public String toString(){
		return Byte.toString(channel);
	}
	
	public static RegisteChannel getEnum(int channel){
		for (RegisteChannel t : RegisteChannel.values()) {
			if(t.channel == channel) return t;
		}
		throw new NullPointerException("注册渠道["+channel+"]无对应的枚举");
	}
}
