package org.guyou.util.excel;

import java.awt.Point;
import java.text.NumberFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class GPoiUtils {

	public static int getIntValue(Cell cell) {
		if (cell == null || cell.toString().trim().equals("")) {
			return 0;
		}
		try {
			return (int) Double.parseDouble(cell.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
			// System.out.println(cell.toString());
			throw e;
		}
	}

	public static double getDoubleValue(Cell cell) {
		if (cell == null) {
			return 0.0;
		}
		try {
			String temp = cell.toString().trim();
			if (temp.indexOf("%") != -1) {
				double data = Double.parseDouble(temp.substring(0,
						temp.indexOf("%")));
				return data / 100.0;
			} else {
				return Double.parseDouble(cell.toString());
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			// System.out.println(cell.toString());
			throw e;
		}
	}

	public static String getStringValue(XSSFCell cell) {
		if (cell == null) {
			return "";
		}
		String str = cell.toString().trim();
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING: {
				return String.valueOf(cell.getRichStringCellValue());
			}
			case Cell.CELL_TYPE_NUMERIC: {
				NumberFormat nf = NumberFormat.getInstance();
				nf.setGroupingUsed(false);// true时的格式：1,234,567,890
				double acno = cell.getNumericCellValue();// 将科学技术法的值转换为计算之前的值
				return nf.format(acno);
//				if (str.endsWith(".0")) {
//					return str.substring(0, str.length() - 2);
//				} else {
//					return str;
//				}
			}
			case Cell.CELL_TYPE_FORMULA:{
				return cell.getRawValue();
				
				/*
				if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_ERROR) {
                    return new CellValue("formula error").toString();
                }
                CellValue cellResultValue = eval.evaluate(cell);
                if (cellResultValue.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                    NumberFormat nf = NumberFormat.getInstance();
       				nf.setGroupingUsed(false);// true时的格式：1,234,567,890
       				double acno = cell.getNumericCellValue();// 将科学技术法的值转换为计算之前的值
       				str = nf.format(acno);
       				if (str.endsWith(".0")) {
       					return str.substring(0, str.length() - 2);
       				} else {
       					return str;
       				}
                }else{
                	String stringValue = cellResultValue.getStringValue();
                    if (null != stringValue) {
                        str = stringValue.replaceAll("'", "''");
                    } else {
                        str = "";
                    }
                }
                break;
                */
			}
			case Cell.CELL_TYPE_BLANK: {
				return "";
			}
			case Cell.CELL_TYPE_BOOLEAN: {
				// TODO
			}
			default: {
			}
		}
		
		return str;
	}
	
	public static float getFloatValue(Cell cell) {
		if (cell == null) {
			return 0;
		}
		try {
			return Float.parseFloat(cell.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
			// System.out.println(cell.toString());
			throw e;
		}
	}

	public static String getIntString(Cell cell) {
		return "" + getIntValue(cell);
	}

	/**
	 * 获得坐标集合，适合于：[100#200][150#210] 这种形式
	 * 
	 * @param str
	 * @return
	 */
	public static Point[] getPoints(String str) {
		String temp = str.substring(1, str.lastIndexOf("]"));
		String[] points = temp.replace("][", ",").split(",");
		if (points.length > 0) {
			Point[] result = new Point[points.length];
			for (int i = 0; i < points.length; i++) {
				String[] xy = points[i].split("#");
				result[i] = new Point(Integer.parseInt(xy[0]),
						Integer.parseInt(xy[1]));
			}
			return result;
		}

		return null;
	}

	/**
	 * 获得坐标集合，适合于：[100#200] 这种形式
	 * 
	 * @param str
	 * @return
	 */
	public static Point getOnePoint(String str) {
		Point p = new Point();
		if (str != null && str.length() > 0) {
			String temp = str.substring(1, str.length() - 1);
			String[] pos = temp.split("#");
			p.x = Integer.parseInt(pos[0]);
			p.y = Integer.parseInt(pos[1]);
		}
		return p;
	}
}
