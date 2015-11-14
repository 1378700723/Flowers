/**
 * create by 朱施健
 */
package com.flower.enums;


/**
 * @author 朱施健
 *
 */
public enum ResultState {
	Z_正常(1),
	H_会话超时(0),
	Y_用户不存在(-1),
	Y_用户重复(-2),
	S_手机号重复(-3),
	Y_邮箱重复(-4),
	M_密码错误(-5),
	Y_验证码错误(-6),
	S_手机号与验证码不匹配(-7),
	Y_已是好友(-8),
	Y_已在群中(-9),
	Y_不在群中(-10),
	T_图片个数异常(-11),
	F_非多组件请求(-99),
	F_发送验证码失败(-100),
	E_异常(-30000),
	I_IM异常(Short.MIN_VALUE+1),
	Z_数据逻辑错误(Short.MIN_VALUE),
	;
	
	public final int state;
	ResultState(int state){this.state=state;}
	
	public String toString(){
		return Integer.toString(state);
	}
}
