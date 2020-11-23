package com.leo.gradle;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mcxiaoke.packer.helper.PackerNg;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMetaData();
            }
        });
    }

//    @NewTag(value = "888")
private void getMetaData() {
        PackerNg.getChannel(this);
    PackageManager pm = getPackageManager();
    List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
    for (ApplicationInfo app : apps) {
        if (app.packageName.startsWith("com.douban.")) {
            Log.d("TAG", "app=" + app.packageName + ", channel="
                    + PackerNg.getChannel(new File(app.sourceDir)));
        }
    }

    try {
        ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                PackageManager.GET_META_DATA);
        String value = appInfo.metaData.getString("leo");
        ((TextView) findViewById(R.id.text)).setText(value);
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
}
}
