/**
 * create by 朱施健
 */
package org.guyou.web.server;

import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.guyou.event.Event;
import org.guyou.event.IEventHandler;
import org.guyou.util.ClassUtil;
import org.guyou.util.LogUtil;
import org.guyou.util.SerializeUtil;
import org.guyou.util.ServletUtil;
import org.guyou.web.server.Response.ResponseType;
import org.guyou.web.servlet.AppHttpServletRequest;

/**
 * @author 朱施健
 *
 */
public abstract class AbstractHttpHandler implements IEventHandler<Event> {
	
	private static final Logger log = Logger.getLogger(AbstractHttpHandler.class);
	
	@Override
	public void doExecute(Event e) throws Exception{
		HttpServletRequest req = (HttpServletRequest)((AsyncContext)e.getSource()).getRequest();
		HttpServletResponse resp = (HttpServletResponse)((AsyncContext)e.getSource()).getResponse();
		req.setAttribute(RequestKeyEnum.REQUEST_EXCUTE_TIME, System.nanoTime());
		boolean isAppClient = (req instanceof AppHttpServletRequest);
		boolean isPrintDebugInfo = Level.INFO==log.getEffectiveLevel() && !ServletFileUpload.isMultipartContent(req);
		PrintWriter out = null;
		StringBuilder result_str = null;
		String stationary_str;
		try {
			if(isPrintDebugInfo) {
				result_str = new StringBuilder("\n######################################################################\n");
				result_str.append("请求地址:"+ServletUtil.getUrl(req)+"\n");
			    result_str.append("头信息:{\n");
				for(Enumeration<String> it=req.getHeaderNames();it.hasMoreElements();){
				    String param = it.nextElement();
				    String value = req.getHeader(param);
				    result_str.append("\t"+param+" = "+value+"\n");
				}
				result_str.append("}\n");
				result_str.append("传入参数:{\n");
				for(Enumeration<String> it=req.getParameterNames();it.hasMoreElements();){
				    String param = it.nextElement();
				    String value = req.getParameter(param);
				    result_str.append("\t"+param+" = "+value+"\n");
				}
				result_str.append("}\n");
			}
			out = resp.getWriter(); 
			Response response = excuteAction(req,resp,out);
			if(response!=null){
				if(response.type==ResponseType.forward){
					req.getRequestDispatcher(response.returnObj.toString()).forward(req, resp);
				}else if(response.type==ResponseType.stationary){
					if(ClassUtil.isBaseDataCalss(response.returnObj.getClass())){
						stationary_str = response.returnObj.toString();
						out.write(stationary_str);
					}else{
						stationary_str = SerializeUtil.objectToJsonString(response.returnObj);
						out.write(stationary_str);
					}
					if(isPrintDebugInfo) {
						result_str.append("响应内容:").append(stationary_str).append("\n");
					}
				}else{
					resp.sendRedirect(response.returnObj.toString());
				}
			}
		} catch (Throwable ex){
			if(out!=null && isPrintDebugInfo){
				stationary_str = "{\"state\":-30000,\"errorMsg\":\""+LogUtil.stackTrace(ex.getStackTrace())+"\"}";
				out.write(stationary_str);
				result_str.append("响应内容:").append(stationary_str).append("\n");
			}
			throw ex;
		} finally {
			HibernateSessionFactory.closeSession();
			if(isAppClient){
				resp.addHeader(HeaderKeyEnum.USER_SESSIONID, req.getSession().getId());
			}
			((AsyncContext)e.getSource()).complete();
			if(isPrintDebugInfo) log.info(result_str.append("######################################################################").toString());
		}
	}
	
	public abstract Response excuteAction(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception;
}
