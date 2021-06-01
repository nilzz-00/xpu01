package com.xpu01;

import java.io.*;
import java.math.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.*;
import java.text.*;
import java.util.zip.*;

public class x64
{
	

    public static boolean copy_file(String sourcePath, String targetPath) {//复制文件  文件路径 保存后的路径文件名
        if (!new File(sourcePath).exists()) {
            return false;
        }
        int bytesum = 0;
        try {
            InputStream inStream = new FileInputStream(sourcePath);
            FileOutputStream fs = new FileOutputStream(targetPath);
            byte[] buffer = new byte[1444];
            while (true) {
                int byteread = inStream.read(buffer);
                if (byteread != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                } else {
                    inStream.close();
                    fs.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public static String readableFileSize(long size) { //文件大小字节转换
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
	
	
	
	public static long fisize(String filePath) {//取文件大小尺寸
        File file = new File(filePath);
        try {
            if (file.isDirectory()) {
                return getFileSizes(file);
            }
            return getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
	
	private static long getFileSize(File file) throws Exception {
        if (file.exists()) {
            return (long) new FileInputStream(file).available();
        }
        file.createNewFile();
        return 0;
    }

    private static long getFileSizes(File f) throws Exception {
        long fileSize;
        long size = 0;
        File[] flist = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                fileSize = getFileSizes(flist[i]);
            } else {
                fileSize = getFileSize(flist[i]);
            }
            size += fileSize;
        }
        return size;
    }
	

	
	public static String checksh(String sourcePath) { //查壳 文件路径
		String dat= drtext(sourcePath);
		
		String nm = "360加固|腾讯加固|爱加密|百度加固|阿里加固|几维安全加固|通付盾加固|梆梆加固|";
		String[] identify_nm = nm.split("\\|");// 分割文本
		String tz = "libjiagu.so|libshell.so|libexec.so|libbaiduprotect.so|libmobisecx.so|libkwscmm.so|libegis.so|libsecexe.so|";
		String[] identify_tz = tz.split("\\|");// 分割文本
		
		for (int i = 0; i <= 7; i++) {
			if(dat.indexOf(identify_tz[i]) != -1){ //indexOf 由指定位置从前向后查找指定字符串的位置，如果查找到了则返回（第一个字母）位置索引，如果找不到返回-1.
				return identify_nm[i];

			}
		}
		return "未发现加固";
	}
	
	
	private static String drtext(String filename) { //读入文本文件 返回内容
		Exception e;
		String res = "";
		if (!new File(filename).exists()) {
			return res;
		}
		try {
			FileInputStream fin = new FileInputStream(filename);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			String res2 = new String(buffer, 0, length, "utf8");
			try {
				fin.close();
				return res2;
			} catch (Exception e2) {
				e = e2;
				res = res2;
				e.printStackTrace();
				return res;
			}
		} catch (Exception e3) {
			e = e3;
			e.printStackTrace();
			return res;
		}
	}

	
	
	
	public static String getMd5ByFile(String filePath) { //获取文件md5值 文件路径
		String value = null;
		FileInputStream in = null;
		File file = new File(filePath);
		try {
			in = new FileInputStream(file);
		    MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		    MessageDigest md5 = MessageDigest.getInstance("MD5");
		    md5.update(byteBuffer);
		    BigInteger bi = new BigInteger(1, md5.digest());
		    value = bi.toString(16);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
			if(null != in) {
				try {
			        in.close();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
		    }
		}
		return value;
	}
	
	public static String getcrc32(String filename) { //获取文件crc32值
	    File file = new File(filename);
    	CRC32 crc32 = new CRC32();  
    	//MessageDigest.get
        FileInputStream fileInputStream = null;
        try {
			fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
            	crc32.update(buffer,0, length);
            }
            return crc32.getValue()+"";
			
			
        } catch (FileNotFoundException e) {
			e.printStackTrace();
            return null;
        } catch (IOException e) {
			e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null)
					fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public static String tohex(long j) { //转到十六进制
		try {
			String toHexString = Long.toHexString(j);
			return toHexString.length() < 2 ? "0" + toHexString : toHexString;
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String strMd5(String str) { //字符串获取md5
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)

                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

	
	
	
}
