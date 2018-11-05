package nirvana.cash.loan.privilege.common.util;
/**
 * 生成JPG格式的图片验证验证码,存入图片缓冲区BufferedImage
 *
 * @author YangBin
 */

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

@Data
public class CodeImageUtil {
    private int height = 33;
    private int width = 146;
    private int num = 4;
    private int fontSize = 28;
    private String verifyCode;

    Random rd = new Random();

    /**
     * @return 一个随机颜色的Color
     */
    public Color getColor() {
        return new Color(rd.nextInt(255), rd.nextInt(255), rd.nextInt(255));
    }

    /**
     * @return 一个随记的数字或者字母
     */
    public String getStr() {
        String s = "23456789abcdefghigkmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String ch = String.valueOf(s.charAt(rd.nextInt(s.length())));
        return ch;
    }

    /**
     * 绘制一条任意的线
     *
     * @param g 画笔对象
     */
    public void randomLine(Graphics2D g) {
        int ax = rd.nextInt(width), ay = rd.nextInt(height);
        int bx = rd.nextInt(width), by = rd.nextInt(height);
        g.drawLine(ax, ay, bx, by);
    }

    /**
     * @return 返回一个图片缓冲区对象 BufferedImage 包含了绘制完成的图片.
     */
    protected BufferedImage getBufferedImage() {
        StringBuilder sb = new StringBuilder();
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        sb.delete(0, num);
        Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 28);
        for (int i = 0; i < num; i++) {
            g.setColor(getColor());
            g.setFont(font);
            randomLine(g);
            String s = getStr();
            sb.append(s);
            g.drawString(s, i * (width / num), height / 2 + rd.nextInt(height / 2));
        }
        verifyCode=sb.toString();
        return bi;
    }

    public byte[] getImageBytes(){
        try{
            BufferedImage bufferedImage =  this.getBufferedImage();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] imageInByte = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return imageInByte;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

}