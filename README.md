# xpu01
基于Xposed框架安卓脱壳软件
#### 实现与使用
通过反射调用getDex方法取得com.android.dex.Dex类（获取dex），不支持8.0系统以上版本，高系统可以配合虚拟机来使用。


#### 关于类抽取（nop）还原填充代码实现
请看nop.java
