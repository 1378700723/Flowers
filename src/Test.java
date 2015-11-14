

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import org.guyou.util.ServletUtil.ClientType;
import org.guyou.web.server.HeaderKeyEnum;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.WebStartListening;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.flower.tables.Administrator;
import com.flower.tables.FriendApply;
import com.flower.tables.FriendApply.FriendApplyId;

@WebStartListening
public class Test {

	public static void init(){
		Session session = HibernateSessionFactory.getSession();
		FriendApply apply = (FriendApply) session.createQuery("from "+FriendApply.class.getName()+" where id=?").setParameter(0, new FriendApplyId("1","2")).uniqueResult();
		System.out.println(apply);
	}
	
	public static void main(String[] args) {
		System.out.println(ClientType.MOBILE_APPLICATION.equals(ClientType.MOBILE_APPLICATION));
		System.out.println(ClientType.MOBILE_APPLICATION.equals(null));
		
//		Configuration cfg = new Configuration().configure(); 
//	    SchemaExport export = new SchemaExport(cfg); 
//	    export.create(true, true); 
		try {
			URL localURL = new URL("http://127.0.0.1:8080/flowers/customer/send_to_friendcircle.do?delete=0");
			HttpURLConnection httpURLConnection = (HttpURLConnection)localURL.openConnection();
	        httpURLConnection.setRequestProperty("user-agent", "mobile_app");
			String sessionid = httpURLConnection.getHeaderField(HeaderKeyEnum.USER_SESSIONID);
        	BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String tempLine;
            while ((tempLine = reader.readLine()) != null) {
            	System.out.println(tempLine);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
