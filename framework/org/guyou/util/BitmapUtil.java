/**
 * create by 朱施健
 */
package org.guyou.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * @author 朱施健
 *
 */
public class BitmapUtil {
	
	/**
     * 根据图片路径生成缩略图
     * @param originalImgFile   原图片文件
     * @param w            		缩略图宽
     * @param h            		缩略图高
     * @param force        		是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     * @param thumbnailImage	缩略图文件
	 * @throws IOException 
     */
    public static void thumbnailImage(File originalImgFile, int w, int h, boolean force,File thumbnailImageFile) throws IOException{
    	if(!originalImgFile.exists()){
    		throw new FileNotFoundException("原始图不存在");
    	}
    	String originalImgName = originalImgFile.getName();
        // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
        String types = Arrays.toString(ImageIO.getReaderFormatNames());
        String suffix = null;
        // 获取图片后缀
        if(originalImgName.indexOf(".") > -1) {
            suffix = originalImgName.substring(originalImgName.lastIndexOf(".") + 1);
        }
        // 类型和图片后缀全部小写，然后判断后缀是否合法
        if(suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0){
            throw new IllegalStateException("原始图无扩展名或类型不支持");
        }
        Image img = ImageIO.read(originalImgFile);
        if(!force){
            // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
            int width = img.getWidth(null);
            int height = img.getHeight(null);
            if((width*1.0)/w < (height*1.0)/h){
                if(width > w){
                    h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w/(width*1.0)));
                }
            } else {
                if(height > h){
                    w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h/(height*1.0)));
                }
            }
        }
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
        g.dispose();
        ImageIO.write(bi, suffix,thumbnailImageFile);
    }
    
    public static void main(String[] args) throws IOException {
    	thumbnailImage(new File("D:/朱施健.JPG"), 200, 200, false, new File("D:/朱施健_min.JPG"));
	}
}
