/**
 * create by 朱施健
 */
package com.easemob.server.jersey;

import java.util.List;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.JerseyWebTarget;

import com.easemob.server.comm.Constants;
import com.easemob.server.comm.HTTPMethod;
import com.easemob.server.comm.Roles;
import com.easemob.server.jersey.utils.JerseyUtils;
import com.easemob.server.jersey.vo.ClientSecretCredential;
import com.easemob.server.jersey.vo.Credential;
import com.easemob.server.jersey.vo.EndPoints;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flower.customer.beans.CustomerUser;

/**
 * @author 朱施健
 *
 */
public class EasemobManager {
	private static final Logger log = Logger.getLogger(EasemobManager.class);
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	// 通过app的client_id和client_secret来获取app管理员token
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);
    
    static{
    	// check appKey format
   		if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {
   			log.error("环信的Appkey格式错误: " + Constants.APPKEY,new IllegalStateException("环信的Appkey格式错误: " + Constants.APPKEY));
   			System.exit(1);
   		}
    }
    
    /**
     * 注册单个IM用户
     */
    public static ObjectNode registeCustomer(String username,String password){
        ObjectNode datanode = factory.objectNode();
        datanode.put("username",username);
        datanode.put("password", password);
		JerseyWebTarget webTarget = EndPoints.USERS_TARGET
				.resolveTemplate("org_name",Constants.APPKEY.split("#")[0])
				.resolveTemplate("app_name",Constants.APPKEY.split("#")[1]);
		return JerseyUtils.sendRequest(webTarget, datanode, credential, HTTPMethod.METHOD_POST, null);
    }
    
    /**
     * 添加好有
     * @param me
     * @param friend
     * @return
     */
    public static ObjectNode addFriend(String me,String friend){
		JerseyWebTarget webTarget = EndPoints.USERS_ADDFRIENDS_TARGET
				.resolveTemplate("org_name", Constants.APPKEY.split("#")[0])
				.resolveTemplate("app_name", Constants.APPKEY.split("#")[1])
				.resolveTemplate("ownerUserName", me)
				.resolveTemplate("friendUserName",friend);
		//{"action":"post","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","path":"/users/0d5557da-1326-11e5-a1e9-1f174c40c643/contacts","uri":"http://a1.easemob.com/huahuahui/huahuahui/users/0d5557da-1326-11e5-a1e9-1f174c40c643/contacts","entities":[{"uuid":"5c505f7a-132f-11e5-88cc-ef0d57d5bc0a","type":"user","created":1434352983783,"modified":1434352983783,"username":"huahuahuikefu001","activated":true}],"timestamp":1435197219765,"duration":223,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
		//{"action":"post","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","path":"/users/5c505f7a-132f-11e5-88cc-ef0d57d5bc0a/contacts","uri":"http://a1.easemob.com/huahuahui/huahuahui/users/5c505f7a-132f-11e5-88cc-ef0d57d5bc0a/contacts","entities":[{"uuid":"0d5557da-1326-11e5-a1e9-1f174c40c643","type":"user","created":1434348985805,"modified":1434348985805,"username":"kenshinnuser100","activated":true}],"timestamp":1435197291062,"duration":63,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
		return JerseyUtils.sendRequest(webTarget, factory.objectNode(), credential,HTTPMethod.METHOD_POST, null);
	}
    
    /**
     * 删除好友
     * @param me
     * @param friend
     * @return
     */
    public static ObjectNode deleteFriend(String me,String friend){
    	JerseyWebTarget webTarget = EndPoints.USERS_ADDFRIENDS_TARGET
				.resolveTemplate("org_name", Constants.APPKEY.split("#")[0])
				.resolveTemplate("app_name", Constants.APPKEY.split("#")[1])
				.resolveTemplate("ownerUserName", me)
				.resolveTemplate("friendUserName",friend);
		//解除好友关系: {"action":"delete","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","path":"/users/00b88cfa-1334-11e5-8f2c-01fe4157e011/contacts","uri":"http://a1.easemob.com/huahuahui/huahuahui/users/00b88cfa-1334-11e5-8f2c-01fe4157e011/contacts","entities":[{"uuid":"3b91295a-133d-11e5-8c31-efa17220a1ce","type":"user","created":1434358941797,"modified":1434358941797,"username":"kenshinnuser102","activated":true}],"timestamp":1435200296738,"duration":40,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
		return JerseyUtils.sendRequest(webTarget, factory.objectNode(), credential, HTTPMethod.METHOD_DELETE, null);
    }
    
    /**
     * 创建群组
     * @param user
     * @param name
     * @param desc
     * @param member_username_list
     * @return
     */
    public static ObjectNode createGroup(CustomerUser user,String name,String desc,List<String> member_username_list){
    	ObjectNode dataObjectNode = factory.objectNode();
		dataObjectNode.put("groupname", name);
		dataObjectNode.put("desc", desc);
		dataObjectNode.put("approval", false);
		dataObjectNode.put("public", true);
		dataObjectNode.put("maxusers", 1024);
		dataObjectNode.put("owner", user.customer.phone);
		if(member_username_list.size()>0){
			ArrayNode arrayNode = factory.arrayNode();
			for (String phone : member_username_list) {
				arrayNode.add(phone);
			}
			dataObjectNode.put("members", arrayNode);
		}
		JerseyWebTarget webTarget = EndPoints.CHATGROUPS_TARGET
				.resolveTemplate("org_name", Constants.APPKEY.split("#")[0])
				.resolveTemplate("app_name", Constants.APPKEY.split("#")[1]);
		//{"action":"post","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","uri":"https://a1.easemob.com/huahuahui/huahuahui","entities":[],"data":{"groupid":"143445012842806"},"timestamp":1434450128037,"duration":38,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
		return JerseyUtils.sendRequest(webTarget, dataObjectNode, credential, HTTPMethod.METHOD_POST, null);
    }
    
    /**
     * 加入群组
     * @param groupid
     * @param target
     * @return
     */
    public static ObjectNode joinGroup(String groupid,String target){
		JerseyWebTarget webTarget = EndPoints.CHATGROUPS_TARGET.resolveTemplate("org_name", Constants.APPKEY.split("#")[0])
				.resolveTemplate("app_name", Constants.APPKEY.split("#")[1]).path(groupid).path("users")
				.path(target);
		return JerseyUtils.sendRequest(webTarget, null, credential, HTTPMethod.METHOD_POST, null);
    }
    
    /**
     * 退出群
     * @param groupid
     * @param quiter
     * @return
     */
    public static ObjectNode quitGroup(String groupid,String quiter){
    	JerseyWebTarget webTarget = EndPoints.CHATGROUPS_TARGET.resolveTemplate("org_name", Constants.APPKEY.split("#")[0])
				.resolveTemplate("app_name", Constants.APPKEY.split("#")[1]).path(groupid).path("users")
				.path(quiter);
		return JerseyUtils.sendRequest(webTarget, null, credential, HTTPMethod.METHOD_DELETE, null);
    }
    
    public static void main(String[] args) {
//		ObjectNode r =  joinGroup("143445012842806", "kenshinnuser103");
//		System.out.println(r.toString());
    	ObjectNode r2 = quitGroup("143445012842806","kenshinnuser103");
    	System.out.println(r2);
    	ObjectNode r3 = quitGroup("143445012842806","kenshinnuser103");
    	System.out.println(r3);
    }
}
