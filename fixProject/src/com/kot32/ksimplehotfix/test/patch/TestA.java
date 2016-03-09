package com.kot32.ksimplehotfix.test.patch;


import android.app.Activity;
import android.widget.Toast;

public class TestA extends com.kot32.ksimplehotfix.test.TestA{
	public void showToast(Activity acticity) {
        Toast.makeText(acticity, "我是替补TestA", Toast.LENGTH_SHORT).show();
    }

}
