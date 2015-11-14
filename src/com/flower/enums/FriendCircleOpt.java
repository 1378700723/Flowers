/**
 * 
 */
package com.flower.enums;

/**
 * @author zhush
 *
 */
public enum FriendCircleOpt {
	发布主题(1),
	评论(2),
	回复(3),
	踩(11),
	赞(12),
	送花(13),
	;
	
	public final byte opt;
	FriendCircleOpt(int state){
		this.opt = (byte) state;
	}
	
	public String toString(){
		return Byte.toString(opt);
	}
	
	public static FriendCircleOpt getEnum(int opt){
		for (FriendCircleOpt t : FriendCircleOpt.values()) {
			if(t.opt == opt) return t;
		}
		throw new NullPointerException("朋友圈操作["+opt+"]无对应的枚举");
	}
}
