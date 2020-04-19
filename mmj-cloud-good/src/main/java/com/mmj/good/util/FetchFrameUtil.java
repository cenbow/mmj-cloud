package com.mmj.good.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FetchFrameUtil {
    /**
     * 获取指定视频的帧并保存为图片至指定目录
     * @param videofile  源视频文件路径
     * @throws Exception
     */
    public static InputStream fetchFrame(String videofile)
            throws Exception {
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(videofile);
        ff.start();
        int lenght = ff.getLengthInFrames();
        int i = 0;
        Frame f = null;
        while (i < lenght) {
            // 过滤前5帧，避免出现全黑的图片，依自己情况而定
            f = ff.grabFrame();
            if ((i > 48) && (f.image != null)) {
                break;
            }
            i++;
        }
        if (f != null) {
            //int owidth = f.imageWidth;
            //int oheight = f.imageHeight;
            // 对截取的帧进行等比例缩放
            int width = 380;
            //int height = (int) (((double) width / owidth) * oheight);
            int height = 380;
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage fecthedImage = converter.getBufferedImage(f);
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            bi.getGraphics().drawImage(fecthedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH),
                    0, 0, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            ff.stop();
            return input;
        }
        return null;
    }

    /**
     * 获取指定视频的帧并保存为图片至指定目录
     * @param inputStream  源视频流
     * @throws Exception
     */
    public static InputStream fetchFrame(InputStream inputStream)
            throws Exception {
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(inputStream);
        ff.start();
        int lenght = ff.getLengthInFrames();
        int i = 0;
        Frame f = null;
        while (i < lenght) {
            // 过滤前5帧，避免出现全黑的图片，依自己情况而定
            f = ff.grabFrame();
            if ((i > 48) && (f.image != null)) {
                break;
            }
            i++;
        }
        if (f != null) {
            //int owidth = f.imageWidth;
            //int oheight = f.imageHeight;
            // 对截取的帧进行等比例缩放
            int width = 380;
            //int height = (int) (((double) width / owidth) * oheight);
            int height = 380;
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage fecthedImage = converter.getBufferedImage(f);
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            bi.getGraphics().drawImage(fecthedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH),
                    0, 0, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            ff.stop();
            return input;
        }
        return null;
    }
}
