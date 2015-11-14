package org.guyou.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @author zhangmoyuan
 * @version 1.0
 */
public class MathUtil {

	private final static char[] digits = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		'X', 'Y', 'Z' };
	
	private static final ThreadLocal<Random> localRandom =  new ThreadLocal<Random>() {
	    @Override
		protected Random initialValue() {
	        return new Random();
	    }
    };
	
	/**
	 * 传入double类型获得int类型的修正值(四舍五入)
	 * 
	 * @param basicValue
	 * @return (int)value
	 * @throws Exception 
	 */
	public static double getCorrectionValue(double basicValue,int digit) {
		if(digit<0) return basicValue;
		StringBuilder sb = new StringBuilder("#");
		if(digit>0){
			sb.append("0.");
			for ( int i = 0 ; i < digit ; i++ ) {
				sb.append("0");
			}
		}
		DecimalFormat format = new DecimalFormat(sb.toString());
		return Double.parseDouble(format.format(basicValue));
	}


	private static int getCursor(char cc) {
		for (int i = 0; i < digits.length; i++) {
			if (digits[i] == cc) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 整数转化为指定进制字符串
	 * @param i
	 * @param radix
	 * @return
	 */
	public static String toUnsignedString(long i, int radix) {
		if (radix > digits.length || radix < 2) {
			throw new IllegalStateException("进制数必须在2-"+digits.length+"之间");
		}
		String tmp = "";
		if (i < radix) {
			tmp = digits[(int) i] + tmp;
			return tmp;
		}
		do {
			int lf = (int) (i % radix);
			tmp = digits[lf] + tmp;
			i = i / radix;
		} while (i != 0);
		return tmp;
	}
	
	public static String toUnsignedString(BigInteger i, int r) {
		if (r > digits.length || r < 2) {
			throw new IllegalStateException("进制数必须在2-"+digits.length+"之间");
		}
		String tmp = "";
		BigInteger radix = new BigInteger(Integer.toString(r), 10);
		if (i.compareTo(radix) < 0) {
			tmp = digits[i.intValue()] + tmp;
			return tmp;
		}
		do {
			BigInteger[] zy = i.divideAndRemainder(radix);
			BigInteger lf = zy[1];
			tmp = digits[lf.intValue()] + tmp;
			i = zy[0];
		} while (i.intValue() != 0);
		return tmp;
	}

	/**
	 * 指定进制字符串转化为整数
	 * @param num
	 * @param radix
	 * @return
	 */
	public static long currentRadixToDecimal(String num, int radix) {
		long result = 0;
		if (radix > 62 || radix < 2) {
			return -1;
		}
		for (int i = 0; i < num.length(); i++) {
			char c = num.charAt(i);
			int cursor = getCursor(c);
			long digit = cursor;
			for (int j = i + 1; j < num.length(); j++) {
				digit = digit * radix;
			}
			result = result + digit;
		}
		return result;
	}
	
	/**
	 * 判断是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str){
		if(str.equals("0")){
			return true;
		}
		if(str==null || str.equals("") || (str.startsWith("0") && !str.startsWith("0.")) || str.startsWith(".") ){
			return false;
		}
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(c!='.' && (c<'0' || c>'9')){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 字节数组转int
	 * @param b
	 * @param offset
	 * @return
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	/**
	 * int转字节数组
	 * @param s
	 * @return
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}
	
	/**
	 * long转化为ActionScript中的字节数组
	 * @param i
	 * @return
	 */
	public static byte[] longToByteArrayForAS(long i) {
		int h = (int) (i/100000000);
		int l = (int) (i%100000000);
		
		byte[] result = new byte[8];
		result[0] = (byte) ((h >> 24) & 0xFF);
		result[1] = (byte) ((h >> 16) & 0xFF);
		result[2] = (byte) ((h >> 8) & 0xFF);
		result[3] = (byte) (h & 0xFF);
		
		result[4] = (byte) ((l >> 24) & 0xFF);
		result[5] = (byte) ((l >> 16) & 0xFF);
		result[6] = (byte) ((l >> 8) & 0xFF);
		result[7] = (byte) (l & 0xFF);
		
		return result;
	}
	
	/**
	 * 字节数组转化为ActionScript的long
	 * @param b
	 * @param offset
	 * @return
	 */
	public static long byteArrayToLongForAS(byte[] b, int offset) {
		int h = 0;
		int l=0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			h += (b[i + offset] & 0x000000FF) << shift;// 往高位游
		}
		for (int i = 4; i < 8; i++) {
			int shift = (8 - 1 - i) * 8;
			l += (b[i + offset] & 0x000000FF) << shift;// 往高位游
		}
		return ((long)h)*100000000+l;
	}
	
	public static int nextInt(){
		return localRandom.get().nextInt();
	}
	
	public static int nextInt(int n){
		return localRandom.get().nextInt(n);
	}
	
	/**
	 * 两个整数之间的随机值
	 * @param minInt
	 * @param maxInt
	 * @return
	 */
	public static int randomInt(int minInt,int maxInt){
		return minInt==maxInt ? minInt : (Math.min(minInt, maxInt)+localRandom.get().nextInt(Math.abs(maxInt - minInt)+1));
	}
	
	/**
	 * 两个小数之间的随机值
	 * @param minDouble
	 * @param maxDouble
	 * @return
	 * @throws Exception 
	 */
	public static double randomDouble(double minDouble,double maxDouble){
		return minDouble==maxDouble ? minDouble: randomInt((int)getCorrectionValue(minDouble*100000,0),(int)getCorrectionValue(maxDouble*100000,0))/100000.0;
	}
	
	/**
	 * 获取随机范围下标
	 * @param ranges(随即比重)
	 * @return
	 */
	public static int randomIndex(int[] ranges){
		int maxRange = 0;
		
		for(int randomRange : ranges){
			maxRange += randomRange;
		}
		
		int rate = localRandom.get().nextInt(maxRange==0?1:maxRange);

		int currentRange = 0;
		//随机数一定在取值范围内(百分百命中)
		for(int i = 0; i < ranges.length; i ++){
			currentRange += ranges[i];
			if(currentRange > rate){
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 从集合中随机获取一定的个数
	 * @param allCount
	 * @return
	 */
	public static int[] randomList(int allCount,int requireCount){
		if(allCount==0) return new int[0];
		int[] r = null;
		if(requireCount>=allCount){
			r = new int[allCount];
			for (int i = 0; i < allCount; i++) {
				r[i] = i;
			}
		}else{
			List<Integer> temp = new ArrayList<Integer>();
			do {
				int index = localRandom.get().nextInt(allCount);
				if(temp.indexOf(index)==-1){
					temp.add(index);
				}
				
			} while (temp.size()<requireCount);
			r = ArrayUtils.toPrimitive(temp.toArray(new Integer[0]));
		}
		return r;
	}
	
	/**
	 * 获取随机范围下标
	 * @param ranges(随即比重)
	 * @return
	 */
	public static int randomIndex(Integer[] ranges){
		int maxRange = 0;
		for(int randomRange : ranges){
			maxRange += randomRange;
		}
		
		int rate = localRandom.get().nextInt(maxRange==0?1:maxRange);

		int currentRange = 0;
		//随机数一定在取值范围内(百分百命中)
		for(int i = 0; i < ranges.length; i ++){
			currentRange += ranges[i];
			if(currentRange > rate){
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 获取随机范围下标
	 * @param ranges(随即比重)
	 * @return
	 */
	public static int randomIndex(List<Integer> ranges) {
		int maxRange = 0;
		
		for(int randomRange : ranges){
			maxRange += randomRange;
		}
		
		int rate = localRandom.get().nextInt(maxRange==0?1:maxRange);

		int currentRange = 0;
		//随机数一定在取值范围内(百分百命中)
		for(int i = 0; i < ranges.size(); i ++){
			currentRange += ranges.get(i);
			if(currentRange > rate){
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 是否命中
	 * @param hitProbability 命中概率
	 * @param probability 总概率(百分位,千分位... ...)
	 * @return
	 */
	public static boolean isHit(int hitProbability, int probability) {
		if(hitProbability==0) return false;
		if(hitProbability>=probability) return true;
		int result = localRandom.get().nextInt(probability) + 1;
		if(result <= hitProbability) return true;
		return false;
	}
	
	public static double randomDouble(){
		return localRandom.get().nextDouble();
	}
	
	public static void main(String[] aaa){
		randomList(9,10);
		
	}
	
	public static Number min(Number... numbers){
		Number min = Double.MAX_VALUE;
		for ( Number number : numbers ) {
			if(number.doubleValue()<min.doubleValue()){
				min = number;
			}
		}
		return min;
	}
	
	public static Number max(Number... numbers){
		Number max = Double.MIN_VALUE;
		for ( Number number : numbers ) {
			if(number.doubleValue()>max.doubleValue()){
				max = number;
			}
		}
		return max;
	}
	
	public static String parseNumber(String formula){
		MathExpress me = new MathExpress(formula);
		return me.caculate();
	}
	
	
	private static class MathExpress{
		/**
	     * +
	     */
	    private final static String OP1 = "+";
	       
	    /**
	     * -
	     */
	    private final static String OP2 = "-";
	       
	    /**
	     * *
	     */
	    private final static String OP3 = "*";
	       
	    /**
	     * /
	     */
	    private final static String OP4 = "/";
	    
	    /**
	     * ^
	     */
//	    private final static String OP5 = "^";
	    
	    /**
	     * %
	     */
//	    private final static String OP6 = "%";
	    /**
	     * (
	     */
	    private final static String OPSTART = "(";
	       
	    /**
	     * )
	     */
	    private final static String OPEND = ")";
	    
	    /**
	     * !用来替代负数前面的'-'
	     */
//	    private final static String NEGATIVESING = "!";
	    
	    /**
	     * !用来替代负数前面的'+'
	     */
//	    private final static String PLUSSING = "@";
	    
	    /**
	     * '#'用来代表运算级别最低的特殊字符
	     */
//	    private final static String LOWESTSING = "#";
	   
	    //最原始的四则运算式
	    private String expBase;
	    
	    //经过初始化处理后的四则运算式
	    private String expInited;
	       
	    //精度
	    private int precision=10;
	       
	    //取舍模式
	    private RoundingMode roundingMode=RoundingMode.HALF_UP;
	    
	    //精度上下文
	    private MathContext mc;
	       
	    //四则运算解析
	    private List<String> expList = new ArrayList<String>();
	   
	    //存放逆波兰表达式
	    private List<String> rpnList = new ArrayList<String>();
	    
	    public MathExpress(){}
	    
	    public MathExpress(String expBase) {
	    	init(expBase,this.precision,this.roundingMode);
		}

		public MathExpress(String expBase,int precision,RoundingMode roundingMode){
	    	init(expBase,precision,roundingMode);
	    }
	    
	    public void init(String expBase,int precision,RoundingMode roundingMode){
	    	this.expBase = expBase;
	    	this.precision = precision;
	    	this.roundingMode = roundingMode;
	    	this.mc = new MathContext(precision,roundingMode);
	    	this.expInited = initExpress(expBase);
	    	
	    	StringTokenizer st = new StringTokenizer(this.expInited,"+-*/^%()",true);
	    	while(st.hasMoreElements()){
	    		this.expList.add(st.nextElement().toString().trim());
	    	}
	    	
	    	this.rpnList = initRPN(this.expList);
	    }
	    
	    /**
		 * @return the expBase
		 */
		public String getExpBase() {
			return expBase;
		}

		/**
		 * @param expBase the expBase to set
		 */
		public void setExpBase(String expBase) {
			this.expBase = expBase;
		}

		/**
		 * @return the expInited
		 */
		public String getExpInited() {
			return expInited;
		}

		/**
		 * @param expInited the expInited to set
		 */
		public void setExpInited(String expInited) {
			this.expInited = expInited;
		}

		/**
		 * @return the precision
		 */
		public int getPrecision() {
			return precision;
		}

		/**
		 * @param precision the precision to set
		 */
		public void setPrecision(int precision) {
			this.precision = precision;
		}

		/**
		 * @return the roundingMode
		 */
		public RoundingMode getRoundingMode() {
			return roundingMode;
		}

		/**
		 * @param roundingMode the roundingMode to set
		 */
		public void setRoundingMode(RoundingMode roundingMode) {
			this.roundingMode = roundingMode;
		}

		/**
		 * @return the expList
		 */
		public List<String> getExpList() {
			return expList;
		}

		/**
		 * @param expList the expList to set
		 */
		public void setExpList(List<String> expList) {
			this.expList = expList;
		}

		/**
		 * @return the rpnList
		 */
		public List<String> getRpnList() {
			return rpnList;
		}

		/**
		 * @param rpnList the rpnList to set
		 */
		public void setRpnList(List<String> rpnList) {
			this.rpnList = rpnList;
		}

		/**
		 * @return the mc
		 */
		public MathContext getMc() {
			return mc;
		}

		/**
		 * @param mc the mc to set
		 */
		public void setMc(MathContext mc) {
			this.mc = mc;
		}

		/**
	     * 去除空白字符和在负号'-'前加'0',便于后面的StringTokenizer
	     * @param exp
	     * @return
	     */
	    private static String initExpress(String exp){
	    	String reStr = null;
	    	reStr = exp.replaceAll("\\s", "");
	    	if(reStr.startsWith("-")){
	    		reStr = "0"+reStr;
	    	}
	    	reStr = reStr.replaceAll("\\(\\-", "(0-");
	    	return reStr;
	    }
	    
	    /**
	     * 是否是整数或是浮点数,但默认-05.15这种也认为是正确的格式
	     * @param str
	     * @return
	     */
	    private boolean isNumber(String str){
	    	Pattern p = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
	    	Matcher m = p.matcher(str);
	    	boolean isNumber = m.matches();
	    	return isNumber;
	    }
	    
	    /**
	     * 设置优先级顺序()设置与否无所谓
	     * @param sign
	     * @return
	     */
	    private int precedence(String str){
	    	char sign = str.charAt(0);
	    	switch(sign){
	    		case '+':
	    		case '-':
	    			return 1;
	    		case '*':
	    		case '/':
	    			return 2;
	    		case '^':
	    		case '%':
	    			return 3;
	    		case '(':
	    		case ')':
//	    		case '#':
	    		default:
	    			return 0;
	    		
	    	}
	    }
	    
	    /**
	     * 转变为逆波兰表达式
	     * @param strList
	     * @return
	     */
	    public List<String> initRPN(List<String> strList){
	    	List<String> returnList = new ArrayList<String>();
	    	//用来存放操作符的栈
	    	Stack stack = new Stack();
//	    	stack.push(LOWESTSING);
	    	int length = strList.size();
	    	for(int i=0;i<length;i++ ){
	    		String str = strList.get(i);
	    		if(isNumber(str)){
	    			returnList.add(str);
	    		}else{
	    			if(str.equals(OPSTART)){
	    				//'('直接入栈
	    				stack.push(str);
	    			}else if(str.equals(OPEND)){
	    				//')'
	    				//进行出栈操作，直到栈为空或者遇到第一个左括号   
	                    while (!stack.isEmpty()) {   
	                        //将栈顶字符串做出栈操作   
	                        String tempC = stack.pop();   
	                        if (!tempC.equals(OPSTART)) {   
	                            //如果不是左括号，则将字符串直接放到逆波兰链表的最后   
	                        	returnList.add(tempC);   
	                        }else{   
	                            //如果是左括号，退出循环操作   
	                            break;   
	                        }   
	                    }   
	    			}else{
	                    if (stack.isEmpty()) {
	                    	//如果栈内为空   
	                        //将当前字符串直接压栈   
	                        stack.push(str);   
	                    }else{
	                    	//栈不空,比较运算符优先级顺序
	                    	if(precedence(stack.top())>=precedence(str)){
	                    		//如果栈顶元素优先级大于当前元素优先级则
	                    		while(!stack.isEmpty() && precedence(stack.top())>=precedence(str)){
	                    			returnList.add(stack.pop());
	                    		}
	                    	}
	                    	stack.push(str);
	                    }
	    			}
	    		}
	    	}
	    	//如果栈不为空，则将栈中所有元素出栈放到逆波兰链表的最后   
	        while (!stack.isEmpty()) {
	        	returnList.add(stack.pop());
	        }
	    	return returnList;
	    }
	    
	    /**
	     * 计算逆波兰表达式
	     * @param rpnList
	     * @return
	     */
	    public String caculate(List<String> rpnList){
	    	Stack numberStack = new Stack();   
	        
	        int length=rpnList.size();   
	        for(int i=0;i<length;i++){   
	            String temp=rpnList.get(i);   
	            if(isNumber(temp)){   
	                numberStack.push(temp);   
	            }else{   
	                BigDecimal tempNumber1 = new BigDecimal(numberStack.pop(),this.mc);
	                   
	                BigDecimal tempNumber2 = new BigDecimal(numberStack.pop(),this.mc);
	                   
	                BigDecimal tempNumber = new BigDecimal("0",this.mc);
	                   
	                if(temp.equals(OP1)){   
	                    tempNumber=tempNumber2.add(tempNumber1);   
	                }else if(temp.equals(OP2)){   
	                    tempNumber=tempNumber2.subtract(tempNumber1);   
	                }else if(temp.equals(OP3)){   
	                    tempNumber=tempNumber2.multiply(tempNumber1);   
	                }else if(temp.equals(OP4)){   
	                    tempNumber=tempNumber2.divide(tempNumber1,   
	                            precision,   
	                            roundingMode);   
	                }  
	                numberStack.push(tempNumber.toString());   
	                   
	            }   
	        }   
	           
	        return numberStack.pop();
	    	
	    }
	    /**
	     * 按照类的缺省参数进行计算
	     * @return
	     */
	    public String caculate(){
	    	return caculate(this.rpnList);
	    }
	    
	    /**
	     * 栈
	     */
	    private class Stack {
	        
	        LinkedList<String> stackList = new LinkedList<String>();
	   
	        public Stack() {
	        
	        }
	   
	        /**
	         * 入栈
	         * @param expression
	         */
	        public void push(String expression) {
	            stackList.addLast(expression);
	        }
	   
	        /**
	         * 出栈
	         * @return
	         */
	        public String pop() {
	            return stackList.removeLast();
	        }
	   
	        /**
	         * 栈顶元素
	         * @return
	         */
	        public String top() {
	            return stackList.getLast();
	        }
	   
	        /**
	         * 栈是否为空
	         * @return
	         */
	        public boolean isEmpty() {
	            return stackList.isEmpty();
	        }
	    }
	}
} 
