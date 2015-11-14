package org.guyou.util.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.guyou.util.MyArrayList;
import org.guyou.util.StringUtil;

/**
 * excel辅助工具类
 * 
 * @author 朱施健
 * @date 2012-11-14
 * @version 1.0
 */
public class ExcelUtil {

	private static final Logger log = Logger.getLogger(ExcelUtil.class);

	/**
	 * 获取excel列数
	 * 
	 * @param row
	 *            excel第一行对象
	 * @return excel列数
	 */
	public static int getColumnsNum(Row row) {
		return row.getLastCellNum();
	}

	/**
	 * 获取excel中的模板表头信息
	 * 
	 * @param filePath
	 * @return
	 */
	public static Map<String, List<ExcelHead>> getExcelBeans(File xlsFile) {
		Map<String, List<ExcelHead>> map = new LinkedHashMap<String, List<ExcelHead>>();
		XSSFWorkbook wb = null;
		InputStream inp = null;

		try {
			inp = new FileInputStream(xlsFile);
			wb = new XSSFWorkbook(inp);
			
			for (int i = 0; i < 1; i++) {

				XSSFSheet sheet = wb.getSheetAt(i);

				XSSFRow row0 = sheet.getRow(0);
				XSSFRow row1 = sheet.getRow(1);
				XSSFRow row2 = sheet.getRow(2);

				if (row0 == null)
					continue;
				if (row1 == null)
					continue;
				if (row2 == null)
					continue;

				List<ExcelHead> list = new MyArrayList<ExcelHead>();

				for (int k = 0; k < getColumnsNum(row0); k++) {

					ExcelHead bean = new ExcelHead();
					bean.desc = GPoiUtils.getStringValue(row0.getCell(k));
					bean.title = GPoiUtils.getStringValue(row1.getCell(k));
					bean.type = GPoiUtils.getStringValue(row2.getCell(k));
					list.add(bean);
				}
				map.put(sheet.getSheetName(), list);
			}
			inp.close();
		} catch (IOException e) {
			log.error("解析表头错误", e);
		}

		return map;
	}
	
	public static String getExcelSheetName(File xlsFile) {
		XSSFWorkbook wb = null;
		InputStream inp = null;

		String result = "";
		try {
			inp = new FileInputStream(xlsFile);
			wb = new XSSFWorkbook(inp);
			if(wb.getNumberOfSheets()>0){
				result = wb.getSheetAt(0).getSheetName();
			}
			inp.close();
		} catch (IOException e) {
			log.error("解析表头错误", e);
		}
		return result;
	}

	/**
	 * 获取excel中的数据
	 * 
	 * @param filePath
	 *            文件名字
	 * @return
	 */
	public static Map<String, List<List<Object>>> getExcelData(File xlsFile,Map<String,List<ExcelHead>> heads) {
		XSSFWorkbook wb = null;
		InputStream inp = null;
		Map<String, List<List<Object>>> result = new LinkedHashMap<String, List<List<Object>>>();

		try {
			inp = new FileInputStream(xlsFile);
			wb = new XSSFWorkbook(inp);
			
			for (int i = 0; i < 1; i++) {
				XSSFSheet sheet = wb.getSheetAt(i);

				if (sheet == null)
					continue;

				if (sheet.getPhysicalNumberOfRows() <= 3)
					continue;
				
				List<ExcelHead> head = heads.get(sheet.getSheetName());

				Map<String,List<Object>> rows = new LinkedHashMap<String,List<Object>>();
				int strat = sheet.getFirstRowNum()+3;
				int end = sheet.getLastRowNum();
				for (int j = strat; j <= end; j++) {

					XSSFRow row = sheet.getRow(j);
					if (row == null)
						continue;

					List<Object> cells = new MyArrayList<Object>();
					String id = "";
					
					for (int k = 0; k < head.size(); k++) {
						if(k>=head.size()) break;
						
						XSSFCell cell = row.getCell(k);

						try {
							String str = GPoiUtils.getStringValue(cell).trim();
							if(k==0 && (str == null || str.equals(""))) break;
							if(k==0) id = str;
							cells.add(str);
						} catch (Exception e) {
							ExcelHead eh = head.get(k);
							if(!eh.desc.equals("") && !eh.title.equals("") && !eh.type.equals("")){
								log.error("发生错误的表：" + xlsFile.getAbsolutePath() + "  " + (j + 1)+ "行    "+k+"列",e);
							}
							cells.add("");
						}
					}
					if(cells.size()>0 && !"".equals(id)){
						if(rows.containsKey(id)){
							IllegalStateException e = new IllegalStateException("ID重复的表：" + xlsFile.getAbsolutePath()+" id="+id);
							log.error("ID重复的表：" + xlsFile.getAbsolutePath() +" id="+id ,e);
							throw e;
						}
						rows.put(id, cells);
					}
				}
				result.put(sheet.getSheetName(), new ArrayList<List<Object>>(rows.values()));
			}
			inp.close();

		} catch (IOException e1) {
			log.error("解析表数据错误", e1);
		}
		return result;
	}

