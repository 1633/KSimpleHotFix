package com.kot32.ksimplehotfix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kot32.ksimplehotfix.activity.ActivityA;
import com.kot32.ksimplehotfix.test.TestA;
import com.kot32.ksimplehotfixlibrary.manager.FixManager;

public class MainActivity extends AppCompatActivity {

    private Button testA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        testA = (Button) findViewById(R.id.button);
        testA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ActivityA.class);
                startActivity(intent);
            }
        });
        testA.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("原来的TestA是由:",TestA.class.getClassLoader().toString());
                TestA a=(TestA)FixManager.getInstance().getLatestClassInstance(TestA.class);
                a.showToast(MainActivity.this);
                return false;
            }
        });
    }

}
