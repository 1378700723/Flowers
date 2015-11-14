/**
 * create by 朱施健
 */
package com.flower.admin.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.io.JSONExternalizable;
import com.alibaba.fastjson.io.JSONInput;
import com.alibaba.fastjson.io.JSONOutput;
import com.flower.Application;

/**
 * @author 朱施健
 *
 */
public class AdminMenu implements JSONExternalizable{
	public String menuID;
	public String menuName;
	public Map<String,AdminModule> modules = new LinkedHashMap<String,AdminModule>();

	public static class AdminModule{
		public String moduleID;
		public String moduleName;
		public String modulePath;
	}
	
	@Override
	public void writeExternal(JSONOutput out) {
		out.putStringVaule("menuID",menuID);
		out.putObject("modules", modules.keySet().toArray(new String[0]));
	}

	@Override
	public void readExternal(JSONInput in) {
		menuID = in.getStringValue("menuID");
		AdminMenu template = Application.getMenuTemplates().get(menuID);
		menuName = template.menuName;
		String[] module_ids = in.getObject("modules",String[].class);
		if(module_ids!=null && module_ids.length>0){
			for (String mid : module_ids) {
				modules.put(mid, template.modules.get(mid));
			}
		}
	}
}
