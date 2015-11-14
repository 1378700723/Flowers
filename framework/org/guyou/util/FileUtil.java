/**
 * 
 */
package org.guyou.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.guyou.io.UnicodeReader;


/**
 * @author 朱施健
 *
 */
public class FileUtil {
	
	private static Logger log = Logger.getLogger(FileUtil.class);
	
	private static int BUFFER = 1024;
	
	public static List<File> getFileList(File file,String[] suffixs){
		return getFileList(file,new ArrayList<File>(),suffixs,null,null);
	}
	
	/**
	 * 获得文件列表
	 * @param file
	 * @param suffixs 文件后缀集合
	 * @return
	 */
	public static List<File> getFileList(File file,String[] suffixs,String[] nameMatche,String[] nameNotMatche){
		return getFileList(file,new ArrayList<File>(),suffixs,nameMatche,nameNotMatche);
	}
	
	public static List<File> getFileList(File file,List<File> fileList,String[] suffixs){
		return getFileList(file,fileList,suffixs,null,null);
	}
	
	public static List<File> directoryList(String root) throws IOException {
		File rootFile = new File(root);
		if(!rootFile.exists()) throw new FileNotFoundException("["+root+"]目录不存在");
		if(!rootFile.isDirectory()) throw new FileNotFoundException("["+root+"]不是目录");
		List<File> list = new ArrayList<File>();
		list.add(rootFile);
		return directoryList0(rootFile,list);
	}
	
	private static List<File> directoryList0(File file,List<File> fileList){
		if(file.getAbsolutePath().contains(".svn")) return fileList;
		if(file.isDirectory()){
			fileList.add(file);
			File[] fs = file.listFiles();
			for (File f : fs) {
				directoryList0(f,fileList);
			}
		}
		return fileList;
	}
	
	
	/**
	 * 获得文件列表
	 * @param file
	 * @param fileList
	 * @param suffixs 文件后缀集合
	 * @return
	 */
	public static List<File> getFileList(File file,List<File> fileList,String[] suffixs,String[] nameMatche,String[] nameNotMatche){
		if(file.getAbsolutePath().contains(".svn")) return fileList;
		if(file.isFile()){
			String filename = file.getName();
			boolean isNameRight = false;
			if(nameMatche!=null && nameMatche.length>0){
				for (String str : nameMatche) {
					if(filename.toLowerCase().contains(str.toLowerCase())){
						isNameRight = true;
						break;
					}
				}
			}else{
				isNameRight = true;
			}
			if(isNameRight){
				if(nameNotMatche!=null && nameNotMatche.length>0){
					for (String str : nameNotMatche) {
						if(filename.toLowerCase().contains(str.toLowerCase())){
							isNameRight = false;
							break;
						}
					}
				}
			}
			if(isNameRight){
				if(suffixs==null || suffixs.length==0){
					fileList.add(file);
				}else{
					int index = filename.lastIndexOf(".");
					if(index!=-1){
						boolean isright = false;
						String suffix = filename.substring(index,filename.length());
						for (String str : suffixs) {
							if(suffix.equalsIgnoreCase(str)){
								isright = true;
								break;
							}
						}
						if(isright){
							fileList.add(file);
						}
					}
				}
			}
		}else if(file.isDirectory()){
			File[] fs = file.listFiles();
			for (File f : fs) {
				getFileList(f,fileList,suffixs,nameMatche,nameNotMatche);
			}
		}
		return fileList;
	}
	
