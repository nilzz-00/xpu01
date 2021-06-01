package com.xpu01;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import java.io.*;
import java.util.*;

import android.view.View.OnClickListener;
import android.util.*;

public class Main extends Activity 
{
	private static Main INSTANCE;
	private static String apackage = null; //包名
	private final static String noapp = "未选择应用"; //final 是不能被修改赋值
	public static String sdexsd = Environment.getExternalStorageDirectory().getPath() + "/Xpu01";
	
	Button bu,bu1,bu2,bu3;
	ImageView appIcon;
	TextView tname,tpackage,tversion,tsign,tsize,treinforce,tdatadirectory,tapkdirectory,tmd5apk,tcrc32apk,tmd5sign,tapkuid;
	LinearLayout logView;
	List<PackageInfo> apps;
	
	private SharedPreferences configs_ini;
	
	
	public Main() {  //第一个初始化
		INSTANCE = this;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		configs_ini = getSharedPreferences("configs",MODE_WORLD_READABLE); //设置为MODE_WORLD_READABLE xp才可以读
		
		appIcon = (ImageView)findViewById(R.id.aIcon);
		
		tname = (TextView)findViewById(R.id.aname);
		tpackage = (TextView)findViewById(R.id.apackage);
		tversion = (TextView)findViewById(R.id.aversion);
		tsign = (TextView)findViewById(R.id.asign);
		tapkuid = (TextView)findViewById(R.id.apkuid);
		treinforce = (TextView)findViewById(R.id.areinforce);
		tsize = (TextView)findViewById(R.id.asize);
		tmd5apk = (TextView)findViewById(R.id.md5apk);
		tcrc32apk = (TextView)findViewById(R.id.crc32apk);
		tmd5sign = (TextView)findViewById(R.id.md5sign);
		tdatadirectory = (TextView)findViewById(R.id.datadir);
		tapkdirectory =  (TextView)findViewById(R.id.apkdir);
		
		
		appIcon.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					initAppst();
					
				}});
				
		bu = (Button) findViewById( R.id.adump);  //脱壳 打开另一个应用
		//bu.setEnabled(false);//安按钮不可用
        bu.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
						
						if(Switch2bool("dump")){
							dk();

						}else if(Switch2bool("edump")){

							dk();
							
						}else{
							if(apackage != null){

                               configs_ini.edit().putString("packagename",apackage).commit();
								Intent intent = getPackageManager().getLaunchIntentForPackage(apackage); 
								startActivity(intent);
							}else{
								Toast(noapp);
							}
						}		

				}
			});
		

		bu1 = (Button) findViewById( R.id.adeta);  //跳转信息界面
		//bu1.setEnabled(false);//安按钮不可用
        bu1.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
				  if(apackage != null){
					//打开应用信息界面
					Intent mIntent = new Intent();
					mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					if (Build.VERSION.SDK_INT >= 9) {
						mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
						mIntent.setData(Uri.fromParts("package", apackage, null));//getPackageName() 是取当前应用 我替换成Package
					} else if (Build.VERSION.SDK_INT <= 8) {
						mIntent.setAction(Intent.ACTION_VIEW);
						mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
						mIntent.putExtra("com.android.settings.ApplicationPkgName", apackage);
					}
					startActivity(mIntent);
					
				  }else{
					  Toast(noapp);

				  }


				}
			});
		
			
		bu2 = (Button) findViewById( R.id.ahook);  
        bu2.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					hook_dialog();

				}
			});
			
		bu3 = (Button) findViewById( R.id.aelog);  
        bu3.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					elog_dialog();

				}
			});
			
		

		if(Switch2bool("dump")){
			bu.setText("DUMP");
		}else if(Switch2bool("edump")){
			bu.setText("DUMP");
		}
			

		create_sd();
		
		if(!isRunInXposed()){//不等于
			
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle("温馨提示");
			dlg.setMessage("您尚未激活此模块、核心Hook功能将无法使用！");
			dlg.setPositiveButton("OK",null);

			dlg.setNegativeButton("模块管理",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

						try {
							Intent intent = new Intent();
							intent.setComponent(new ComponentName("de.robv.android.xposed.installer", "de.robv.android.xposed.installer.WelcomeActivity"));
							startActivity(intent);
						} catch (Throwable th) {
							Toast("启动失败，可能未安装Xposed或者权限不足∶" + th.toString());
						}


					}});

			dlg.show();
		}
	
		
    }
	
	
	
	public static Main getContext() {
        return INSTANCE;
    }


	//创建标题菜单
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate main_menu.xml 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);



		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{


			case R.id.setup:
				
				setup_dialog();
				
				return true;
				
			case R.id.drill:    
				drill_dialog();
				return true;

			case R.id.about:

				AlertDialog.Builder dlg = new AlertDialog.Builder(this);
				dlg.setTitle("关于");
				dlg.setMessage("Xpu01（v1.2）\nby-mail:7029974@qq.com.");
				dlg.setIcon(R.drawable.about);
				dlg.setNegativeButton("关闭",null);
				
				dlg.setPositiveButton("更新",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

							Uri uri = Uri.parse("https://www.lanzous.com/b0ce6cvri");
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
							

						}});
						
				dlg.show();
				return true;


			case R.id.exit:    
				System.exit(0);
				return true;

		}
		return super.onOptionsItemSelected(item);
	}
	//菜单结束
	
	private boolean isRunInXposed(){ //判断框架有没有激活
        return false;
    }
	
	public  void dk() {
        
		if(apackage != null){

			configs_ini.edit().putString("packagename",apackage).commit();
			if(Switch2bool("cdump")){
				configs_ini.edit().putString("exp",Environment.getExternalStorageDirectory().getPath()).commit();
				Toast("DUMP输出文件路径：" + Environment.getExternalStorageDirectory().getPath() +"/ 目录下");
			}else{
				configs_ini.edit().putString("exp",Japp.dataDir(apackage)).commit();
				Toast( "DUMP输出文件路径：" + Japp.dataDir(apackage) +"/ 目录下");
			}


			Intent intent = getPackageManager().getLaunchIntentForPackage(apackage); 
			startActivity(intent);
		}else{
			Toast(noapp);
		}
		
    }
	
	
	
	private void setup_dialog() { //设置弹窗
		
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		final View gview = getLayoutInflater().inflate(R.layout.setting,null);//获取自定义对话框资源
		//获取自定义dialog下的确定和取消按钮资源
		//Button okBtnInDialog=(Button)view.findViewById(R.id.register_dialog_ok_btn);
		//Button cancelBtnInDialog=(Button)view.findViewById(R.id.register_dialog_cancel_btn);

		Switch sa = (Switch)gview.findViewById( R.id.setting_sysapp); //加载系统应用
		sa.setChecked(Switch2bool("sysapp"));//开关状态
		sa.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						configs_ini.edit().putString("sysapp","true").commit();

					} else {
						//关闭状态
						configs_ini.edit().putString("sysapp","false").commit();

					}
				}
			});
			
			
		Switch sb = (Switch)gview.findViewById( R.id.setting_checksh); //加载系统应用
		sb.setChecked(Switch2bool("checksh"));//开关状态
		sb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						configs_ini.edit().putString("checksh","true").commit();

					} else {
						//关闭状态
						configs_ini.edit().putString("checksh","false").commit();

					}
				}
			});
			
				
		Switch se = (Switch)gview.findViewById( R.id.setting_xlog); 
		se.setChecked(Switch2bool("xlog"));//开关状态
		se.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						configs_ini.edit().putString("xlog","true").commit();

					} else {
						//关闭状态
						configs_ini.edit().putString("xlog","false").commit();

					}
				}
			});
		
		Switch sf = (Switch)gview.findViewById( R.id.setting_cdump); 
		sf.setChecked(Switch2bool("cdump"));//开关状态
		sf.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						configs_ini.edit().putString("cdump","true").commit();

					} else {
						//关闭状态
						configs_ini.edit().putString("cdump","false").commit();

					}
				}
			});
		
		dlg.setTitle("设置");
		dlg.setIcon(R.drawable.settings);
		dlg.setView(gview);
		dlg.create();
		dlg.setPositiveButton("关闭",null);
		dlg.show();
		
	}
	
	
	public boolean Switch2bool(String key){ //key是表的key值
		if(configs_ini.getString(key,"").equals("true")){
			return true;
		}
	    return false;
	}
	
	
	private void drill_dialog() { //模块弹窗
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		final View gview = getLayoutInflater().inflate(R.layout.drill,null);//获取自定义对话框资源
		//获取自定义dialog下的确定和取消按钮资源
		//Button okBtnInDialog=(Button)view.findViewById(R.id.register_dialog_ok_btn);
		//Button cancelBtnInDialog=(Button)view.findViewById(R.id.register_dialog_cancel_btn);

		Button extractapk = (Button) gview.findViewById( R.id.extract_apk);  //提取apk
        extractapk.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(Japp.whet_app(apackage)){
						if(x64.copy_file(Japp.qapklj(apackage),sdexsd + "/apks/" + Japp.appname(apackage) + "_" + Japp.qversion(apackage) + ".apk")){
							Toast("提取成功：" + sdexsd + "/apks/" + Japp.appname(apackage) + "_" + Japp.qversion(apackage) + ".apk");
						}else{
							Toast("提取失败");
							File file = new File(sdexsd + "/apks"); //如果文件夹不存在则创建    
							if (!file .exists() && !file.isDirectory()){       
								file.mkdir();   //创建文件夹
								
							}
						}
					}else{
						Toast(noapp);
					}

				}
			});
		
		Button signbase = (Button) gview.findViewById( R.id.sign_base);  //复制Base64加密后的签名值
        signbase.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(Japp.whet_app(apackage)){
						try
						{
							String sign = new String(Base64.encode(Japp.qsign(apackage).getBytes("UTF-8"), 0));
							ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							manager.setText(sign);
							Toast("已复制到剪切板");
							
						}
						catch (UnsupportedEncodingException e)
						{}
					}else{
						Toast(noapp);
					}

				}
			});
		
		Button buninstall = (Button) gview.findViewById( R.id.uninstall);  //复制Base64加密后的签名值
        buninstall.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(Japp.whet_app(apackage)){
						//卸载应用程序
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DELETE);
						intent.setData(Uri.parse("package:" + apackage));
						startActivity(intent);

					}else{
						Toast(noapp);
					}

				}
			});
		
			
		Button bktest = (Button) gview.findViewById( R.id.ktest);  //复制Base64加密后的签名值
        bktest.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(Japp.whet_app(apackage)){
						ktest_dialog();

					}else{
						Toast(noapp);
					}

				}
			});
		
		
		dlg.setTitle("模块");
		dlg.setIcon(R.drawable.modules);
		dlg.setView(gview);
		dlg.create();
		dlg.setPositiveButton("关闭",null);
		dlg.show();
	
	}
	
	private void hook_dialog() { //hook弹窗
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		final View gview = getLayoutInflater().inflate(R.layout.hook,null);//获取自定义对话框资源
		//获取自定义dialog下的确定和取消按钮资源
		//Button okBtnInDialog=(Button)view.findViewById(R.id.register_dialog_ok_btn);
		//Button cancelBtnInDialog=(Button)view.findViewById(R.id.register_dialog_cancel_btn);

		final Switch es = (Switch)gview.findViewById( R.id.hook_edump); //2脱壳
		
		final Switch s = (Switch)gview.findViewById( R.id.hook_dump); //脱壳
		s.setChecked(Switch2bool("dump"));//开关状态
		s.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						if(!checkSupport()){
							s.setChecked(false);
							Toast("这台设备不支持DUMP脱壳");
						}else{
							es.setChecked(false);
							configs_ini.edit().putString("edump","false").commit();
							
							bu.setText("DUMP");
							configs_ini.edit().putString("dump","true").commit();
						}
						
					} else {
						//关闭状态
						bu.setText("OPEN");
						configs_ini.edit().putString("dump","false").commit();

					}
				}
			});
		
		
		
		es.setChecked(Switch2bool("edump"));//开关状态
		es.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						if(!checkSupport()){
							es.setChecked(false);
							Toast("这台设备不支持DUMP脱壳");
						}else{
							s.setChecked(false);
							configs_ini.edit().putString("dump","false").commit();
							
							bu.setText("DUMP");
							configs_ini.edit().putString("edump","true").commit();
						}

					} else {
						//关闭状态
						bu.setText("OPEN");
						configs_ini.edit().putString("edump","false").commit();

					}
				}
			});
		
			
		
		Switch sa = (Switch)gview.findViewById( R.id.hook_gvpn); //屏蔽抓包检测
		sa.setChecked(Switch2bool("gvpu"));//开关状态
		sa.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						configs_ini.edit().putString("gvpu","true").commit();

					} else {
						//关闭状态
						configs_ini.edit().putString("gvpu","false").commit();

					}
				}
			});


		Switch sb = (Switch)gview.findViewById( R.id.hook_dwtc); //全局定位弹窗
		sb.setChecked(Switch2bool("dwtc"));//开关状态
		sb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						//打开状态
						configs_ini.edit().putString("dwtc","true").commit();

					} else {
						//关闭状态
						configs_ini.edit().putString("dwtc","false").commit();

					}
				}
			});

		dlg.setTitle("HookTools");
		dlg.setView(gview);
		dlg.create();
		//dlg.setPositiveButton("关闭",null);
		dlg.show();

	}
	
	private void elog_dialog() { //log弹窗
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		final View gview = getLayoutInflater().inflate(R.layout.elog,null);//获取自定义对话框资源
		//获取自定义dialog下的确定和取消按钮资源
		//Button okBtnInDialog=(Button)view.findViewById(R.id.register_dialog_ok_btn);
		//Button cancelBtnInDialog=(Button)view.findViewById(R.id.register_dialog_cancel_btn);

		logView = (LinearLayout) gview.findViewById(R.id.logView);
		Xpu01_log();//添加日志


		dlg.setTitle("日志");
		dlg.setView(gview);
		dlg.create();
		dlg.setPositiveButton("关闭",null);
		
		dlg.setNegativeButton("删除",new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialogInterface, int i) {

					if(delete_log()){
						Toast("删除成功");
					} else {
						Toast("日志为空");
					}
					

				}});
				
		dlg.show();

	}
	
	
	public void create_sd() { //创建文件夹
		File file = new File(sdexsd);//如果文件夹不存在则创建    
		if (!file .exists() && !file.isDirectory()){       
			file.mkdir();   //创建文件夹
			new File(sdexsd + "/apks").mkdir(); //创建文件夹
			new File(sdexsd + "/.temp").mkdir(); //创建文件夹
		}
	}

	
	private void logView(String txt) { //添加标签显示ui打印日志
	    TextView textView = new TextView(this);
		textView.setText(txt);
		textView.setTextColor(Color.parseColor("#ffffff"));

		textView.setTextIsSelectable(true); 
		logView.addView(textView);

		}
	

	public void Xpu01_log(){  //读取日志
	    File file = new File("/sdcard/Xpu01/Xpu01.log");
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
				logView(s);
                //result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }


    }

	public boolean delete_log() { // 删除日志文件
		File file = new File("/sdcard/Xpu01/Xpu01.log");
		if (file.exists() && !file.isDirectory() && file.delete()) {
			return true;
		}
		return false;
	}
	
	private void Toast(String str) {
	    
		Toast.makeText(Main.this, str, Toast.LENGTH_SHORT).show();
		
	}
	
	private boolean checkSupport() { //判断支不支持dex
        boolean bool = true;
        try {
            Class.forName("com.android.dex.Dex");
        } catch (ClassNotFoundException classNotFoundException) {
            bool = false;
        } 
		return bool;
    }
	
	
    
	private void initAppst() {
		/*
		List<String> list = new ArrayList<String>();    
		list.add("a1");    
		list.add("a2");   
		*/

		final String[] items;

		if(Switch2bool("sysapp") == true){
			items = Japp.sygetpacka(); 
		}else{
			items = Japp.getpacka();
		}
		
		List<String> listde = Arrays.asList(items);//数组转list
		
		DialogItemAdapter adapter = new DialogItemAdapter(this ,listde); //传数据
		AlertDialog alertDialog = new AlertDialog
			.Builder(this)
			.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					apackage = items[which];

					appIcon.setImageBitmap(Japp.applcon(items[which]));

					tname.setText("应用名：" + Japp.appname(items[which]));
					tpackage.setText("包名：" +items[which]);
					tversion.setText("版本：" + Japp.qversion(items[which]));
					tsign.setText("签名值：" +Japp.qsignz(items[which]));
					tapkuid.setText("应用UID：" + Japp.uid(items[which]));
					
					if(Switch2bool("checksh")){
						treinforce.setText("加固状态：" + x64.checksh(Japp.qapklj(items[which])));
					} else {
						treinforce.setText("加固状态：没有开启识别" );
					}
					tsize.setText("安装包大小：" + x64.readableFileSize(x64.fisize(Japp.qapklj(items[which]))));
					
					tmd5apk.setText("应用MD5：" + x64.getMd5ByFile(Japp.qapklj(items[which])));
					tcrc32apk.setText("应用RCR：" + x64.tohex(Long.parseLong(x64.getcrc32(Japp.qapklj(items[which])))));
					tmd5sign.setText("MD5签名值：" + x64.strMd5(Japp.qsign(items[which])));
					tdatadirectory.setText("数据目录：" + Japp.dataDir(items[which]));
					tapkdirectory.setText("APK目录：" + Japp.qapklj(items[which]));
					
					//Toast(items[which]);
					//Toast(String.valueOf(which));
					dialog.dismiss();
				}
			}).create();
			
		alertDialog.setTitle("应用列表");
        alertDialog.show();

	}
	
	private void ktest_dialog() { //信息校验弹窗
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		final View gview = getLayoutInflater().inflate(R.layout.ktest_dialog,null);//获取自定义对话框资源
		//获取自定义dialog下的确定和取消按钮资源
		//Button okBtnInDialog=(Button)view.findViewById(R.id.register_dialog_ok_btn);
		//Button cancelBtnInDialog=(Button)view.findViewById(R.id.register_dialog_cancel_btn);

		final EditText eewjmdu=(EditText) gview.findViewById(R.id.ewjmdu);
		
		final TextView tdbx=(TextView) gview.findViewById(R.id.dbx);
		final EditText eeqmmdu=(EditText) gview.findViewById(R.id.eqmmdu);
		
		tdbx.setTextColor(gview.getResources().getColor(R.color.mediumseagreen));
		tdbx.setText("校验对象∶" + Japp.appname(apackage));
		
		Button bwjmdu = (Button) gview.findViewById( R.id.wjmdu);  //复制Base64加密后的签名值
        bwjmdu.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(Japp.whet_app(apackage)){
						
						if(eewjmdu.getText().toString().length()!=32){
							Toast("文件MD5值填写错误");
							return;//结束该方法
						}
						
						if(x64.getMd5ByFile(Japp.qapklj(apackage)).equals(eewjmdu.getText().toString())){
							tdbx.setTextColor(gview.getResources().getColor(R.color.mediumseagreen));
							tdbx.setText("文件MD5值匹配");
						}else{
							tdbx.setTextColor(gview.getResources().getColor(R.color.deeppink));
							tdbx.setText("文件MD5值不匹配");
						}

					}else{
						Toast(noapp);
					}

				}
			});
			
			
		Button bqmmdu = (Button) gview.findViewById( R.id.qmmdu);  //复制Base64加密后的签名值
        bqmmdu.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(Japp.whet_app(apackage)){

						if(eeqmmdu.getText().toString().length()!=32){
							Toast("MD5签名值填写错误");
							return;//结束该方法
						}

						if(x64.strMd5(Japp.qsign((apackage))).equals(eeqmmdu.getText().toString())){
							tdbx.setTextColor(gview.getResources().getColor(R.color.mediumseagreen));
							tdbx.setText("MD5签名值匹配");
						}else{
							tdbx.setTextColor(gview.getResources().getColor(R.color.deeppink));
							tdbx.setText("MD5签名值不匹配");
						}

					}else{
						Toast(noapp);
					}

				}
			});
		
	
		dlg.setTitle("信息校验");
		dlg.setView(gview);
		dlg.create();
		dlg.setPositiveButton("关闭",null);

		dlg.show();

	}
	
	
	
}


