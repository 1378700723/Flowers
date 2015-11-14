/**
 * create by 朱施健
 */
package org.guyou.util.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 朱施健
 *
 */
public abstract class LuaFileCreater {
	protected List<String> fileNames = new ArrayList<String>(2);
	protected List<String> contents = new ArrayList<String>(2);
	
	public void addFile(String fileName,String content){
		fileNames.add(fileName);
		contents.add(content);
	}
	
	public abstract void createFile(String path) throws Exception;
	
	public void clear(){
		fileNames.clear();
		contents.clear();
	}
}