	/**
	 * 复制文件
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
    	if(!targetFile.getParentFile().exists()){
    		targetFile.getParentFile().mkdirs();
    	}
    	BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
    
    public static void copyFile(File sourceFile, String dstPath) throws IOException {
    	File targetFile = new File(dstPath);
    	if(!targetFile.exists()){
    		targetFile.mkdirs();
    	}
    	File dstFile = new File(targetFile.getAbsolutePath()+ File.separator +sourceFile.getName());
    	BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(dstFile));

            // 缓冲数组
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
    
    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
    	File sourceFile = new File(sourceDir);
    	// 新建目标目录
        File targetFile = new File(targetDir);
        if(!targetFile.exists()){
        	targetFile.mkdirs();
        }
        // 获取源文件夹当前下的文件或目录
        File[] file = sourceFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return !f.getAbsolutePath().contains(".svn");
			}
		});
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sF = file[i];
				// 目标文件
				File tF = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sF, tF);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
     }
    
    
    /**
     * 获得文件绑定数据
     * @param propertiesAbsolutePath
     * @return
     */
    public static Map<String,String> getResourceBundleByAbsolutePath(String propertiesAbsolutePath){
    	Map<String,String> map = new HashMap<String,String>();
    	try {
    		InputStream inputstream = new FileInputStream(new File(propertiesAbsolutePath));
    		BufferedReader in = new BufferedReader(new UnicodeReader(inputstream, SystemUtil.DEFAULT_CHARSET.name()));
    		String s = in.readLine();
    		while(s != null){
    			if(!s.equals("") && s.indexOf("=")!=-1){
    				String data = s.trim();
    				if(!data.startsWith("#")){
    					String[] tmp = data.trim().split("=");
    					map.put(tmp[0].trim(), tmp[1].trim());
    				}
    			}
    			s = in.readLine();
    		}
    		in.close();
    		inputstream.close();
		} catch (Exception e) {
			log.error("getResourceBundleByAbsolutePath",e);
		}
    	return map;
    }
    
    /**
     * 获得文件绑定数据
     * @param propertiesRelativePath
     * @return
     */
    public static Map<String,String> getResourceBundleByRelativePath(String propertiesRelativePath){
    	Map<String,String> map = new LinkedHashMap<String,String>();
    	try {
    		InputStream inputstream = getResourceAsStream(propertiesRelativePath);
    		if(inputstream==null){
    			return map;
    		}
    		BufferedReader in = new BufferedReader(new UnicodeReader(inputstream, SystemUtil.DEFAULT_CHARSET.name()));
    		String s = in.readLine();
    		while(s != null){
    			if(!s.equals("") && s.indexOf("=")!=-1){
    				String data = s.trim();
    				if(!data.startsWith("#")){
    					String key = data.substring(0,data.indexOf("=")).trim();
    					String value = data.substring(data.indexOf("=")+1,data.length()).trim();
    					map.put(key, value);
    				}
    			}
    			s = in.readLine();
    		}
    		in.close();
    		inputstream.close();
		} catch (Exception e) {
			log.error("getResourceBundleByRelativePath",e);
		}
    	return map;
    }
    
    public static void zipFile(File root,String outFileFullName,String[] suffixs,String[] nameMatche,String[] nameNotMatche) throws Exception{  
    	zipFile0(root, outFileFullName, getFileList(root,suffixs,nameMatche,nameNotMatche));
    }
    
    public static void zipFile(File root,String outFileFullName,List<File> fileList) throws Exception{  
    	zipFile0(root, outFileFullName, fileList);
    }
    
    private static void zipFile0(File root,String outFileFullName,List<File> fileList) throws Exception{  
        ZipOutputStream zos=new ZipOutputStream(new FileOutputStream(outFileFullName)); 
        zos.setLevel(Deflater.BEST_COMPRESSION);
        ZipEntry ze=null;  
        byte[] buf=new byte[BUFFER];  
        int readLen=0;  
        for(int i = 0; i <fileList.size(); i++) {  
            File f=fileList.get(i);  
            ze=new ZipEntry(getAbsFileName(root, f));  
            ze.setSize(f.length());  
            ze.setTime(f.lastModified());
            zos.putNextEntry(ze); 
            InputStream is=new BufferedInputStream(new FileInputStream(f));
            while ((readLen=is.read(buf, 0, BUFFER))!=-1) {  
                zos.write(buf, 0, readLen);  
            }  
            is.close();
        }  
        zos.close();  
    }
    
