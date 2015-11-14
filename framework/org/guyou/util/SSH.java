/**
 * 
 */
package org.guyou.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * ssh连接和执行脚本类
 * @author zhushijian
 */
public class SSH implements Serializable {
	
	private static final long serialVersionUID = -6525599953496778162L;

	private String id;
	
	private String dstHost;
	
	private String dstUser;
	
	private String dstPwd;
	
	private int dstPort;
	
	private String os;
	
	private transient Session session = null;
	
	private transient Channel channel = null;
	
	
	
	public SSH(String id){
		this.id = id;
		os = System.getProperty("os.name");
	}
	
	public SSH(String id,String dstHost, String dstUser, String dstPwd,int dstPort){
		this(id);
		setConfig(dstHost,dstUser,dstPwd,dstPort);
	}
	
	public void setConfig(String dstHost, String dstUser, String dstPwd,int dstPort){
		this.dstHost = dstHost;
		this.dstUser = dstUser;
		this.dstPwd = dstPwd;
		this.dstPort = dstPort;
	}
	
	public void connect() throws JSchException{
		JSch jsch = new JSch();
		session = jsch.getSession(dstUser, dstHost, dstPort);
		session.setPassword(dstPwd);
		Properties pop = new Properties();
		pop.setProperty("StrictHostKeyChecking", "no");
		session.setConfig(pop);
		session.connect();
	}
	
	public void disconnect(){
		if(null!=session && session.isConnected()){
			session.disconnect();
			session = null;
		}
	}
	
	public String sshExecute(String command) throws JSchException, IOException{
		Channel channel = null;
		InputStream in = null;
		StringBuilder sb = new StringBuilder();
		try{
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			in = channel.getInputStream();
			channel.connect();
			int nextChar;
			while (true) {
				while ((nextChar = in.read()) != -1) {
					char outchar = (char) nextChar;
					sb.append(outchar);
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: "+ channel.getExitStatus());
					break;
				}
			}
		}finally{
			if(in!=null){
				in.close();
			}
		}
		return sb.toString();
	}
	
	public String getId(){
		return id;
	}
	
	/**
	 * @param args
	 * @throws JSchException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws JSchException, IOException {
		//String cmd = "SERVER=/usr/local/gameserver_bate\n cd $SERVER\n libpath=\"lib/axis.jar:lib/commons-discovery-0.2.jar:lib/jaxrpc.jar:lib/saaj.jar:lib/wsdl4j.jar:lib/pgworld.jar:lib/lib.jar\"\n nohup java -classpath $CLASSPATH:.:config:$libpath -Xmx32m -Xmx128m -Xss512k -XX:-UseParallelGC -XX:MaxNewSize=32m -XX:MaxPermSize=64m -XX:NewSize=1m -XX:ThreadStackSize=1024 -XX:ParallelGCThreads=8 -XX:+UseParallelOldGC -XX:+UseFastAccessorMethods -XX:LargePageSizeInBytes=8m -server cn.com.xxz.game.dbserver.DbServerManager > $SERVER/log/DbServerManager.log 2>&1 & \n echo $!\n";
		System.out.print(System.getProperty("os.name"));
		//		// TODO Auto-generated method stub
		SSH ssh = new SSH("id","119.57.21.148", "httpd", "SjlhrOBP", 22);
		ssh.connect();
			String aaa = ssh.sshExecute("ls /usr/local -la");
			ssh.disconnect();
			ssh.disconnect();
		System.out.println(aaa);
	}

	
	/**
	 * @return the dstHost
	 */
	public String getDstHost() {
		return dstHost;
	}

	/**
	 * @param dstHost the dstHost to set
	 */
	public void setDstHost(String dstHost) {
		this.dstHost = dstHost;
	}

	/**
	 * @return the dstUser
	 */
	public String getDstUser() {
		return dstUser;
	}

	/**
	 * @param dstUser the dstUser to set
	 */
	public void setDstUser(String dstUser) {
		this.dstUser = dstUser;
	}

	/**
	 * @return the dstPwd
	 */
	public String getDstPwd() {
		return dstPwd;
	}

	/**
	 * @param dstPwd the dstPwd to set
	 */
	public void setDstPwd(String dstPwd) {
		this.dstPwd = dstPwd;
	}

	/**
	 * @return the dstPort
	 */
	public int getDstPort() {
		return dstPort;
	}

	/**
	 * @param dstPort the dstPort to set
	 */
	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

	/**
	 * @return the os
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @param os the os to set
	 */
	public void setOs(String os) {
		this.os = os;
	}
	
}
