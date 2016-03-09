# KSimpleHotFix
A Simple HotFix Library on Android

### The is a library to learn hotfix and dynamicload,Not a mature library. Just for learn and myself use.

ScreenCapture:
![ScreenCapture](http://image17-c.poco.cn/mypoco/myphoto/20160309/16/17425403720160309165206025.gif)

### Attention:Not available below Android 5.0,still exist some bug.
### How To Use
#### Host Project:

Add this code in your Application's onCreate().

```

FixManager.getInstance().init(new FixManager.FixConfig("http://xxx.xx.xx/patch.apk", null), getApplicationContext());

```

##### For normal class:

```

TestA a=(TestA)FixManager.getInstance().getLatestClassInstance(TestA.class);

```

It will return the latest class you fixed.

##### For Activity:

Nothing to do,just use Intent as you used before.

### Fixed Project:
##### For normal class:

For example,you want to fix your class:TestA.class,you should define it with same package in your host project.and new a other package call
'patch',then put the fixed java file in it,also named 'TestA',and extends the old 'TestA'.

like this:
![ScreenCapture](http://image17-c.poco.cn/mypoco/myphoto/20160309/17/1742540372016030917053602.png)

##### For Activity:

Little trouble is if you didn't add the assetPath in host app,you can't use Resource file.so,like This:
[ActivityA.java](https://github.com/kot32go/KSimpleHotFix/blob/master/fixProject%2Fsrc%2Fcom%2Fkot32%2Fksimplehotfix%2Factivity%2FActivityA.java)

Ok,then make your fixed app be APK,then upload to your server ,and host app can get the latest class.

The idea of this library is [Here](http://blog.csdn.net/kot32go/article/details/50821808)



