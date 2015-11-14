package org.guyou.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.Sigar;

public class SystemUtil {
	
	private static final Logger log = Logger.getLogger(SystemUtil.class);
	
	protected static String RUN_PATTERN = "debug";
	
	public static final boolean ISLINUX = System.getProperty("os.name").toLowerCase().contains("linux");
	
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	public static final CharsetDecoder DEFAULT_CHARSET_DECODER = DEFAULT_CHARSET.newDecoder();
	
	public static final CharsetEncoder DEFAULT_CHARSET_ENCODER = DEFAULT_CHARSET.newEncoder();
	
	/**
	 * 是否是debug模式
	 * @return
	 */
	public static boolean isDebug(){
		return "debug".equals(RUN_PATTERN);
	}
	/**
	 * 获得IP
	 * @return
	 */
	public static String getIpAddress(){
		String address = null;
		try {
			address = InetAddress.getLocalHost().getHostAddress();
			// 没有出现异常而正常当取到的IP时，如果取到的不是网卡循回地址时就返回
			// 否则再通过Sigar工具包中的方法来获取
			if (!NetFlags.LOOPBACK_ADDRESS.equals(address)) {
				return address;
			}
		} catch (UnknownHostException e) {
			log.warn("InetAddress.getLocalHost().getHostAddress()异常!",e);
		}
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			address = sigar.getNetInterfaceConfig().getAddress();
		} catch (Throwable e) {
			address = NetFlags.LOOPBACK_ADDRESS;
			log.warn("Sigar异常!",e);
		} finally {
			if(sigar!=null)sigar.close();
		}
		return address;
	}
	
	/**
	 * 获取IP列表
	 * @return
	 */
	public static List<String> getHostAddressList() {
		List<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface;
			Enumeration<InetAddress> inetAddresses;
			InetAddress inetAddress;
			String ip;
			while ( networkInterfaces.hasMoreElements() ) {
				networkInterface = networkInterfaces.nextElement();
				inetAddresses = networkInterface.getInetAddresses();
				while ( inetAddresses.hasMoreElements() ) {
					inetAddress = inetAddresses.nextElement();
					if ( inetAddress != null && inetAddress instanceof Inet4Address ) { // IPV4
						ip = inetAddress.getHostAddress();
						ipList.add(ip);
					}
				}
			}
		} catch ( SocketException e ) {
			log.error("获取网卡实例异常",e);
			ipList.clear();
		}
		return ipList;
	}
	
	/**
	 * 获取hostname列表
	 * @return
	 */
	public static List<String> getHostNameList() {
		List<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface;
			Enumeration<InetAddress> inetAddresses;
			InetAddress inetAddress;
			String hostname;
			while ( networkInterfaces.hasMoreElements() ) {
				networkInterface = networkInterfaces.nextElement();
				inetAddresses = networkInterface.getInetAddresses();
				while ( inetAddresses.hasMoreElements() ) {
					inetAddress = inetAddresses.nextElement();
					if ( inetAddress != null && inetAddress instanceof Inet4Address ) { // IPV4
						hostname = inetAddress.getHostName();
						if(!ipList.contains(hostname)){
							ipList.add(hostname);
						}
					}
				}
			}
		} catch ( SocketException e ) {
			log.error("获取网卡实例异常",e);
			ipList.clear();
		}
		return ipList;
	}
	
	/**
	 * 获取进程编号
	 * @return
	 */
	public static int getpid() {
		int pid = -1;
		String runtimeMXBeanName = ManagementFactory.getRuntimeMXBean().getName();
		if (runtimeMXBeanName.indexOf("@") != -1) {
			pid = Integer.valueOf(runtimeMXBeanName.substring(0,runtimeMXBeanName.indexOf("@")));
		}
		return pid;
	}
	
	/**
	 * 端口是否在监听
	 * @param port
	 * @return
	 */
	public static boolean isPortListening(int port){
		String os = System.getProperties().getProperty("os.name");
		String result = "";
		if(ISLINUX){
			result = execCmd("netstat -ntlp| grep "+port+" | grep LISTEN");
		}else if(os.indexOf("Windows")!=-1){
			result = execCmd("netstat -aon | findstr \"0.0.0.0:"+port+"\" | findstr \"LISTEN\"");
		}
		
		if(!result.equals("")){
			String[] strs = result.split("\n");
			for(String s1 : strs){
				String[] ds = s1.split(" ");
				for (String s2 : ds) {
					if(!s2.equals("") && s2.endsWith(":"+port)){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 执行cmd命令
	 * @param cmd 命令
	 * @return 返回结果
	 */
	public static String execCmd(String cmd){
		try {
			Process process = null;
			if(ISLINUX){
				process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", cmd }, null, null);
			}else{
				process = Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", cmd }, null, null);
			}
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line;
			StringBuilder sb = new StringBuilder("");
			while ((line = input.readLine()) != null) {
				sb.append(line.trim()).append("\n");
			}
			input.close();
			ir.close();
			int state = process.waitFor();
			if(state!=0){
				sb.delete(0,sb.length());
				ir = new InputStreamReader(process.getErrorStream(),Locale.getDefault().getLanguage().equalsIgnoreCase("en")?SystemUtil.DEFAULT_CHARSET.name() : "GB2312");
				input = new LineNumberReader(ir);
				while ((line = input.readLine()) != null) {
					sb.append(line.trim()).append("\n");
				}
				input.close();
				ir.close();
			}
			if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
			return sb.toString().trim();
		}catch (Exception e) {
			log.error("执行cmd命令出错!!",e);
		}
		return "";
	}
	
	public static int getHashCode(Object obj) {
		Class<?> clazz = obj.getClass();
		if ( clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Double.class
				|| clazz == Float.class || clazz == Boolean.class || clazz == String.class ) {
			return obj.hashCode();
		}
		return System.identityHashCode(obj);
	}
	
	public static void printToLog4jFile(){
		PrintStream printStream = new PrintStream(System.out) {
			@Override
			public void println(boolean x) {
            	Logger.getRootLogger().error(Boolean.valueOf(x));
            }
            @Override
			public void println(char x) {
                Logger.getRootLogger().error(Character.valueOf(x));
            }
            @Override
			public void println(char[] x) {
                Logger.getRootLogger().error(x == null ? null : new String(x));
            }
            @Override
			public void println(double x) {
                Logger.getRootLogger().error(Double.valueOf(x));
            }
            @Override
			public void println(float x) {
                Logger.getRootLogger().error(Float.valueOf(x));
            }
            @Override
			public void println(int x) {
                Logger.getRootLogger().error(Integer.valueOf(x));
            }
            @Override
			public void println(long x) {
                Logger.getRootLogger().error(x);
            }
            @Override
			public void println(Object x) {
                Logger.getRootLogger().error(x);
            }
            @Override
			public void println(String x) {
                Logger.getRootLogger().error(x);
            }
            @Override
			public void print(boolean x) {
                Logger.getRootLogger().error(Boolean.valueOf(x));
            }
            @Override
			public void print(char x) {
                Logger.getRootLogger().error(Character.valueOf(x));
            }
            @Override
			public void print(char[] x) {
                Logger.getRootLogger().error(x == null ? null : new String(x));
            }
            @Override
			public void print(double x) {
                Logger.getRootLogger().error(Double.valueOf(x));
            }
            @Override
			public void print(float x) {
                Logger.getRootLogger().error(Float.valueOf(x));
            }
            @Override
			public void print(int x) {
                Logger.getRootLogger().error(Integer.valueOf(x));
            }
            @Override
			public void print(long x) {
                Logger.getRootLogger().error(x);
            }
            @Override
			public void print(Object x) {
                Logger.getRootLogger().error(x);
            }
            @Override
			public void print(String x) {
                Logger.getRootLogger().error(x);
            }
        };
        System.setErr(printStream);
        System.setOut(printStream);
	}
	
	public static void main(String[] args) {
		System.out.println(isPortListening((short)80));
	}
}
