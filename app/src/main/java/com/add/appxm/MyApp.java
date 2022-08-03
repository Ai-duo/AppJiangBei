package com.add.appxm;

import android.app.Application;
import android.content.Intent;
import android.os.Process;

import androidx.annotation.NonNull;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                throwable.printStackTrace();
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                restartApp();
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(handler);
      // startActivity();
    }

    Thread.UncaughtExceptionHandler handler;

    public void restartApp() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity();
        android.os.Process.killProcess(Process.myPid());
        System.exit(1);
    }


    public void startActivity() {
        Intent activity = new Intent(getApplicationContext(), MainActivity.class);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activity);
    }
}
