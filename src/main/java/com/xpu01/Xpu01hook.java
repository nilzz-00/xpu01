package com.xpu01;

import android.app.*;
import android.content.pm.*;
import android.widget.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class Xpu01hook implements IXposedHookLoadPackage
{
	XSharedPreferences pini; //XSharedPreferences类去加载指定路径下得xml文件来获取返回数据

	Method c = null; //getDex
	Class p;
	Method o;



	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam p1)
	{

		if(p1.packageName.equals("com.xpu01")){//判断包名是否存在
		    XposedHelpers.findAndHookMethod("com.xpu01.Main",p1.classLoader, "isRunInXposed", new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param) throws Throwable { //替换方法

						return new Boolean(true);
					}
				});
		}
		
		pini = new XSharedPreferences("com.xpu01","configs"); //包名 文件名
		pini.reload(); //初始化加载数据

		dwtc(p1.packageName);//定位弹窗
		
		if(p1.packageName.equals(pini.getString("packagename",null))){//判断包名是否存在
			dump(p1);//脱壳
			edump();//脱壳2
			vpn(); //屏蔽VPN
			//dwtc(p1.packageName);//定位弹窗
	    }else{
			//XposedBridge.log("ydll_No package name");//包名不存在
			return;//返回
		}



	}



	private void vpn()//hook vpn方法
	{
		if(!pini.getString("gvpu","").equals("true"))  //开关 判断是否开启 ！取反 等于 就是不等于   真等于
			return;


		try {
			XposedHelpers.findAndHookMethod(Class.forName("java.net.NetworkInterface"), //类名
                "getName", // hook方法名
                new XC_MethodHook() {

					@Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable { //方法前执行
                        super.beforeHookedMethod(param);

                        //这样设置函数的返回值
                        param.setResult("rmnet_data1");
                    }
				});

		} catch (Throwable e) {
			throw new NoClassDefFoundError(e.getMessage());
		}

	}





	private void dwtc(final String pack)//hook 定位弹窗方法
	{
		if(!pini.getString("dwtc","").equals("true"))  //开关 判断是否开启 ！取反 等于 就是不等于   真等于
			return;


		try {
			XposedHelpers.findAndHookMethod(Dialog.class, //类名
                "show", // hook方法名
                new XC_MethodHook() {

					@Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable { //方法前执行
                        printStack(pack, "Dialog");
                    }
				});

		} catch (Throwable e) {
			throw new NoClassDefFoundError(e.getMessage());
		}



		try {
			XposedHelpers.findAndHookMethod(Toast.class, //类名
                "show", // hook方法名
                new XC_MethodHook() {

					@Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable { //方法前执行
                        printStack(pack, "show");
                    }
				});

		} catch (Throwable e) {
			throw new NoClassDefFoundError(e.getMessage());
		}


	}



	private void printStack(String pack, String name)
	{
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append(">>>>[Xpu01弹窗定位]> ");
		localStringBuilder.append(pack);
		localStringBuilder.append(" -> ");
		localStringBuilder.append(name);
		localStringBuilder.append(".show()");
		Xpu01_log(localStringBuilder.toString());

		boolean canPrint = false;
		for (StackTraceElement element : new Exception().getStackTrace()) {
			if (!canPrint) {
				if (element.getMethodName().equals("show")) {
					canPrint = true;
				} else {
				}
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(">  ");
			stringBuilder.append(element);
			Xpu01_log(stringBuilder.toString());

		}
		Xpu01_log("<<<<<<<<<[结束]<");



	}



	private void dump(final XC_LoadPackage.LoadPackageParam pa){//hook dump脱壳

	    if(!pini.getString("dump","").equals("true"))  //总开关 判断是否开启 ！取反 等于 就是不等于   真等于
			return;


	    dump_i();

	    de.robv.android.xposed.XposedHelpers.findAndHookMethod( "java.lang.ClassLoader" , pa.classLoader,"loadClass" ,String.class ,boolean.class,

			new XC_MethodHook () 
			{

				@Override
				protected void afterHookedMethod(XC_MethodHook.MethodHookParam param)  //执行方法后执行
				{



					//XposedBridge.log( " after hook : "  ) ;
					boolean isSystemClass=true;
					Class cls =(Class)param.getResult();
					if(cls==null)return;


					try
					{
						Class.forName(cls.getName(),false,Xpu01hook.class.getClassLoader().getSystemClassLoader());
						//.loadClass();
					}
					catch (ClassNotFoundException e)
					{

						isSystemClass=false;}

					if(isSystemClass)
						return;



					try
					{
						Object dex = c.invoke(cls);



						byte[] data = (byte[])o.invoke(dex) ;


						if(data!= null )
						{

							File f=new File( pini.getString("exp","/sdcard"),  "Xpu01_dump" +Integer.toHexString(data.length)+ ".dex" ); //Integer.toHexString 整数转到十六进制
							if(!f.exists())
							{
								dump_b( data,f.getAbsolutePath());
								//XposedBridge.log(dex.toString());
							}
						}
					}
					catch (Exception e)
					{
						XposedBridge.log(e.toString());
					}
				}




			}

		);

	}

	public void dump_i()//initRefect
	{
		try
		{
			p = Class.forName("com.android.dex.Dex");

			o = p.getDeclaredMethod("getBytes");

			c = Class.class.getDeclaredMethod("getDex" );
		}
		catch ( Exception e)
		{

			XposedBridge.log(e.toString());

		}
	}


	public void dump_b(byte [] data, String path )//写数据 Byte

	{

		try
		{
			OutputStream os = new FileOutputStream(path);
			os.write( data);
			os.close();

            //Xpu01hook.Toast(Xpu01hook.this,"执行DUMP脱壳成功");
		}
		catch (Exception e)
		{
			//Xpu01hook.Toast(Xpu01hook.this,"执行DUMP脱壳失败" + e.toString());
		}

	}
	
	
	private void edump()//第二脱壳
	{
		if(!pini.getString("edump","").equals("true"))  //开关 判断是否开启 ！取反 等于 就是不等于   真等于
			return;
		
		try
		{
			final Class<?> g = Class.forName("android.os.Bundle");

			Class<?> cls2 = Class.forName("android.app.Activity");
			XposedHelpers.findAndHookMethod(cls2, "onCreate",g, new XC_MethodHook() {

					@Override
					protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramMethodHookParam) {//方法后执行
						
						hack2Dex(paramMethodHookParam.thisObject.getClass());
						//Xpu01hook.Toast(Xpu01hook.this,"执行成功");
					}
				});

		}
		catch (ClassNotFoundException e)
		{}
	}
	
	
	private void hack2Dex(Class<?> cls) {
        try {
            byte[] bArr = (byte[]) Class.forName("com.android.dex.Dex").getDeclaredMethod("getBytes", new Class[0]).invoke(Class.forName("java.lang.Class").getMethod("getDex", new Class[0]).invoke(cls, new Object[0]), new Object[0]);
            File file = new File(pini.getString("exp","/sdcard"),  "Xpu01_2dump" +Integer.toHexString(bArr.length)+ ".dex" );
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bArr);
            fileOutputStream.flush();
            fileOutputStream.close();
			//Xpu01hook.Toast(Xpu01hook.this,"执行2DUMP脱壳成功");
        } catch (Exception e) {
            //Toast.makeText(context, e.toString(), 0).show();
			//Xpu01hook.Toast(Xpu01hook.this,"执行2DUMP脱壳失败" + e.toString());
        }
    }
	

	private void Xpu01_log(String s) {
		if(pini.getString("xlog","").equals("true")){ //开关 判断是否开启打印到xp框架日志
			XposedBridge.log(s);
	    }else{

			File file = new File("/sdcard/Xpu01/Xpu01.log");
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(file,true));
				bw.write("I: " +s  + "\n");
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


}

