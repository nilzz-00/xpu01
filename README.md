# xpu01
基于Xposed框架安卓脱壳软件
#### 实现与使用
通过反射调用getDex方法取得com.android.dex.Dex类（获取dex），不支持8.0系统以上版本，高系统可以配合虚拟机来使用。


####关于类抽取（nop）还原填充代码实现

private String Ldex(Context mContext,String stord)throws Throwable{//脱壳 保存目录, 脱多个dex 带遍历类 nop 回填
		String[] strArr;
		Log("遍历dump 加载Dex数组...");
		ArrayList arrayList = new ArrayList();
		ClassLoader classLoader = mContext.getClassLoader();
		Field declaredField = classLoader.getClass().getSuperclass().getDeclaredField("pathList");
		declaredField.setAccessible(true);
		Object[] objArr2 = (Object[]) fsd(declaredField.get(classLoader), "dexElements");
		//publishProgress(objArr2);
		Method declaredMethod3 = classLoader.getClass().getSuperclass().getSuperclass().getDeclaredMethod("findClass", new Class[]{String.class});
		declaredMethod3.setAccessible(true);
		for (Object obj : objArr2) {
			Object d = fsd(obj, "dexFile");
			if (d != null) {
				Log("遍历类...");
				Method declaredMethod4 = d.getClass().getDeclaredMethod("entries", new Class[0]);
				if (!(declaredMethod4.invoke(d, new Object[0]) == null || (strArr = (String[]) fsd(declaredMethod4.invoke(d, new Object[0]), "mNameList")) == null || strArr.length == 0)) {
					Log("提取Dex");
					for (String str4 : strArr) {
						try {
							Class cls2 = (Class) declaredMethod3.invoke(classLoader, new Object[]{str4});
							Object invoke2 = cls2.getClass().getDeclaredMethod("getDex", new Class[0]).invoke(cls2, new Object[0]);
							if (!arrayList.contains(invoke2)) {
								arrayList.add(invoke2);
							}
						} catch (Throwable th2) {
						}
					}
					continue;
				}
			}
		}
		if (arrayList.size() == 0) {
			return "找不到Dex！";
		}
		File file = new File(stord);
		file.mkdirs();
		int i = 1;
		Iterator it = arrayList.iterator();
		while (true) {
			int i2 = i;
			if (it.hasNext()) {
				Object next = it.next();
				byte[] bArr2 = (byte[]) next.getClass().getDeclaredMethod("getBytes", new Class[0]).invoke(next, new Object[0]);
				RandomAccessFile randomAccessFile2 = new RandomAccessFile(new File(file, "Dexdump" + (i2 == 1 ? "" : Integer.valueOf(i2)) + ".dex"), "rw");
				
				randomAccessFile2.write(bArr2);
				randomAccessFile2.close();
				i = i2 + 1;
			} else {
				return "脱壳成功，共写出 " + (i2 - 1) + " 个dex，文件夹位于 "+ stord;
			}
		}

	}
  
  
