/**
 * create by 朱施健
 */
package com.flower.customer.vo;



/**
 * @author 朱施健
 * 用户公开信息VO
 */
public class CustomerOpenInfoVo extends CustomerIndexVo{
	//邮箱
	public String email;
	//相册
	public String[] images;
	//喜欢的花
	public String likeFlower;
	//签名
	public String signature;
	//是否是好友
	public boolean isFriend;
	
}