	/**
	 * 构建一个对应excel模板的 java数据结构
	 * 
	 * @param filePath
	 *            文件名
	 * @param clazz
	 *            类模板
	 * @return 对应excel模板的 java数据结构
	 * @throws Exception
	 */
	public static <T extends IExcelBean> ExcelDataResult<T> buildExcelDataMap(
			File xlsFile, Class<?> clazz) throws Exception {
		String[] allClassName = clazz.getName().split("\\.");
		String className = allClassName.length == 1 ? allClassName[0]: allClassName[allClassName.length - 1];
		Map<String, T> sheetMap = new LinkedHashMap<String, T>();
		Map<String,List<ExcelHead>> heads = getExcelBeans(xlsFile);
		List<ExcelHead> cols = heads.get(className);
		List<List<Object>> rows = getExcelData(xlsFile,heads).get(className);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				T bean = (T) clazz.newInstance();
				List<Object> row = rows.get(i);
				ByteArrayInputStream bais = new ByteArrayInputStream(buildByteArray(cols, row, className));
				DataInputStream dis = new DataInputStream(bais);
				bean.read(dis);
				dis.close();
				bais.close();
				if(sheetMap.containsKey(bean.getId())){
					log.error("["+xlsFile.getAbsolutePath()+"]ID重复,请策划检查!id="+bean.getId(),new IllegalStateException("["+xlsFile.getAbsolutePath()+"]ID重复,请策划检查!id="+bean.getId()));
				}
				sheetMap.put(bean.getId(), bean);
			}
		}
		return new ExcelDataResult<T>(sheetMap, cols, rows, className);
	}
	
	public static <T extends IExcelBean> ExcelDataResult<T> updateExcelData(
			File xlsFile, Class<?> clazz,Map<String,T> oldDatas,List<T> oldList) throws Exception {
		String[] allClassName = clazz.getName().split("\\.");
		String className = allClassName.length == 1 ? allClassName[0]: allClassName[allClassName.length - 1];
		Map<String,T> addedMap = new LinkedHashMap<String, T>();
		Map<String,List<ExcelHead>> heads = getExcelBeans(xlsFile);
		List<ExcelHead> cols = heads.get(className);
		List<List<Object>> rows = getExcelData(xlsFile,heads).get(className);
		if (rows != null && rows.size() > 0) {
			for (int i = 0; i < rows.size(); i++) {
				T bean = (T) clazz.newInstance();
				List<Object> row = rows.get(i);
				ByteArrayInputStream bais = new ByteArrayInputStream(buildByteArray(cols, row, className));
				DataInputStream dis = new DataInputStream(bais);
				bean.read(dis);
				dis.close();
				bais.close();
				if(oldDatas.containsKey(bean.getId())){
					T oo = oldDatas.get(bean.getId());
					if(!oo.equals(bean)){
						oldDatas.get(bean.getId()).update(bean);
						addedMap.put(bean.getId(), bean);
					}
				}else{
					oldDatas.put(bean.getId(), bean);
					oldList.add(bean);
					addedMap.put(bean.getId(), bean);
				}
			}
		}
		if(addedMap.size()>0){
			log.warn("["+xlsFile.getAbsolutePath()+"]改变了数据");
		}
		return addedMap.size()>0 ? new ExcelDataResult(addedMap, cols, rows, className) : null;
	}

	/**
	 * 构建一个 和对应excel行数据顺序一样的 有序字节数组
	 * 
	 * @param cols
	 *            所有列数据
	 * @param row
	 *            行数据
	 * @param className
	 *            类名
	 * @return 对应excel行数据顺序一样的 有序字节数组
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static byte[] buildByteArray(List<ExcelHead> cols, List<Object> row,
			String className) throws NumberFormatException, IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		for (int i = 0; i < cols.size(); i++) {

			ExcelHead bean = cols.get(i);

			// 如果前三行的单元格有空字符串或者空值则忽略
			if ((bean.desc == null || bean.desc.equals(""))
					|| (bean.title == null || bean.title.equals(""))
					|| (bean.type == null || bean.type.equals(""))) {
				continue;
			}

			try {
				writeData(dos, bean, i >= row.size() ? "" : row.get(i).toString());
			} catch (Exception e) {
				log.error("\n页签名:" + className + "\n注释:" + bean.desc + "\n名称:"
						+ bean.title + "\n类型:" + bean.type + "\n值:"
						+ row.get(i).toString(),e);
			}
		}
		byte[] b = baos.toByteArray();
		dos.flush();
		dos.close();
		baos.flush();
		baos.close();

		return b;
	}

	/**
	 * 构建一个 和对应excel行数据顺序一样的 有序字节数组
	 * 
	 * @param cols
	 *            所有列数据
	 * @param rows
	 *            所有行数据
	 * @param className
	 *            类名
	 * @return 对应excel中的一个sheet所有行数据 有序字节数组集合 行与行之间 以 字符串(yes|no)分割 如遇到no则代表
	 *         不在有下一元素
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static byte[] buildByteArrayByAllRow(List<ExcelHead> cols,
			List<List<Object>> rows, String className)
			throws NumberFormatException, IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		DataOutputStream bos = new DataOutputStream(baos);

		int len = rows.size();
		bos.writeShort(len);
		for (int i = 0; i < len; i++) {

			byte[] buff = buildByteArray(cols, rows.get(i), className);

			baos.write(buff);

		}
		byte[] result = baos.toByteArray();
		bos.flush();
		bos.close();
		baos.flush();
		baos.close();
		return result;
	}
	
	
	public static String buildLuaByAllRow(List<ExcelHead> cols,
			List<List<Object>> rows, String className)
			throws NumberFormatException, IOException {

		StringBuilder sb = new StringBuilder(className+" = {").append("\n");
		int len = rows.size();
		for (int i = 0; i < len; i++) {
			List<Object> row = rows.get(i);
			String idVaule = row.get(0).toString();
			sb.append("\t").append("[\""+idVaule+"\"] = {");
			for (int j = 0; j < cols.size(); j++) {
				ExcelHead bean = cols.get(j);

				// 如果前三行的单元格有空字符串或者空值则忽略
				if ((bean.desc == null || bean.desc.equals(""))
						|| (bean.title == null || bean.title.equals(""))
						|| (bean.type == null || bean.type.equals(""))) {
					continue;
				}
				try {
					String value = row.get(j).toString();
					String type = bean.type;
					if (type.equals("byte")) {
						sb.append("[\""+bean.title+"\"]="+("".equals(value)?0:value)+",");
					} else if (type.equals("short")) {
						sb.append("[\""+bean.title+"\"]="+("".equals(value)?0:value)+",");
					} else if (type.equals("int")) {
						sb.append("[\""+bean.title+"\"]="+("".equals(value)?0:value)+",");
					} else if (type.equals("long")) {
						sb.append("[\""+bean.title+"\"]="+("".equals(value)?0:value)+",");
					} else if (type.equals("float")) {
						sb.append("[\""+bean.title+"\"]="+("".equals(value)?0:value)+",");
					} else if (type.equals("double")) {
						sb.append("[\""+bean.title+"\"]="+value+",");
					} else if (type.equals("boolean")) {
						sb.append("[\""+bean.title+"\"]="+("1".equals(value)?1:0)+",");
					} else if (type.toUpperCase().equals("STRING")) {
						sb.append("[\""+bean.title+"\"]=\""+value+"\",");
					} else if (type.equals("short[]")) {

					} else if (type.equals("int[]")) {
					
					} else if (type.equals("long[]")) {
					} else if (type.equals("float[]")) {
					} else if (type.equals("double[]")) {
					} else if (type.equals("boolean[]")) {
					} else if (type.toUpperCase().equals("STRING[]")) {
					}
				} catch (Exception e) {
					log.error("\n页签名:" + className + "\n注释:" + bean.desc + "\n名称:"
							+ bean.title + "\n类型:" + bean.type + "\n值:"
							+ row.get(i).toString(),e);
				}
			}
			StringUtil.deleteEndsMark(sb, ",");
			sb.append("},\n");
		}
		StringUtil.deleteEndsMark(sb, ",\n");
		sb.append("\n}\n");
		sb.append("return "+className);
		return sb.toString();
	}
	

	/**
	 * 将数据写入到 数据流中
	 * 
	 * @param dos
	 *            数据流
	 * @param type
	 *            数据类型
	 * @param value
	 *            值
	 * @throws NumberFormatException
	 *             转换格式异常
	 * @throws IOException
	 *             磁盘异常
	 */
	public static void writeData(DataOutputStream dos, ExcelHead bean,
			String value) throws NumberFormatException, IOException {
		String type = bean.type;
		if (type.equals("byte")) {

			byte temp = value == null || value.equals("") ? 0 : (byte) parseInt(value, true);

			dos.writeByte(temp);
		} else if (type.equals("short")) {

			short temp = value == null || value.equals("") ? 0
					: (short) parseInt(value, true);

			dos.writeShort(temp);
		} else if (type.equals("int")) {

			int temp = value == null || value.equals("") ? 0 : parseInt(value, true);
			dos.writeInt(temp);
		} else if (type.equals("long")) {

			long temp = value == null || value.equals("") ? 0 : Long
					.parseLong(value);
			long v = temp;
			dos.writeInt((int) (v / 100000000));
			dos.writeInt((int) (v % 100000000));
		} else if (type.equals("float")) {

			float temp = value == null || value.equals("") ? 0 : Float
					.parseFloat(value);

			dos.writeFloat(temp);
		} else if (type.equals("double")) {
			double temp = value == null || value.equals("") ? 0 : Double
					.parseDouble(value);
			dos.writeDouble(temp);
		} else if (type.equals("boolean")) {

			boolean temp = value == null || value.equals("") ? false : (value
					.equals("1") || value.equalsIgnoreCase("true"));

			dos.writeBoolean(temp);
		} else if (type.toUpperCase().equals("STRING")) {
			String temp = value == null || value.equals("") ? "" : value;
			dos.writeUTF(temp);
		} else if (type.equals("byte[]")) {

			if (value == null || value.equals("")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					dos.writeByte(Byte.parseByte(temps[i]));
				}
			}
		} else if (type.equals("short[]")) {

			if (value == null || value.equals("")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					dos.writeShort(Short.parseShort(temps[i]));
				}
			}

		} else if (type.equals("int[]")) {
			if (value == null || value.equals("")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					dos.writeInt(Integer.parseInt(temps[i]));
				}
			}
		} else if (type.equals("long[]")) {
			if (value == null || value.equals("")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					long v = Long.parseLong(temps[i]);
					dos.writeInt((int) (v / 100000000));
					dos.writeInt((int) (v % 100000000));
				}
			}
		} else if (type.equals("float[]")) {
			if (value == null || value.equals("")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					dos.writeFloat(Float.parseFloat(temps[i]));
				}
			}
		} else if (type.equals("double[]")) {
			if (value == null || value.equals("")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					dos.writeDouble(Double.parseDouble(temps[i]));
				}
			}
		} else if (type.equals("boolean[]")) {
			if (value == null || value.equals("")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					dos.writeBoolean(Boolean.parseBoolean(temps[i]));
				}
			}
		} else if (type.toUpperCase().equals("STRING[]")) {
			if (value == null || value.equals("") || value.equals("0")) {
				dos.writeShort(0);
			} else {
				String[] temps = getStringArrayForExcel(bean.desc, value);
				dos.writeShort(temps.length);
				for (int i = 0; i < temps.length; i++) {
					dos.writeUTF(temps[i]);
				}
			}
		}
	}

	/**
	 * 获取分隔符
	 * 
	 * @param titleDesc
	 *            表头描述
	 * @return
	 */
	public static String[] getStringArrayForExcel(String titleDesc, String value) {

		titleDesc = titleDesc.replace("；", ";").replace("，", ",");
		value = value.replace("；", ";").replace("，", ",");

		String mark = null;
		if (titleDesc.indexOf(";") != -1) {
			mark = ";";
		} else if (titleDesc.indexOf(",") != -1) {
			mark = ",";
		}

		String[] result = null;
		boolean isHaveF = value.indexOf(";") != -1;
		// 有分号
		if (isHaveF) {
			result = value.split(";");
		}
		// 没有分号
		else {
			// 先判断title中是否有分号
			if (titleDesc.indexOf(";") != -1) {
				result = value.split(";");
			}
			// title中也没有分号，说明是以逗号作为分隔
			else {
				result = value.split(",");
			}
		}
		if (result == null) {
			result = new String[] { value };
		}
		return result;
	}
	
	private static int parseInt(String s, boolean isforce)
			throws NumberFormatException {
		if (!isforce) {
			return Integer.parseInt(s, 10);
		}
		if (s == null) {
			throw new NumberFormatException("null");
		}
		if (s.equals("")) {
			throw new NumberFormatException("\"\"");
		}
		int index = s.indexOf(".");
		int isadd = 0;
		if (s.indexOf(".") != -1 && index > 0) {
			if (index < s.length() - 1) {
				int t = Integer.parseInt(s.substring(index + 1, index + 2));
				isadd = t > 4 ? 1 : 0;
			}
			s = s.substring(0, index);
		}
		return Integer.parseInt(s, 10) + isadd;
	}
	
	public static class ExcelDataResult<T extends IExcelBean>{
		public Map<String,T> datas;
		public List<ExcelHead> cols;
		public List<List<Object>> rows;
		public String fileName;
		
		public ExcelDataResult(Map<String, T> changes,
									 List<ExcelHead> cols,
									 List<List<Object>> rows,
								 String fileName){
			this.datas = changes;
			this.cols = cols;
			this.rows = rows;
			this.fileName = fileName;
		}
	}
}
