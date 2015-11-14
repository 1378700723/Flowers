package org.guyou.util.excel;

import java.lang.reflect.Field;

/**
 * 所有需要生成的模板bean要实现的接口
 * @author 王烁
 * @date 2012-11-14
 * @version 1.0
 */
public abstract class IExcelBean{
	public String getDetail() {
		return "";
	}
	
	public boolean check() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		IExcelBean other = (IExcelBean) obj;
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			try {
				Object thisO = field.get(this);
				Object otherO = field.get(other);
				if(thisO==null && otherO==null){
					continue;
				}else if(thisO!=null && otherO!=null){
					Class<?> cls = field.getType();
					if(cls.isArray()){
						if(thisO instanceof String[]){
							String[] thisStr = (String[])thisO;
							String[] otherStr = (String[])otherO;
							if(thisStr.length!=otherStr.length){
								return false;
							}else{
								for(int i=0;i<thisStr.length;i++){
									if(thisStr[i]==null && otherStr[i]==null){
										continue;
									}else if(thisStr[i]!=null && otherStr[i]!=null){
										if(!thisStr[i].equals(otherStr[i])){
											return false;
										}
									}else{
										return false;
									}
								}
							}
						}else if(thisO instanceof byte[]){
							byte[] thisByte = (byte[])thisO;
							byte[] otherByte = (byte[])otherO;
							if(thisByte.length!=otherByte.length){
								return false;
							}else{
								for(int i=0;i<thisByte.length;i++){
									if(thisByte[i]!=otherByte[i]){
										return false;
									}
								}
							}
						}else if(thisO instanceof short[]){
							short[] thisShort = (short[])thisO;
							short[] otherShort = (short[])otherO;
							if(thisShort.length!=otherShort.length){
								return false;
							}else{
								for(int i=0;i<thisShort.length;i++){
									if(thisShort[i]!=otherShort[i]){
										return false;
									}
								}
							}
						}else if(thisO instanceof int[]){
							int[] thisInt = (int[])thisO;
							int[] otherInt = (int[])otherO;
							if(thisInt.length!=otherInt.length){
								return false;
							}else{
								for(int i=0;i<thisInt.length;i++){
									if(thisInt[i]!=otherInt[i]){
										return false;
									}
								}
							}
						}else if(thisO instanceof long[]){
							long[] thisLong = (long[])thisO;
							long[] otherLong = (long[])otherO;
							if(thisLong.length!=otherLong.length){
								return false;
							}else{
								for(int i=0;i<thisLong.length;i++){
									if(thisLong[i]!=otherLong[i]){
										return false;
									}
								}
							}
						}else if(thisO instanceof float[]){
							float[] thisFloat = (float[])thisO;
							float[] otherFloat = (float[])otherO;
							if(thisFloat.length!=otherFloat.length){
								return false;
							}else{
								for(int i=0;i<thisFloat.length;i++){
									if(thisFloat[i]!=otherFloat[i]){
										return false;
									}
								}
							}
						}else if(thisO instanceof double[]){
							double[] thisDouble = (double[])thisO;
							double[] otherDouble = (double[])otherO;
							if(thisDouble.length!=otherDouble.length){
								return false;
							}else{
								for(int i=0;i<thisDouble.length;i++){
									if(thisDouble[i]!=otherDouble[i]){
										return false;
									}
								}
							}
						}else if(thisO instanceof boolean[]){
							boolean[] thisBoolean = (boolean[])thisO;
							boolean[] otherBoolean = (boolean[])otherO;
							if(thisBoolean.length!=otherBoolean.length){
								return false;
							}else{
								for(int i=0;i<thisBoolean.length;i++){
									if(thisBoolean[i]!=otherBoolean[i]){
										return false;
									}
								}
							}
						}
					}else{
						if(!thisO.equals(otherO)){
							return false;
						}
					}
				}else{
					return false;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public void update(IExcelBean newData) throws IllegalArgumentException, IllegalAccessException{
		if(this.getClass()==newData.getClass()){
			Field[] fields = this.getClass().getFields();
			for (Field field : fields) {
				field.set(this, field.get(newData));
			}
		}
	}
	
	public abstract String getId();
	
	public abstract String getPkName();
	
	/**
	 * 写入数据
	 */
	public abstract void write(java.io.DataOutputStream out) throws java.io.IOException;
	
	/**
	 * 读取数据
	 */
	public abstract void read(java.io.DataInputStream inn) throws java.io.IOException;
}
