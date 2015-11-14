package org.guyou.util.excel;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.guyou.util.MD5;
import org.guyou.util.MyArrayList;
import org.guyou.util.StringUtil;

/**
 * 生成器类 主要负责生成 与excel对应的java语言文件和 ActionScript语言文件 的bean
 * 和java语言自动数据加载内存机制文件
 * @author 王烁
 * @date 2012-11-14
 * @version 1.0
 */
public class Generator {
	private static String OUT_PATH_FOR_JAVA = "";
	
	private static String RES_OUT_PATH_FOR_JAVA = "";
	
	private static String FILE_OUT_PATH_FOR_AS = "";
	
	private static String RES_OUT_PATH_FOR_AS = "";
	
	private static String FILE_OUT_PATH_FOR_C = "";
	
	private static String RES_OUT_PATH_FOR_C = "";
	
	private static String FILE_OUT_PATH_FOR_LUA = "";
	
	public static void init_for_java(String fileout,String resout){
		OUT_PATH_FOR_JAVA = fileout==null ? "" : (fileout.endsWith("/") ? fileout.substring(0,fileout.length()-1) : fileout);
		RES_OUT_PATH_FOR_JAVA = resout==null ? "" : (resout.endsWith("/") ? resout.substring(0,resout.length()-1) : resout);
	}
	
	public static void init_for_as(String fileout,String resout){
		FILE_OUT_PATH_FOR_AS = fileout==null ? "" : (fileout.endsWith("/") ? fileout.substring(0,fileout.length()-1) : fileout);
		RES_OUT_PATH_FOR_AS = resout==null ? "" : (resout.endsWith("/") ? resout.substring(0,resout.length()-1) : resout);
	}
	
	public static void init_for_c(String fileout,String resout){
		FILE_OUT_PATH_FOR_C = fileout==null ? "" : (fileout.endsWith("/") ? fileout.substring(0,fileout.length()-1) : fileout);
		RES_OUT_PATH_FOR_C = resout==null ? "" : (resout.endsWith("/") ? resout.substring(0,resout.length()-1) : resout);
	}
	
