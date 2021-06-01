package com.xpu01;

import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import java.io.*;
import java.util.*;
import android.content.pm.PackageManager.*;

public class Japp
{
	
	public static String[] getpacka() { //取第三方已安装应用
		List packageInfos = Main.getContext().getPackageManager().getInstalledPackages(0);
		List<String> list = new ArrayList();
		for (int i = 0; i < packageInfos.size(); i++) {
			PackageInfo pInfo = (PackageInfo) packageInfos.get(i);
			if ((pInfo.applicationInfo.flags & 1) == 0) {
				list.add(pInfo.packageName);
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	
	public static String[] sygetpacka() {//取所有应用包名
		List var1 = Main.getContext().getPackageManager().getInstalledPackages(0);
		String[] var2 = new String[var1.size()];

		for(int var0 = 0; var0 < var1.size(); ++var0) {
			var2[var0] = ((PackageInfo)var1.get(var0)).packageName;
		}

		return var2;
	}
	
	
	public static Bitmap applcon(String packagename) {//取应用图标
        try {
            PackageManager pm = Main.getContext().getPackageManager();
            return drawableToBitmap(pm.getApplicationInfo(packagename, 0).loadIcon(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return  null;
        }
    }
	
	public static String appname(String packagename) {//取应用名称
        try {
            PackageManager pm = Main.getContext().getPackageManager();
            return pm.getApplicationInfo(packagename, 0).loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
	
	public static String qversion(String packagename) {//取应用版本
        try {
            return Main.getContext().getPackageManager().getPackageInfo(packagename, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0";
        }
    }
	
	public static String qsign(String packagename) { //取应用签名
        try {
            return Main.getContext().getPackageManager().getPackageInfo(packagename, 64).signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
	
	
	public static String qsignz(String packagename) {//获取 签名值
        try
		{
			PackageInfo a = Main.getContext().getPackageManager().getPackageInfo(packagename, PackageManager.GET_SIGNATURES);  //packagename 包名
			Signature[] b = a.signatures;
			Signature c = b[0];
			
			return String.valueOf(c.hashCode());
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return null;
		}
    }
	
	public static boolean whet_app(String packageName) { //判断手机是否安装某个应用
	    if(packageName == null){
			return false;
		}
        PackageManager packageManager = Main.getContext().getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (packageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }
	
	
	
	public static String qapklj(String packagename) {//获取 已知包名apk的安装路径
	    try
		{
			return Main.getContext().getPackageManager().getApplicationInfo(packagename, 0).sourceDir;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return null;
		}

	}
	
	
	public static String dataDir(String packagename) {//获取应用 data数据路径
	    try
		{
			return Main.getContext().getPackageManager().getApplicationInfo(packagename, 0).dataDir;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return null;
		}

	}
	
	
    private static Bitmap drawableToBitmap(Drawable paramDrawable) {
        int i = paramDrawable.getIntrinsicWidth();
        int j = paramDrawable.getIntrinsicHeight();
        Bitmap localBitmap = Bitmap.createBitmap(i, j, paramDrawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        paramDrawable.setBounds(0, 0, i, j);
        paramDrawable.draw(localCanvas);
        return localBitmap;
    }
	
	public static String uid(String packagename) {//获取应用 data数据路径
	
		try {
			PackageManager pm = Main.getContext().getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(packagename, 0);
			return Integer.toString(ai.uid,10);
		} catch (NameNotFoundException e) {
	    }
		return null;
	}
	
}