    public static InputStream getResourceAsStream(String filePath) throws FileNotFoundException{
    	Thread.currentThread().getContextClassLoader();
		InputStream inputstream = ClassLoader.getSystemResourceAsStream(filePath);
		if(inputstream==null){
			inputstream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		}
		if(inputstream==null){
			inputstream = new FileInputStream(new File(filePath));
		}
		return inputstream;
    }
    
    
    /**
     * 读取zip文件内容
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Map<String,byte[]> readZipFile(String fileFillName){
    	Map<String,byte[]> result = new HashMap<String, byte[]>();
    	FileInputStream fis = null;
    	ZipInputStream zis = null;
    	try {
    		fis = new FileInputStream(fileFillName);
    		zis = new ZipInputStream(new BufferedInputStream(fis));
    		ZipEntry entry = null;
    		while ((entry = zis.getNextEntry()) != null) {
    			System.out.println("Extracting: " + entry  +"  isDirectory:"+entry.isDirectory());
    			if(entry.isDirectory()) continue;
    			int count;
    			byte[] data = new byte[BUFFER];
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			BufferedOutputStream dest = new BufferedOutputStream(baos, BUFFER);
    			while ((count = zis.read(data, 0, BUFFER)) != -1) {
    				dest.write(data, 0, count);
    			}
    			dest.flush();
    			dest.close();
    			result.put(entry.getName(), baos.toByteArray());
    			baos.reset();
    			baos.close();
    		}
    		zis.close();
    		fis.close();
		} catch (IOException e) {
			log.error("readZipFile",e);
		}finally{
			try {
				if(zis!=null) zis.close();
				if(fis!=null) fis.close();
			} catch (IOException e) {
			}
		}
		return result;
    }
    
    public static byte[] readFile(File file){
    	BufferedInputStream inBuff = null;
        byte[] result = null;
    	try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(file));
            result = readFile(inBuff);
        } catch(IOException e){
        	log.error("readFile(File file)",e);
        }finally {
            try {
            	if (inBuff != null) inBuff.close();
			} catch (IOException e) {
			}
        }
    	return result==null?new byte[0]:result;
    }
    
    /**
     * 获得文件绑定数据
     * @param propertiesAbsolutePath
     * @return
     */
    public static String readTextFile(File file){
    	InputStream inputstream = null;
    	BufferedReader in = null;
    	StringBuilder sb = new StringBuilder();
    	try {
    		inputstream = new FileInputStream(file);
    		in = new BufferedReader(new UnicodeReader(inputstream, SystemUtil.DEFAULT_CHARSET.name()));
    		String s = in.readLine();
    		while(s != null){
    			s = s.trim();
    			if(!s.equals("")){
    				sb.append(s).append("\n");
    			}
    			s = in.readLine();
    		}
		} catch (Exception e) {
			log.error("readTextFile(File file)",e);
		}finally{
			try {
				if(in!=null)in.close();
				if(inputstream!=null)inputstream.close();
			} catch ( Exception e2 ) {
			}
		}
    	return sb.toString();
    }
    
    public static String readTextFile(URL url){
    	BufferedReader in = null;
    	InputStream inputstream = null;
    	StringBuilder sb = new StringBuilder();
    	try {
    		inputstream = url.openStream();
    		in = new BufferedReader(new UnicodeReader(inputstream, SystemUtil.DEFAULT_CHARSET.name()));
    		String s = in.readLine();
    		while(s != null){
    			if(!s.equals("")){
    				sb.append(s).append("\n");
    			}
    			s = in.readLine();
    		}
    		in.close();
		} catch (Exception e) {
			log.error("readTextFile(URL url)",e);
		}finally{
			try {
				if(in!=null)in.close();
				if(inputstream!=null)inputstream.close();
			} catch ( Exception e2 ) {
			}
		}
    	return sb.toString();
    }
    
    public static void writeFile(String fileFullName,String content) throws IOException{
    	writeFile(fileFullName, content, SystemUtil.DEFAULT_CHARSET.name());
    }
    