	public static void init_for_lua(String fileout){
		FILE_OUT_PATH_FOR_LUA = fileout==null ? "" : (fileout.endsWith("/") ? fileout.substring(0,fileout.length()-1) : fileout);
	}
	
	
	/**
	 * 生成器
	 * @param excelPath excel 相对路径 例：(datafile\\player\\simple.xls)
	 * @param fileToPath 文件生成到的路径 例： (cn\\com\\gameserver\\player)
	 * @param srcPackageName 源码包名字  例：("GameServer")
	 */
	public static void generator(File file,String excelPath, String fileToPath) {
		fileToPath = fileToPath.replace(".","/");
		String[] temp = excelPath.split("/");
		
		String excelName = temp.length == 1 ? temp[0] : temp[temp.length - 1];
		excelName = excelName.substring(0,excelName.indexOf("."));
		
		Map<String,List<ExcelHead>> heads = ExcelUtil.getExcelBeans(file);
		Map<String,List<List<Object>>> bodys = ExcelUtil.getExcelData(file,heads);
		try {
			ResFile res = new ResFile();
			res.xlsAbsolutePath = file.getAbsolutePath();
			res.md5 = MD5.getFileHash(file);
			Generator.gen(excelPath,heads, bodys, fileToPath, excelName,res);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成文件
	 * @param heads excel前3行数据
	 * @param bodys excel每行的数据
	 * @param filePath 文件路径
	 * @param excelName excel名字
	 * @param srcPackageName 生成到对应的源码包下
	 * @throws IOException
	 */
	private static void gen(String excelPath,Map<String, List<ExcelHead>> heads, Map<String,List<List<Object>>> bodys, String filePath, String excelName,ResFile res) throws IOException {
		
		String beanPath = filePath + "/" + "bean";
		
		for(String key : heads.keySet()) {
			
			List<ExcelHead> cols = heads.get(key);
			List<List<Object>> rows = bodys.get(key);
			//如果表中无数据
			if(rows == null) rows = new MyArrayList<List<Object>>();
			
			res.datas = ExcelUtil.buildByteArrayByAllRow(cols, rows, key);
			
			if(!OUT_PATH_FOR_JAVA.equals("")){
				genJavaParseManager(excelPath,key,excelName,filePath,res);
				genJavaFile(key,cols,beanPath,res);
			}
			if(!RES_OUT_PATH_FOR_JAVA.equals("")){
				//genDataBinFileForJava(excelPath,key,res.datas);
			}
			if(!FILE_OUT_PATH_FOR_AS.equals("")){
				genAsFile(key,cols,filePath,res);
			}
			if(!RES_OUT_PATH_FOR_AS.equals("")){
				//genDataBinFileForAs(key,res.datas, beanPath);
			}
			
			if(!FILE_OUT_PATH_FOR_C.equals("")){
				genCFile(key,cols,filePath,res);
			}
			if(!RES_OUT_PATH_FOR_C.equals("")){
				//genDataBinFileForC(key,res.datas, beanPath);
			}
			if(!FILE_OUT_PATH_FOR_LUA.equals("")){
				res.LuaTemplate = ExcelUtil.buildLuaByAllRow(cols, rows, key);
				genLuaFile(key,filePath,res.LuaTemplate);
			}
			break;
		}
	}
	
	private static void gen(String excelPath, String filePath, String excelName,String key,ResFile res) throws IOException {
		String beanPath = filePath + "/" + "bean";
		if(!OUT_PATH_FOR_JAVA.equals("")){
			genJavaParseManagerForCache(key,filePath,res.JavaParseManager);
			genJavaFile(key,beanPath,res.JavaTemplate);
		}
		if(!RES_OUT_PATH_FOR_JAVA.equals("")){
			genDataBinFileForJava(excelPath,key,res.datas);
		}
		if(!FILE_OUT_PATH_FOR_AS.equals("")){
			genAsFile(key,filePath,res.AsTemplate);
		}
		if(!RES_OUT_PATH_FOR_AS.equals("")){
			genDataBinFileForAs(key,res.datas, beanPath);
		}
		
		if(!FILE_OUT_PATH_FOR_C.equals("")){
			genCFile(key,filePath,res.CTemplate_h,res.CTemplate_cpp);
		}
		if(!RES_OUT_PATH_FOR_C.equals("")){
			genDataBinFileForC(key,res.datas, beanPath);
		}
		
		if(!FILE_OUT_PATH_FOR_LUA.equals("")){
			genLuaFile(key,filePath,res.LuaTemplate);
		}
	}
	
	/**
	 * 生成Excel Java解析类
	 * @param key 文件名(sheet名)
	 * @param excelName excel文件名字
	 * @param filePath 类路径
	 * @param srcPackageName 源码包名字
	 * @return
	 */
	private static void genJavaParseManager(String excelPath,String key , String excelName ,String filePath,ResFile res) throws IOException {
	
		String folderPath = OUT_PATH_FOR_JAVA + "/" + filePath;
		String fileName = key + "ParseManager" + ".java";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		
		res.JavaParseManager = buildJavaParseManagerString(excelPath,key,excelName,filePath);
		bw.write(res.JavaParseManager);
		bw.flush();
		bw.close();
	}
	
	private static void genJavaParseManagerForCache(String key ,String filePath, String content) throws IOException {
		
		String folderPath = OUT_PATH_FOR_JAVA + "/" + filePath;
		String fileName = key + "ParseManager" + ".java";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		bw.write(content);
		bw.flush();
		bw.close();
	}
	
	/**
	 * 生成java文件
	 * @param key 类名字
	 * @param cols 类属性
	 * @param filePath 类的生成路径
	 * @param srcPackageName 所属源码包名称
	 * @throws IOException
	 */
	private static void genJavaFile(String key,List<ExcelHead> cols,String filePath,ResFile res) throws IOException {
		String folderPath = OUT_PATH_FOR_JAVA + "/" + filePath;
		String fileName = key + ".java";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		FileWriter fw = new FileWriter(pathResult);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		res.JavaTemplate = buildJavaString(key, cols, filePath.replaceAll("/", "."));
		bw.write(res.JavaTemplate);
		bw.flush();
		bw.close();
		fw.close();
	}
	
	private static void genJavaFile(String key,String filePath,String content) throws IOException {
		String folderPath = OUT_PATH_FOR_JAVA + "/" + filePath;
		String fileName = key + ".java";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		bw.write(content);
		bw.flush();
		bw.close();
	}
	
	
	
	/**
	 * 生成as文件
	 * @param key 类名字
	 * @param cols 类属性
	 * @param filePath 类的生成路径
	 * @param srcPackageName 所属源码包名称
	 * @throws IOException
	 */
	private static void genAsFile(String key,List<ExcelHead> cols, String filePath,ResFile res) throws IOException {
		//路径写死
		filePath = filePath.substring(0,filePath.lastIndexOf("/"));
		
		String folderPath = FILE_OUT_PATH_FOR_AS + "/" + filePath;
		String fileName = key + ".as";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		res.AsTemplate = buildAsString(key, cols, filePath.replaceAll("/", "."));
		bw.write(res.AsTemplate);
		bw.flush();
		bw.close();
	}
	
	private static void genAsFile(String key, String filePath,String content) throws IOException {
		//路径写死
		filePath = filePath.substring(0,filePath.lastIndexOf("/"));
		
		String folderPath = FILE_OUT_PATH_FOR_AS + "/" + filePath;
		String fileName = key + ".as";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		bw.write(content);
		bw.flush();
		bw.close();
	}
	
	protected static void genLuaFile(String key, String filePath,String content) throws IOException {
		//路径写死
		filePath = filePath.substring(0,filePath.lastIndexOf("/"));
		//String folderPath = FILE_OUT_PATH_FOR_LUA + "/" + filePath;
		String folderPath = FILE_OUT_PATH_FOR_LUA;
		String fileName = key + ".lua";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		bw.write(content);
		bw.flush();
		bw.close();
	}
	
	/**
	 * 生成as文件
	 * @param key 类名字
	 * @param cols 类属性
	 * @param filePath 类的生成路径
	 * @param srcPackageName 所属源码包名称
	 * @throws IOException
	 */
	private static void genCFile(String key,List<ExcelHead> cols, String filePath,ResFile res) throws IOException {
		//路径写死
		filePath = filePath.substring(0,filePath.lastIndexOf("/"));
		
		String folderPath = FILE_OUT_PATH_FOR_C + "/" + filePath;
		buildFolder(folderPath);
		String fileName = key + ".h";
		String pathResult = (folderPath + "/" + fileName);
		FileWriter fw = new FileWriter(pathResult);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		res.CTemplate_h = buildC_h_String(key, cols);
		bw.write(res.CTemplate_h);
		bw.flush();
		bw.close();
		fw.close();
		
		fileName = key + ".cpp";
		pathResult = (folderPath + "/" + fileName);
		fw = new FileWriter(pathResult);
		bw = new BufferedWriter(fw);
		res.CTemplate_cpp = buildC_cpp_String(key, cols);
		bw.write(res.CTemplate_cpp);
		bw.flush();
		bw.close();
		fw.close();
	}
	
	private static void genCFile(String key, String filePath,String hContent,String cppContent) throws IOException {
		//路径写死
		filePath = filePath.substring(0,filePath.lastIndexOf("/"));
		
		String folderPath = FILE_OUT_PATH_FOR_C + "/" + filePath;
		buildFolder(folderPath);
		String fileName = key + ".h";
		String pathResult = (folderPath + "/" + fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		bw.write(hContent);
		bw.flush();
		bw.close();
		
		fileName = key + ".cpp";
		pathResult = (folderPath + "/" + fileName);
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathResult), "UTF-8"));
		bw.write(cppContent);
		bw.flush();
		bw.close();
	}
	
