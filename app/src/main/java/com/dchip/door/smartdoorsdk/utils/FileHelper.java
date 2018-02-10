package com.dchip.door.smartdoorsdk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by jelly on 2017/8/4.
 */

public class FileHelper {
    private static final String CONFIG_FILE = "config.properties";

    /**
     * 计算文件md5 用于下载判断时候安装完成
     */
    public static String getMd5ByFile(File file) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[3072];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }
//    public static String getMd5ByFile(File file) {
//        String value = null;
//        FileInputStream in = null;
//        try {
//            in = new FileInputStream(file);
//            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
//            MessageDigest md5 = MessageDigest.getInstance("MD5");
//            md5.update(byteBuffer);
//            BigInteger bi = new BigInteger(1, md5.digest());
//            value = bi.toString(16);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != in) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return value;
//    }

    /**
     * 读文件函数，读取每一行的数据，返回列表
     *
     * @param fileName
     * @return
     */
    public static ArrayList<String> readByBufferedReader(String fileName) {
        ArrayList<String> cards = new ArrayList<String>();
        try {
            File file = new File(fileName);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();
            // 读取文件，并且以utf-8的形式写出去
            BufferedReader bufread;
            String read;
            bufread = new BufferedReader(new FileReader(file));
            while ((read = bufread.readLine()) != null) {
                cards.add(read);
            }
            bufread.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cards;
    }

    /**
     * 读文件函数，读取全部数据。
     *
     * @param fileName
     * @return
     */
    public static String readFileToString(String fileName) {
        StringBuffer sb = new StringBuffer();
        try {
            File file = new File(fileName);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();
            // 读取文件，并且以utf-8的形式写出去
            BufferedReader bufread;
            String read;
            bufread = new BufferedReader(new FileReader(file));
            while ((read = bufread.readLine()) != null) {
                sb.append(read + "\r\n");
            }
            bufread.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 写文件函数，该函数覆盖掉原来的文件
     *
     * @param filePath 文件目录
     * @param list     输入列表
     */
    public static boolean writeByFileOutputStream(String filePath, ArrayList<String> list) {

        FileOutputStream fop = null;
        File file;
        try {
            file = new File(filePath);
            file.delete();
            // if file doesnt exists, then create it
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            fop = new FileOutputStream(file);
            for (int i = 0; i < list.size(); i++) {
                // get the content in bytes
                byte[] contentInBytes = (list.get(i) + "\r\n").getBytes();

                fop.write(contentInBytes);
                fop.flush();
            }
            fop.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 写文件函数，该函数覆盖掉原来的文件
     *
     * @param filePath 文件目录
     * @param content  输入内容
     */
    public static boolean writeByFileOutputStream(String filePath, String content) {

        FileOutputStream fop = null;
        File file;
        try {
            file = new File(filePath);
            file.delete();
            // if file doesnt exists, then create it
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            fop = new FileOutputStream(file);
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param key
     * @return
     */
//    public static String readProperties(String key) {
//        String result = "";
//        try {
//            InputStream fis = SmartACApplication.mAssets.open(CONFIG_FILE);
//            InputStreamReader isr=new InputStreamReader(fis,"utf8");
//            BufferedReader br=new BufferedReader(isr);
//            String read;
//            while ((read = br.readLine()) != null) {
//                if (read.indexOf("#") == 0) continue;
//                if (read.indexOf(key) > -1) {
//                    result = read.split("=")[1];
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }


}
