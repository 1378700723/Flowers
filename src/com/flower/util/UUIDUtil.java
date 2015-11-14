package com.flower.util;

/**
 * 
 */


import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.guyou.util.MathUtil;

/**
 * @author 朱施健
 *
 */
public enum UUIDUtil {
	管理员ID("a"),
	用户ID("c"),
	商品实例ID("g"),
	订单ID("o"),
	图片名称("m"),
	朋友圈内ID("f"),
	竞价ID("j"),
	;
	
	private final String _flag;
	private final Sequence _seq;
	UUIDUtil(String startFlag){
		_flag = startFlag;
		_seq = new Sequence();
	}
	
	public String id(){
		return _flag+_seq.getSeq();
	}
	
	static{
		Set<String> set = new HashSet<String>();
		for (UUIDUtil uid : UUIDUtil.values()) {
			if(set.contains(uid._flag)){
				Logger.getLogger(UUIDUtil.class).error("UUIDUtil的标示重复",new IllegalStateException("UUIDUtil的标示重复"));
				System.exit(1);
			}
			set.add(uid._flag);
		}
		set.clear();
	}
	
	private static class Sequence{
		private static final int last = 999;
		private static final int radix = 36;
		
		int __seq;
		StringBuilder __buf = new StringBuilder();
		DecimalFormat __df = new DecimalFormat("000");
		
		String getSeq(){
			String str = null;
			synchronized (this) {
				if (__seq >= last) {
					__seq = 0;
				}
				__seq +=1;
				__buf.delete(0, __buf.length());
				__buf.append(System.currentTimeMillis()).append(__df.format(__seq));
				str = __buf.toString();
			}
			return MathUtil.toUnsignedString(new BigInteger(str,10),radix);
		}
	}
}