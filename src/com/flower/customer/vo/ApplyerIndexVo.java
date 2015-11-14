/**
 * create by 朱施健
 */
package com.flower.customer.vo;

/**
 * @author 朱施健
 * 好友申请者索引信息VO
 */
public class ApplyerIndexVo extends CustomerIndexVo {
	//申请消息
	public String applyMsg;
	//等待认证(0), 通过(1), 拒绝(2),
	public byte applyState;
}