	private static String buildC_h_String(String key, List<ExcelHead> list){
		StringBuilder sb = new StringBuilder();
		sb.append("#ifndef Hchl_"+key+"_h").append("\n")
		  .append("#define Hchl_"+key+"_h").append("\n")
		  .append("#include \"IExcelBean.h\"").append("\n")
		  .append("#include <string>").append("\n")
		  .append("#include <vector>").append("\n");
		 sb.append("\n");
		 
		 sb.append("class "+key+":public IExcelBean").append("\n")
		   .append("{").append("\n")
		   .append("public:").append("\n");
		
		 for(ExcelHead bean : list){
			 //如果前三行的单元格有空字符串或者空值则忽略
			 if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				 continue;
			 }
			 if(bean.type.equalsIgnoreCase("String")){
				 bean.type="String";
			 }
			 if(bean.type.equalsIgnoreCase("String[]")){
				 bean.type="String[]";
			 }
			 appendDesc(sb, bean.desc);
			 changeLine(sb);
			 if(bean.type.indexOf("[]")==-1){
				 if(bean.type.equalsIgnoreCase("String")){
					 sb.append("\t").append("std::string ").append(bean.title).append(";\n");
				 }else if(bean.type.equalsIgnoreCase("boolean")){
					 sb.append("\t").append("bool ").append(bean.title).append(";\n");
				 }else{
					 sb.append("\t").append(bean.type+" ").append(bean.title).append(";\n");
				 }
			 }else{
				 if(bean.type.indexOf("String")!=-1){
					 sb.append("\t").append("std::vector<std::string> ").append(bean.title).append(";\n");
				 }else if(bean.type.indexOf("int")!=-1){
					 sb.append("\t").append("std::vector<int> ").append(bean.title).append(";\n");
				 }else if(bean.type.indexOf("double")!=-1){
					 sb.append("\t").append("std::vector<double> ").append(bean.title).append(";\n");
				 }else if(bean.type.indexOf("float")!=-1){
					 sb.append("\t").append("std::vector<float> ").append(bean.title).append(";\n");
				 }else if(bean.type.indexOf("boolean")!=-1){
					 sb.append("\t").append("std::vector<bool> ").append(bean.title).append(";\n");
				 }else{
					 sb.append("\t").append("std::vector<"+bean.type+"> ").append(bean.title).append(";\n");
				 }
			 }
		 }
		 sb.append("\n");
		 sb.append("\t").append("virtual void read(ByteBuffer* buffer)").append(";\n");
		 sb.append("\t").append("virtual void write(ByteBuffer** buffer)").append(";\n");
		 sb.append("};").append("\n")
		   .append("#endif");
		 return sb.toString();
	}
	
	private static String buildC_cpp_String(String key, List<ExcelHead> list){
		StringBuilder sb = new StringBuilder();
		sb.append("#include \""+key+".h\"").append("\n")
		  .append("\n");
		sb.append("void "+key+"::read(ByteBuffer* buffer)").append("\n")
		  .append("{").append("\n")
		  .append("\t").append("short len=0;").append("\n");
		  
		for(ExcelHead bean : list){
			 //如果前三行的单元格有空字符串或者空值则忽略
			 if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				 continue;
			 }
			 
			 if(bean.type.indexOf("[]")==-1){
				 if(bean.type.equalsIgnoreCase("String")){
					 sb.append("\t").append("this->"+bean.title+"=buffer->readUTF();").append("\n");
				 }else if(bean.type.equalsIgnoreCase("byte")){
					 sb.append("\t").append("this->"+bean.title+"=buffer->readByte();").append("\n");
				 }else if(bean.type.equalsIgnoreCase("short")){
					 sb.append("\t").append("this->"+bean.title+"=buffer->readShort();").append("\n");
				 }else if(bean.type.equalsIgnoreCase("int")){
					 sb.append("\t").append("this->"+bean.title+"=buffer->readInt();").append("\n");
				 }else if(bean.type.equalsIgnoreCase("double")){
					 sb.append("\t").append("this->"+bean.title+"=buffer->readDouble();").append("\n");
				 }else if(bean.type.equalsIgnoreCase("float")){
					 sb.append("\t").append("this->"+bean.title+"=buffer->readFloat()==1;").append("\n");
				 }else if(bean.type.equalsIgnoreCase("boolean")){
					 sb.append("\t").append("this->"+bean.title+"=buffer->readByte()==1;").append("\n");
				 }
			 }else{
				 if(bean.type.indexOf("String")!=-1){
					 sb.append("\t").append("len=buffer->readShort();").append("\n");
					 sb.append("\t").append("for(int i=0;i<len;i++)").append(" ").append("this->"+bean.title+".push_back(buffer->readUTF());").append("\n");
				 }else if(bean.type.indexOf("int")!=-1){
					 sb.append("\t").append("len=buffer->readShort();").append("\n");
					 sb.append("\t").append("for(int i=0;i<len;i++)").append(" ").append("this->"+bean.title+".push_back(buffer->readInt());").append("\n");
				 }else if(bean.type.indexOf("double")!=-1){
					 sb.append("\t").append("len=buffer->readShort();").append("\n");
					 sb.append("\t").append("for(int i=0;i<len;i++)").append(" ").append("this->"+bean.title+".push_back(buffer->readDouble());").append("\n");
				 }else if(bean.type.indexOf("float")!=-1){
					 sb.append("\t").append("len=buffer->readShort();").append("\n");
					 sb.append("\t").append("for(int i=0;i<len;i++)").append(" ").append("this->"+bean.title+".push_back(buffer->readFloat());").append("\n");
				 }
				 
			 }
		}
		sb.append("}").append("\n");
		sb.append("\n");
		
		sb.append("void "+key+"::write(ByteBuffer** buffer)").append("\n")
		  .append("{").append("\n")
		  .append("\t").append("short len=0;").append("\n")
		  .append("\t").append("ByteBuffer* buf=*buffer;").append("\n");
		 
		
		for(ExcelHead bean : list){
			 //如果前三行的单元格有空字符串或者空值则忽略
			 if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				 continue;
			 }
			 if(bean.type.indexOf("[]")==-1){
				 if(bean.type.equalsIgnoreCase("String")){
					 sb.append("\t").append("buf->putUTF("+bean.title+");").append("\n");
				 }else if(bean.type.equalsIgnoreCase("byte")){
					 sb.append("\t").append("buf->putByte("+bean.title+");").append("\n");
				 }else if(bean.type.equalsIgnoreCase("short")){
					 sb.append("\t").append("buf->putShort("+bean.title+");").append("\n");
				 }else if(bean.type.equalsIgnoreCase("int")){
					 sb.append("\t").append("buf->putInt("+bean.title+");").append("\n");
				 }else if(bean.type.equalsIgnoreCase("double")){
					 sb.append("\t").append("buf->putDouble("+bean.title+");").append("\n");
				 }else if(bean.type.equalsIgnoreCase("boolean")){
					 sb.append("\t").append("buf->putByte("+bean.title+"?1:0);").append("\n");
				 }
			 }else{
				 if(bean.type.indexOf("String")!=-1){
					 sb.append("\t").append("len="+bean.title+".size();").append("\n");
					 sb.append("\t").append("for(int i=0;i<len;i++)").append(" ").append("buf->putUTF("+bean.title+"[i]);").append("\n");
				 }else if(bean.type.indexOf("int")!=-1){
					 sb.append("\t").append("len="+bean.title+".size();").append("\n");
					 sb.append("\t").append("for(int i=0;i<len;i++)").append(" ").append("buf->putInt("+bean.title+"[i]);").append("\n");
				 }
			 }
		}
		
		sb.append("}").append("\n");
		
		return sb.toString();
	}
	
	/**
	 * 生成excel数据二进制文件
	 * @param key 类型(sheet名)
	 * @param cols 列数据
	 * @param rows 行数据
	 * @param filePath 文件的路径
	 * @param srcPackageName 源码包名字
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void genDataBinFileForAs(String key,byte[] datas, String filePath) throws NumberFormatException, IOException {
		String folderPath = RES_OUT_PATH_FOR_AS;
		String fileName = key + ".res";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		FileOutputStream fos = new FileOutputStream(pathResult);
		DataOutputStream bos = new DataOutputStream(fos);
		bos.write(datas);
		bos.flush();
		bos.close();
		fos.close();
	}
	
	
	private static void genDataBinFileForC(String key,byte[] datas, String filePath) throws NumberFormatException, IOException {
		String folderPath = RES_OUT_PATH_FOR_C;
		String fileName = key + ".res";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		FileOutputStream fos = new FileOutputStream(pathResult);
		DataOutputStream bos = new DataOutputStream(fos);
		bos.write(datas);
		bos.flush();
		bos.close();
		fos.close();
	}
	
	private static void genDataBinFileForJava(String excelPath,String className,byte[] datas) throws NumberFormatException, IOException {
		
		String folderPath = RES_OUT_PATH_FOR_JAVA;
		String fileName = excelPath.substring(0,excelPath.lastIndexOf(".")) + ".res";
		buildFolder(folderPath);
		String pathResult = (folderPath + "/" + fileName);
		FileOutputStream fos = new FileOutputStream(pathResult);
		DataOutputStream bos = new DataOutputStream(fos);
		bos.write(datas);
		bos.flush();
		bos.close();
		fos.close();
	}
	
	/**
	 * 构建要创建的文件的文件夹
	 * @param filePath 文件路径
	 * @return
	 */
	private static boolean buildFolder(String filePath) {
		File file = new File(filePath);
		if(file.exists()) {
			return false;
		}
		return file.mkdirs();
	}
	
	/**
	 * 构建Excel Java解析类
	 * @param key 文件名(sheet名)
	 * @param excelName excel文件名字
	 * @param filePath 类路径
	 * @return
	 */
	private static String buildJavaParseManagerString(String excelPath,String key , String excelName ,String filePath) {
		String realFilePath = filePath.replaceAll("/", ".");
		StringBuilder sb = new StringBuilder();
		sb.append("package");
		space(sb);
		sb.append(realFilePath);
		end(sb);
		changeLine(sb);
		changeLine(sb);
		sb.append("import");
		space(sb);
		sb.append("java.util.*");
		end(sb);
		changeLine(sb);
		sb.append("import");
		space(sb);
		sb.append("java.util.Map");
		end(sb);
		changeLine(sb);
		changeLine(sb);
		sb.append("import");
		space(sb);
		sb.append("org.guyou.util.excel.BaseParse");
		end(sb);
		changeLine(sb);
		sb.append("import");
		space(sb);
		sb.append("org.guyou.util.excel.ExcelUtil");
		end(sb);
		changeLine(sb);
		sb.append("import");
		space(sb);
		sb.append("org.guyou.util.excel.ExcelUtil.ExcelDataResult");
		end(sb);
		changeLine(sb);
		sb.append("import");
		space(sb);
		sb.append("org.guyou.util.excel.FieldValueCondition");
		end(sb);
		changeLine(sb);
		sb.append("import");
		space(sb);
		sb.append(realFilePath)
		.append(".bean.")
		.append(key);
		end(sb);
		changeLine(sb);
		changeLine(sb);
		changeLine(sb);
		_public(sb);
		space(sb);
		_class(sb);
		space(sb);
		sb.append(key)
		  .append("ParseManager");
		space(sb);
		sb.append("extends");
		space(sb);
		sb.append("BaseParse<" + key + ">");
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		changeLine(sb);
		tab(sb);
		sb.append("private");
		space(sb);
		sb.append("static");
		space(sb);
		sb.append(key)
		  .append("ParseManager");
		space(sb);
		sb.append("instance");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("null");
		end(sb);
		changeLine(sb);
		changeLine(sb);
		tab(sb);
		sb.append("private");
		space(sb);
		sb.append(key)
		  .append("ParseManager");
		left(sb);
		right(sb);
		space(sb);
		flowerLeft(sb);
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		tab(sb);
		sb.append("private");
		space(sb);
		sb.append("static");
		space(sb);
		sb.append(key)
		  .append("ParseManager");
		space(sb);
		sb.append("getInstance");
		left(sb);
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("if");
		left(sb);
		sb.append("instance");
		space(sb);
		equal(sb);
		equal(sb);
		space(sb);
		sb.append("null");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		tab(sb);
		sb.append("instance");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("new");
		space(sb);
		sb.append(key)
		  .append("ParseManager");
		left(sb);
		right(sb);
		end(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("instance");
		end(sb);
		changeLine(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		
		sb.append("\t").append("public static ExcelDataResult<"+key+"> init() throws Exception{").append("\n");
		sb.append("\t\t").append(key+"ParseManager parse = getInstance();").append("\n");
		sb.append("\t\t").append("parse.xlsFile = new java.io.File(Thread.currentThread().getContextClassLoader().getResource(\""+excelPath+"\").toURI());").append("\n");
		sb.append("\t\t").append("parse.lastModified = parse.xlsFile.lastModified();").append("\n");
		sb.append("\t\t").append("ExcelDataResult<"+key+"> result = ExcelUtil.buildExcelDataMap(parse.xlsFile, "+realFilePath + "." + "bean." + key+".class);").append("\n");
		sb.append("\t\t").append("parse.data = result.datas;").append("\n");
		sb.append("\t\t").append("parse.dataList = new ArrayList<"+key+">(result.datas.values());").append("\n");
		sb.append("\t\t").append("return result;").append("\n");
		sb.append("\t").append("}").append("\n");
		
		changeLine(sb);
		sb.append("\t").append("public static ExcelDataResult<"+ key +"> update() throws Exception {").append("\n");
		sb.append("\t\t").append(key+"ParseManager parse = getInstance();").append("\n");
		sb.append("\t\t").append("ExcelDataResult<"+key+"> r = null;").append("\n");
		sb.append("\t\t").append("if(parse.xlsFile.lastModified()!=parse.lastModified){").append("\n");
		sb.append("\t\t\t").append("r = ExcelUtil.updateExcelData(parse.xlsFile,"+realFilePath + "." + "bean." + key+".class,parse._getAllTemplates(),parse._getAllTemplateList());").append("\n");
		sb.append("\t\t\t").append("if(r!=null) parse.indexByFieldValues.clear();").append("\n");
		sb.append("\t\t\t").append("parse.lastModified = parse.xlsFile.lastModified();").append("\n");
		sb.append("\t\t").append("}").append("\n");
		sb.append("\t\t").append("return r;").append("\n");
		sb.append("\t").append("}").append("\n");
		
		changeLine(sb);
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("static");
		space(sb);
		sb.append(key);
		space(sb);
		sb.append("getTemplate");
		left(sb);
		sb.append("String id");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("getInstance()._getTemplate(id)");
		end(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("static");
		space(sb);
		sb.append("List<" + key + ">");
		space(sb);
		sb.append("getTemplates");
		left(sb);
		sb.append("String... ids");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("getInstance()._getTemplates(ids)");
		end(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("static");
		space(sb);
		sb.append("Map<String," + key + ">");
		space(sb);
		sb.append("getAllTemplates");
		left(sb);
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("getInstance()._getAllTemplates()");
		end(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		
		
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("static");
		space(sb);
		sb.append("List<" + key + ">");
		space(sb);
		sb.append("getAllTemplateList");
		left(sb);
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("getInstance()._getAllTemplateList()");
		end(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		
		
		
		
		
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("static");
		space(sb);
		sb.append("List<" + key + ">");
		space(sb);
		sb.append("getTemplateByField");
		left(sb);
		sb.append("String");
		space(sb);
		sb.append("filedName");
		sb.append(",");
		space(sb);
		sb.append("Object");
		space(sb);
		sb.append("value");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("getInstance()._getTemplateByField");
		left(sb);
		sb.append(realFilePath + "." + "bean." + key);
		dot(sb);
		_class(sb);
		sb.append(",");
		space(sb);
		sb.append("filedName")
		  .append(",");
		space(sb);
		sb.append("value");
		right(sb);
		end(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("static");
		space(sb);
		sb.append("List<" + key + ">");
		space(sb);
		sb.append("getTemplateByField");
		left(sb);
		sb.append("String");
		space(sb);
		sb.append("filedName1");
		sb.append(",");
		space(sb);
		sb.append("Object");
		space(sb);
		sb.append("value1");
		sb.append(",");
		space(sb);
		sb.append("String");
		space(sb);
		sb.append("filedName2");
		sb.append(",");
		space(sb);
		sb.append("Object");
		space(sb);
		sb.append("value2");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("getInstance()._getTemplateByField");
		left(sb);
		sb.append(realFilePath + "." + "bean." + key);
		dot(sb);
		_class(sb);
		sb.append(",");
		space(sb);
		sb.append("new FieldValueCondition[]{new FieldValueCondition(filedName1,value1),new FieldValueCondition(filedName2,value2)}");
		right(sb);
		end(sb);
		changeLine(sb);
		tab(sb);
		
		flowerRight(sb);
		changeLine(sb);
		changeLine(sb);
		
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("static");
		space(sb);
		sb.append("List<" + key + ">");
		space(sb);
		sb.append("getTemplateByField");
		left(sb);
		sb.append("FieldValueCondition[] conditions");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("getInstance()._getTemplateByField");
		left(sb);
		sb.append(realFilePath + "." + "bean." + key);
		dot(sb);
		_class(sb);
		sb.append(",");
		space(sb);
		sb.append("conditions");
		right(sb);
		end(sb);
		changeLine(sb);
		tab(sb);
		
		
		flowerRight(sb);
		changeLine(sb);
		flowerRight(sb);
		
		
		return sb.toString();
	}
	
	/**
	 * 构建java文件字符串
	 * @param key 文件名(sheet名)
	 * @param list (sheet文件里面的前三行信息)
	 * @param packageName 类路径
	 * @return
	 */
	private static String buildJavaString(String key, List<ExcelHead> list, String packageName) {

		StringBuilder sb = new StringBuilder();

		classStart(sb, key, packageName);

		for (int i = 0; i < list.size(); i++) {

			ExcelHead bean = list.get(i);
			//如果前三行的单元格有空字符串或者空值则忽略
			if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				continue;
			}
			if(bean.type.equalsIgnoreCase("String")){
				bean.type="String";
			}
			if(bean.type.equalsIgnoreCase("String[]")){
				bean.type="String[]";
			}
			appendDesc(sb, bean.desc);
			appendTypeAndName(sb, bean.type, bean.title, true);
			end(sb);

		}

		changeLine(sb);
		changeLine(sb);
		readJavaMethodStart(sb);

		for (int i = 0; i < list.size(); i++) {
			ExcelHead bean = list.get(i);
			//如果前三行的单元格有空字符串或者空值则忽略
			if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				continue;
			}
			changeLine(sb);
			tab(sb);
			tab(sb);
			if (isArray(bean.type)) {
				appendReadMethodJavaArray(sb, bean.type, bean.title);
			} else {
				readValue(sb, bean.title, bean.type, true);
			}
		}
		changeLine(sb);
		tab(sb);
		methodEnd(sb);
		changeLine(sb);
		changeLine(sb);
		
		writeJavaMethodStart(sb);
		
		for (int i = 0; i < list.size(); i++) {
			ExcelHead bean = list.get(i);
			//如果前三行的单元格有空字符串或者空值则忽略
			if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				continue;
			}
			changeLine(sb);
			tab(sb);
			tab(sb);
			if (isArray(bean.type)) {
				appendWriteMethodJavaArray(sb, bean.type, bean.title);
			} else {
				writeValue(sb, bean.title, bean.type, true);
			}
		}
		
		changeLine(sb);
		tab(sb);
		methodEnd(sb);
		changeLine(sb);
		changeLine(sb);
		writeJavaGetIdMethod(sb, list.get(0).title);
		
		changeLine(sb);
		cloneJavaMethodStart(key,"newTemplate",sb);

		for (int i = 0; i < list.size(); i++) {
			ExcelHead bean = list.get(i);
			//如果前三行的单元格有空字符串或者空值则忽略
			if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				continue;
			}
			changeLine(sb);
			tab(sb);
			tab(sb);
			sb.append("newTemplate."+bean.title);
			space(sb);
			equal(sb);
			space(sb);
			appendName(sb, bean.title);
			end(sb);
		}
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return newTemplate");
		end(sb);
		changeLine(sb);
		tab(sb);
		methodEnd(sb);
		changeLine(sb);
		
		classEnd(sb);
		return sb.toString();
	}
	/**
	 * 构建as文件字符串
	 * @param key 文件名(sheet名)
	 * @param list (sheet文件里面的前三行信息)
	 * @param packageName 类路径
	 * @return
	 */
	private static String buildAsString(String key, List<ExcelHead> list, String packageName) {
		
		StringBuilder sb = new StringBuilder();

		asClassStart(sb, key, packageName);

		for (int i = 0; i < list.size(); i++) {

			ExcelHead bean = list.get(i);
			//如果前三行的单元格有空字符串或者空值则忽略
			if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				continue;
			}
			
			appendDesc(sb, bean.desc);
			if(bean.type.equalsIgnoreCase("String")){
				bean.type="String";
			}
			if(bean.type.equalsIgnoreCase("String[]")){
				bean.type="String[]";
			}
			appendTypeAndName(sb, bean.type, bean.title, false);
			end(sb);

		}

		changeLine(sb);
		changeLine(sb);
		readAsMethodStart(sb);

		for (int i = 0; i < list.size(); i++) {
			ExcelHead bean = list.get(i);
			//如果前三行的单元格有空字符串或者空值则忽略
			if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				continue;
			}
			changeLine(sb);
			tab(sb);
			tab(sb);
			if (isArray(bean.type)) {
				appendReadMethodAsArray(sb, bean.type, bean.title);
			} else {
				readValue(sb, bean.title, bean.type, false);
			}
		}
		changeLine(sb);
		tab(sb);
		methodEnd(sb);
		changeLine(sb);
		changeLine(sb);
		
		writeAsMethodStart(sb);
		
		for (int i = 0; i < list.size(); i++) {
			ExcelHead bean = list.get(i);
			//如果前三行的单元格有空字符串或者空值则忽略
			if((bean.desc == null || bean.desc.equals("")) || (bean.title == null || bean.title.equals("")) || (bean.type == null || bean.type.equals(""))) {
				continue;
			}
			changeLine(sb);
			tab(sb);
			tab(sb);
			if (isArray(bean.type)) {
				appendWriteMethodAsArray(sb, bean.type, bean.title);
			} else {
				writeValue(sb, bean.title, bean.type, false);
			}
		}
		
		changeLine(sb);
		tab(sb);
		methodEnd(sb);
		changeLine(sb);
		changeLine(sb);
		writeAsGetIdMethod(sb, list.get(0).title);
		
		asClassEnd(sb);
		return sb.toString();
	}
	
	private static boolean isArray(String type) {
		return type.indexOf("[]") != -1;
	}
	
	private static String deleteArrayMark(String type) {
		int index = type.indexOf("[");
		return new StringBuilder(type).delete(index, index + 2).toString();
	}
	private static void javaReadMethodArrayLength(StringBuilder sb,String name) {
		sb.append("int");
		space(sb);
		sb.append(name)
		.append("Length");
		space(sb);
		equal(sb);
		space(sb);
		appendDataInputStreamName(sb);
		dot(sb);
		sb.append("readShort()");
		end(sb);
	}
	
	
	private static void asReadMethodArrayLength(StringBuilder sb,String name) {
		sb.append("var");
		space(sb);
		sb.append(name)
		.append("Length");
		space(sb);
		colon(sb);
		space(sb);
		sb.append("int");
		space(sb);
		equal(sb);
		space(sb);
		appendDataInputStreamName(sb);
		dot(sb);
		sb.append("readShort()");
		end(sb);
	}
	
	private static void javaWriteMethodArrayLength(StringBuilder sb,String name) {
		sb.append("int");
		space(sb);
		sb.append(name)
		.append("Length");
		space(sb);
		equal(sb);
		space(sb);
		sb.append(name);
		space(sb);
		equal(sb);
		equal(sb);
		space(sb);
		sb.append("null");
		space(sb);
		sb.append("?");
		space(sb);
		sb.append("0");
		colon(sb);
		sb.append(name);
		dot(sb);
		sb.append("length");
		end(sb);
	}
	
	private static void asWriteMethodArrayLength(StringBuilder sb,String name) {
		sb.append("var");
		space(sb);
		sb.append(name)
		.append("Length");
		colon(sb);
		sb.append("int");
		space(sb);
		equal(sb);
		space(sb);
		sb.append(name);
		space(sb);
		equal(sb);
		equal(sb);
		space(sb);
		sb.append("null");
		space(sb);
		sb.append("?");
		space(sb);
		sb.append("0");
		colon(sb);
		sb.append(name);
		dot(sb);
		sb.append("length");
		end(sb);
	}
	
	private static void appendReadMethodJavaArray(StringBuilder sb,String type,String name) {
		javaReadMethodArrayLength(sb, name);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("if");
		left(sb);
		sb.append(name)
		.append("Length");
		space(sb);
		sb.append(">");
		space(sb);
		sb.append("0");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		tab(sb);
		appendName(sb, name);
		space(sb);
		equal(sb);
		space(sb);
		sb.append("new");
		space(sb);
		sb.append(deleteArrayMark(type))
		 .append("[")
		 .append(name)
		 .append("Length")
		 .append("]");
		 end(sb);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 sb.append("for");
		 left(sb);
		 sb.append("i");
		 space(sb);
		 equal(sb);
		 space(sb);
		 sb.append("0");
		 end(sb);
		 sb.append("i");
		 space(sb);
		 sb.append("<");
		 space(sb);
		 sb.append(name)
		 .append("Length");
		 end(sb);
		 sb.append("i++");
		 right(sb);
		 space(sb);
		 flowerLeft(sb);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 tab(sb);
		 readValue(sb, name + "[i]", deleteArrayMark(type),true);
//		 int index = sb.indexOf("*") - 1 ;
//		 int lastIndex = index + 1 + name.length() + 3 + 2 ;
//		 sb.delete(index, lastIndex);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 tab(sb);
		 flowerRight(sb);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 flowerRight(sb);
		 changeLine(sb);
	}

	private static void appendReadMethodAsArray(StringBuilder sb,String type,String name) {
		String realtype = type.replace("[]", "");
		asReadMethodArrayLength(sb, name);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("if");
		left(sb);
		sb.append(name)
		.append("Length");
		space(sb);
		sb.append(">");
		space(sb);
		sb.append("0");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		tab(sb);
		appendName(sb, name);
		space(sb);
		equal(sb);
		space(sb);
		sb.append("new");
		space(sb);
		sb.append("Vector.<"+convertToAsType(realtype)+">("+name+"Length)");  
		 end(sb);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 sb.append("for");
		 left(sb);
		 sb.append("i");
		 space(sb);
		 equal(sb);
		 space(sb);
		 sb.append("0");
		 end(sb);
		 sb.append("i");
		 space(sb);
		 sb.append("<");
		 space(sb);
		 sb.append(name)
		 .append("Length");
		 end(sb);
		 sb.append("i++");
		 right(sb);
		 space(sb);
		 flowerLeft(sb);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 tab(sb);
		 readValue(sb,name + "[i]", deleteArrayMark(type),false);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 tab(sb);
		 flowerRight(sb);
		 changeLine(sb);
		 tab(sb);
		 tab(sb);
		 flowerRight(sb);
		 changeLine(sb);
	}
	
	private static void appendWriteMethodJavaArray(StringBuilder sb,String type,String name) {
		javaWriteMethodArrayLength(sb, name);
		changeLine(sb);
		tab(sb);
		tab(sb);
		appendDataOutputStreamName(sb);
		dot(sb);
		sb.append("writeShort");
		left(sb);
		sb.append(name)
		  .append("Length");
		right(sb);
		end(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("for");
		left(sb);
		sb.append("i");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("0");
		end(sb);
		sb.append("i");
		space(sb);
		sb.append("<");
		space(sb);
		sb.append(name);
		sb.append("Length");
		end(sb);
		sb.append("i++");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		writeValue(sb, name + "[i]", deleteArrayMark(type),true);
		changeLine(sb);
		tab(sb);
		tab(sb);
		flowerRight(sb);
	}
	
	private static void appendWriteMethodAsArray(StringBuilder sb,String type,String name) {
		asWriteMethodArrayLength(sb, name);
		changeLine(sb);
		tab(sb);
		tab(sb);
		appendDataOutputStreamName(sb);
		dot(sb);
		sb.append("writeShort");
		left(sb);
		sb.append(name)
		  .append("Length");
		right(sb);
		end(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("for");
		left(sb);
		sb.append("i");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("0");
		end(sb);
		sb.append("i");
		space(sb);
		sb.append("<");
		space(sb);
		sb.append(name);
		sb.append("Length");
		end(sb);
		sb.append("i++");
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		writeValue(sb, name + "[i]", deleteArrayMark(type),false);
		changeLine(sb);
		tab(sb);
		tab(sb);
		flowerRight(sb);
	}
	/**
	 * 是否是一个基本类型
	 * @param type 类型
	 * @return
	 */
	private static boolean isBaseType(String type) {
		if(type.equals("byte")) {
			return true;
		}
		if(type.equals("short")) {
			return true;
		}
		if(type.equals("int")) {
			return true;
		}
		if(type.equals("long")) {
			return true;
		}
		if(type.equals("char")) {
			return true;
		}
		if(type.equals("String")) {
			return true;
		}
		if(type.equals("float")) {
			return true;
		}
		if(type.equals("double")) {
			return true;
		}
		if(type.equals("boolean")) {
			return true;
		}
		return false;
	}
	
	/**
	 * tab
	 * @param sb
	 */
	private static void tab(StringBuilder sb) {
		sb.append("    ");
	}
	
	/**
	 * 空格
	 * @param sb
	 */
	private static void space(StringBuilder sb) {
		sb.append(" ");
	}
	
	/**
	 * 换行
	 * @param sb
	 */
	private static void changeLine(StringBuilder sb) {
		sb.append("\r\n");
	}
	/**
	 * 分号
	 * @param sb
	 */
	private static void end(StringBuilder sb) {
		sb.append(";");
	}
	
	/**
	 * 冒号
	 * @param sb
	 */
	private static void colon(StringBuilder sb) {
		sb.append(":");
	}
	private static void appendDesc(StringBuilder sb,String desc) {
		changeLine(sb);
		String[] descList = desc.split("\n");
		if(descList.length>0){
			for(String str:descList){
				tab(sb);
				sb.append("//").append(str).append("\n");
				//changeLine(sb);
			}
			StringUtil.deleteEndsMark(sb,"\n");
		}else{
			tab(sb);
			sb.append("//");
			//changeLine(sb);
		}
	}
	private static void appendTypeAndName(StringBuilder sb,String type,String name,boolean isJava) {
		
		if(isJava) {
			changeLine(sb);
			tab(sb);
			_public(sb);
			space(sb);
			appendJavaType(sb,type);
			space(sb);
			appendName(sb,name);
		}
		else {
			changeLine(sb);
			tab(sb);
			_public(sb);
			space(sb);
			sb.append("var");
			space(sb);
			appendName(sb, name);
			colon(sb);
			appendAsType(sb, type);
		}
	}
	
	private static void dot(StringBuilder sb) {
		sb.append(".");
	}
	
	private static void equal(StringBuilder sb) {
		sb.append("=");
	}
	
	private static void _public(StringBuilder sb) {
		sb.append("public");
	}
	
	private static void _class(StringBuilder sb) {
		sb.append("class");
	}
	
	private static void _void(StringBuilder sb) {
		sb.append("void");
	}
	
	private static void _function(StringBuilder sb) {
		sb.append("function");
	}
	
	private static void left(StringBuilder sb) {
		sb.append("(");
	}
	
	private static void right(StringBuilder sb) {
		sb.append(")");
	}
	private static void flowerLeft(StringBuilder sb) {
		sb.append("{");
	}
	private static void flowerRight(StringBuilder sb) {
		sb.append("}");
	}
	
	private static String convertToAsType(String type) {
		String realtype = type.replace("[]", "");
		if(type.equals("double") || type.equals("float") || type.equals("long")) {
			return "Number";
		}
		else if(type.equals("byte") || type.equals("short")) {
			return "int";
		}
		else if(type.equals("boolean")) {
			return "Boolean";
		}
		else if(type.contains("[]")) {
			return "Vector.<"+convertToAsType(realtype)+">";
		}
		return type;
	}
	
	private static void appendDataOutputStreamName(StringBuilder sb) {
		sb.append("dos");
	}
	private static void appendDataInputStreamName(StringBuilder sb) {
		sb.append("dis");
	}
	
	private static void classStart(StringBuilder sb,String className, String packageName) {
		
		if(!packageName.equals("")) {
			sb.append("package");
			space(sb);
			sb.append(packageName);
			end(sb);
			changeLine(sb);
		}
		sb.append("@com.alibaba.fastjson.annotation.JSONType(ignores={\"id\",\"detail\"})").append("\n");
		_public(sb);
		space(sb);
		_class(sb);
		space(sb);
		sb.append(className);
		space(sb);
		sb.append("extends org.guyou.util.excel.IExcelBean");
		space(sb);
		flowerLeft(sb);
	}
	
	private static void asClassStart(StringBuilder sb,String className, String packageName) {
		sb.append("package");
		space(sb);
		sb.append(packageName);
		if(!packageName.equals("")) {
			space(sb);
		}
		flowerLeft(sb);
		changeLine(sb);
		sb.append("import org.guyou.util.excel.IExcelBean;");
		changeLine(sb);
		sb.append("import flash.utils.ByteArray;");
		changeLine(sb);
		changeLine(sb);
		_public(sb);
		space(sb);
		_class(sb);
		space(sb);
		sb.append(className);
		space(sb);
		sb.append("implements IExcelBean");
		space(sb);
		flowerLeft(sb);
	}
	
	private static void asClassEnd(StringBuilder sb) {
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		flowerRight(sb);
	}
	
	private static void classEnd(StringBuilder sb) {
		flowerRight(sb);
	}
	
	/**
	 * 添加变量名
	 * @param sb
	 * @param name
	 */
	private static void appendName(StringBuilder sb,String name) {
		sb.append(name);
	}
	
	/**
	 * 添加java语言类型
	 * @param sb
	 * @param type
	 */
	private static void appendJavaType(StringBuilder sb,String type) {
		sb.append(type);
	}
	
	/**
	 * 添加as语言类型
	 * @param sb
	 * @param type
	 */
	private static void appendAsType(StringBuilder sb,String type) {
		String temp = convertToAsType(type);
		appendJavaType(sb,temp);
	}
	
	private static void readJavaMethodStart(StringBuilder sb) {
		tab(sb);
		_public(sb);
		space(sb);
		_void(sb);
		space(sb);
		sb.append("read");
		left(sb);
		sb.append("java.io.DataInputStream");
		space(sb);
		appendDataInputStreamName(sb);
		right(sb);
		space(sb);
		sb.append("throws java.io.IOException");
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("int");
		space(sb);
		sb.append("i");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("0");
		end(sb);
	}
	
	private static void cloneJavaMethodStart(String className,String newVar,StringBuilder sb) {
		tab(sb);
		_public(sb);
		space(sb);
		sb.append(className);
		space(sb);
		sb.append("clone");
		left(sb);
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append(className);
		space(sb);
		sb.append(newVar);
		space(sb);
		equal(sb);
		space(sb);
		sb.append("new "+className+"()");
		end(sb);
	}
	
	private static void readAsMethodStart(StringBuilder sb) {
		tab(sb);
		_public(sb);
		space(sb);
		_function(sb);
		space(sb);
		sb.append("read");
		left(sb);
		appendDataInputStreamName(sb);
		colon(sb);
		sb.append("flash.utils.ByteArray");
		right(sb);
		colon(sb);
		_void(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("var");
		space(sb);
		sb.append("i");
		colon(sb);
		sb.append("int");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("0");
		end(sb);
	}
	
	private static void writeJavaMethodStart(StringBuilder sb) {
		tab(sb);
		_public(sb);
		space(sb);
		_void(sb);
		space(sb);
		sb.append("write");
		left(sb);
		sb.append("java.io.DataOutputStream");
		space(sb);
		appendDataOutputStreamName(sb);
		right(sb);
		space(sb);
		sb.append("throws java.io.IOException");
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("int");
		space(sb);
		sb.append("i");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("0");
		end(sb);
	}
	
	private static void writeAsMethodStart(StringBuilder sb) {
		tab(sb);
		_public(sb);
		space(sb);
		_function(sb);
		space(sb);
		sb.append("write");
		left(sb);
		appendDataOutputStreamName(sb);
		colon(sb);
		sb.append("flash.utils.ByteArray");
		right(sb);
		colon(sb);
		_void(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("var");
		space(sb);
		sb.append("i");
		colon(sb);
		sb.append("int");
		space(sb);
		equal(sb);
		space(sb);
		sb.append("0");
		end(sb);
	}
	
	private static void methodEnd(StringBuilder sb) {
		flowerRight(sb);
	}
	
	private static void readValue(StringBuilder sb,String name,String type,boolean isJava) {
		
		if(isJava && type.equals("long")) {
			readJavaLong(sb, name, type);
			return ;
		}
		else if((!isJava) && type.equals("long")) {
			readAsLong(sb, name, type);
			return ;
		}
		
		appendName(sb, name);
		space(sb);
		equal(sb);
		space(sb);
		appendDataInputStreamName(sb);
		dot(sb);
		sb.append("read");
		if(isBaseType(type)) {
			if(type.equals("String")) {
				sb.append("UTF");
			}
			else {
				String first = String.valueOf(type.charAt(0));
				String firstUp = first.toUpperCase();
				sb.append(type.replace(first, firstUp));
			}
			left(sb);
		}
		else {
			left(sb);
			appendDataInputStreamName(sb);
		}
		right(sb);
		end(sb);
	}

	private static void writeValue(StringBuilder sb,String name,String type,boolean isJava) {
		
		if(isJava && type.equals("long")) {
			writeJavaLong(sb, name, type);
			return ;
		}
		else if((!isJava) && type.equals("long")) {
			writeAsLong(sb, name, type);
			return ;
		}
		
		appendDataOutputStreamName(sb);
		dot(sb);
		sb.append("write");
		
		boolean isBaseType = isBaseType(type);
		if(isBaseType) {
			if(type.equals("String")) {
				sb.append("UTF");
			}
			else {
				String first = String.valueOf(type.charAt(0));
				String firstUp = first.toUpperCase();
				sb.append(type.replace(first, firstUp));
			}
		}
		left(sb);
		if(isBaseType) {
			appendName(sb, name);
		}
		else {
			appendDataOutputStreamName(sb);
			
		}
		right(sb);
		end(sb);
	}
	
	
	
	private static void readJavaLong(StringBuilder sb,String name,String type) {
		sb.append(name);
		space(sb);
		equal(sb);
		space(sb);
		sb.append("((long)dis.readInt())*100000000+(long)dis.readInt();");
	}
	
	
	private static void readAsLong(StringBuilder sb,String name,String type) {
		sb.append(name);
		space(sb);
		equal(sb);
		space(sb);
		sb.append("Number(dis.readInt())*100000000+Number(dis.readInt());");
	}
	
	private static void writeAsLong(StringBuilder sb,String name,String type) {
		appendDataOutputStreamName(sb);
		dot(sb);
		sb.append("writeInt(")
		  .append("int("+ name +"/100000000)");
		sb.append(")");
		end(sb);
		changeLine(sb);
		tab(sb);
		appendDataOutputStreamName(sb);
		dot(sb);
		sb.append("writeInt(")
		.append("int("+ name +"%100000000)")
		.append(")");
		end(sb);
		
	}
	
	private static void writeJavaLong(StringBuilder sb,String name,String type) {
		appendDataOutputStreamName(sb);
		dot(sb);
		sb.append("writeInt(")
		.append("(int)("+ name +"/100000000)")
        .append(")");
		end(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		appendDataOutputStreamName(sb);
		dot(sb);
		sb.append("writeInt(")
		.append("((int)"+ name +"%100000000)")
		.append(")");
		end(sb);
	}
	
	private static void writeJavaGetIdMethod(StringBuilder sb, String idName) {
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("String");
		space(sb);
		sb.append("getId");
		left(sb);
		right(sb);
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("this");
		dot(sb);
		sb.append(idName);
		end(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
		
		sb.append("\t").append("public String getPkName(){").append("\n");
		sb.append("\t\t").append("return \""+idName+"\";").append("\n");
		sb.append("\t").append("}").append("\n");
		sb.append("\n");
		
		
	}
	
	private static void writeAsGetIdMethod(StringBuilder sb, String idName) {
		tab(sb);
		_public(sb);
		space(sb);
		sb.append("function");
		space(sb);
		sb.append("getId");
		left(sb);
		right(sb);
		space(sb);
		sb.append(":");
		space(sb);
		sb.append("String");
		space(sb);
		flowerLeft(sb);
		changeLine(sb);
		tab(sb);
		tab(sb);
		sb.append("return");
		space(sb);
		sb.append("this");
		dot(sb);
		sb.append(idName);
		end(sb);
		changeLine(sb);
		tab(sb);
		flowerRight(sb);
		changeLine(sb);
	}
}