    public static void writeFile(String fileFullName,String content,String charset) throws IOException{
    	BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileFullName), charset));
		w.write(content);
		w.flush();
		w.close();
    }
    
    public static void writeFile(String fileFullName,byte[] datas) throws IOException{
    	File f = new File(fileFullName);
    	if(!f.getParentFile().exists()){
    		f.getParentFile().mkdirs();
    	}
    	FileOutputStream fos = new FileOutputStream(f);
    	fos.write(datas);
    	fos.flush();
		fos.close();
    }
    
    public static byte[] readFile(InputStream inBuff){
    	ByteArrayOutputStream outBuff = null;
        byte[] result = null;
    	try {
            outBuff = new ByteArrayOutputStream();
            // 缓冲数组
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
            result = outBuff.toByteArray();
        } catch(Exception e){
        	log.error("readFile",e);
        } catch(Error error){
        	log.error("readFile",error);
        } finally {
            try {
                if (outBuff != null) outBuff.close();
			} catch (IOException e) {
			}
        }
    	return result==null?new byte[0]:result;
    }
    
    public static byte[] readFile(URL url){
    	InputStream in = null; 
    	ByteArrayOutputStream outBuff = null;
        byte[] result = null;
    	try {
    		in = url.openStream();
            outBuff = new ByteArrayOutputStream();
            // 缓冲数组
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = in.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
            result = outBuff.toByteArray();
        } catch(Exception e){
        	log.error("readFile(URL url)",e);
        } catch(Error error){
        	log.error("readFile(URL url)",error);
        } finally {
            try {
                if (outBuff != null) outBuff.close();
                if(in!=null) in.close();
            } catch (IOException e) {
			}
        }
    	return result==null?new byte[0]:result;
    }
    
    private static String getAbsFileName(File root, File realFileName){  
        File real=realFileName;  
        String ret=real.getName();  
        while (true) {  
            real=real.getParentFile();  
            if(real==null)   
                break;  
            if(real.equals(root))   
                break;  
            else 
                ret=real.getName()+"/"+ret;  
        }  
        return ret;  
    } 
    
    public static String getUserDir(){
		String classpath = "";
		String userdir = System.getProperty("user.dir").replace("\\","/");
		URL url =Thread.currentThread().getContextClassLoader().getResource("");
		if(url==null){
			classpath = userdir;
		}else{
			classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		}
		//解决中文目录问题
		try {
			classpath = URLDecoder.decode(classpath, SystemUtil.DEFAULT_CHARSET.name());
		} catch (UnsupportedEncodingException e) {
			log.error("getUserDir",e);
		}
		if(classpath.endsWith("/")){
			classpath = classpath.substring(0,classpath.length()-1);
		}
		return classpath;
	}
    
	public static void deleteFile(File file,boolean isIncludeDirectory){
		if(file.exists()){
			if(file.isDirectory()){
				File[] tempFileList = file.listFiles();
				for(File f:tempFileList){
					if(f.isFile()){
						f.delete();
					}else if(f.isDirectory()){
						deleteFile(f,isIncludeDirectory);
						if(isIncludeDirectory) f.delete();
					}
				}
			}else{
				file.delete();
			}
		}
	}
    
	public static enum FileContentKind{
		TEXT,BIN;
	}
	
	public static class DynamicFile{
		private File file;
		private long lastModifyTime;
		private FileContentKind kind;
		private Object data;
		private Object tmp;
		
		public DynamicFile(FileContentKind kind ,String filePath){
			if(filePath==null 
					|| "".equals(filePath) 
					|| ".".equals(filePath)
					|| "/".equals(filePath)){
				throw new NullPointerException("文件路径正确");
			}
			File f = new File(filePath);
			if(!f.exists()){
				throw new NullPointerException("文件不存在("+filePath+")");
			}
			if(!f.isFile()){
				throw new IllegalStateException("不是文件("+filePath+")");
			}
			this.file = f;
			this.lastModifyTime = file.lastModified();
			this.kind = kind;
			if(this.kind == FileContentKind.TEXT){
				this.data =  clearComment(readTextFile(f));
			}else{
				this.data = readFile(f);
			}
		}
		
		public DynamicFile(FileContentKind kind ,File f){
			if(!f.exists()){
				throw new NullPointerException("文件不存在("+f.getAbsolutePath()+")");
			}
			if(!f.isFile()){
				throw new IllegalStateException("不是文件("+f.getAbsolutePath()+")");
			}
			this.file = f;
			this.lastModifyTime = file.lastModified();
			this.kind = kind;
			if(this.kind == FileContentKind.TEXT){
				this.data =  clearComment(readTextFile(f));
			}else{
				this.data = readFile(f);
			}
		}
		
		public File getFile(){
			return this.file;
		}
		
		public boolean isModified(){
			return file.lastModified()!=lastModifyTime;
		}
		
		public Object getContent(){
			if(file.lastModified()!=lastModifyTime){
				if(this.kind == FileContentKind.TEXT){
					this.data =  clearComment(readTextFile(this.file));
				}else{
					this.data = readFile(this.file);
				}
				this.lastModifyTime = file.lastModified();
			}
			return this.data;
		}
		
		public Map<String,String> getBundle(){
			if(this.kind != FileContentKind.TEXT){
				throw new IllegalStateException("文件非文本文件");
			}
			
			boolean isChange = this.file.lastModified()!=lastModifyTime;
			if(isChange){
				this.data =  clearComment(readTextFile(this.file));
				this.lastModifyTime = file.lastModified();
			}
			if(tmp==null || isChange){
				Map<String,String> bundle = new HashMap<String, String>();
				String[] lines = ((String)this.data).split("(\\r\\n)|(\\n)");
				for (String line : lines) {
					int index = line.indexOf("=");
					if(!line.equals("") && index>0){
    					String key = line.substring(0,index).trim();
    					String value = line.substring(index+1,line.length()).trim();
    					bundle.put(key, value);
	    			}
				}
				this.tmp =  bundle;
			}
			return (Map<String,String>)this.tmp;
		}
		
		private String clearComment(String txt){
			String[] strs = txt.split("(\\r\\n)|(\\n)");
			StringBuilder sb = new StringBuilder();
			for ( String str : strs ) {
				String str_tmp = str.trim();
				int index1 = str_tmp.indexOf("//");
				int index2 = str_tmp.indexOf("#");
				if(index1!=-1 && index2!=-1){
					str_tmp = str_tmp.substring(0, Math.min(index1, index2));
				}else if(index1==-1 && index2==-1){
					
				}else{
					str_tmp = str_tmp.substring(0, Math.max(index1, index2));
				}
				if("".equals(str_tmp)) continue;
				sb.append(str_tmp).append("\n");
			}
			return StringUtil.deleteEndsMark(sb, "\n").toString();
		}
		
		public Map getContent_Map(){
			boolean isChange = file.lastModified()!=lastModifyTime;
			if(isChange){
				if(this.kind == FileContentKind.TEXT){
					this.data =  clearComment(readTextFile(this.file));
				}else{
					this.data = readFile(this.file);
				}
				this.lastModifyTime = file.lastModified();
			}
			if(tmp==null || isChange){
				if(this.kind == FileContentKind.TEXT){
					this.tmp =  SerializeUtil.jsonStringToObject(clearComment((String)this.data), HashMap.class);
				}else{
					this.tmp = SerializeUtil.byteToObject((byte[])this.data);
				}
			}
			return (Map) this.tmp;
		}
	}
    
    public static void main(String[] args) throws Exception {
    	System.out.println(new URL("http://res.king.37.com/wz_gn/index.html").toString());
    	String bytes = readTextFile(new URL("http://res.king.37.com/wz_gn/index.html"));
    	System.out.println(bytes);
	}
}
