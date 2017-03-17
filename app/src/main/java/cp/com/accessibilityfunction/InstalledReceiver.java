package cp.com.accessibilityfunction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PengChen on 2017/3/8.
 */

public class InstalledReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null){
            return;
        }
        String extraInfo = intent.getStringExtra("extraInfo");
        Log.i(AppUtil.TAG, "intent.getAction = " + intent.getAction() + " package = " + intent.getDataString());
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
            Log.i(AppUtil.TAG, "安装：" + intent.getDataString());//package:cn.zsb.apps
            String installPackageName = intent.getDataString();
            if (installPackageName.contains("com.qihoo360.mobilesafe")
                    || installPackageName.contains("com.tencent.qqpimsecure")
                    || installPackageName.contains("cn.opda.a.phonoalbumshoushou")) {

            } else {
                //安装完成之后开启杀毒软件检测
                String packageName = intent.getDataString().substring(8);
                if (AppUtil.getSingleInstance().containPackageName(packageName)) {
                    AppUtil.getSingleInstance().currentPackageName = packageName;
                    AppUtil.getSingleInstance().softCount = 0;
                    AppUtil.backToHome(context);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(AppUtil.TAG, "开启杀毒检测");
                            AppUtil.startAppWithPackageName(context, AppUtil.getSingleInstance().currentSafe);
                        }
                    }, 1000);
                }
            }
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getDataString().substring(8);
            if (AppUtil.getSingleInstance().containPackageName(packageName)) {
                Log.i(AppUtil.TAG, "关闭杀毒检测, 回到桌面");
//                AppUtil.finishAppWithPackageName(context, AppUtil.getSingleInstance().currentSafe);
//                AppUtil.backToHome(context);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String packageName = intent.getDataString().substring(8);
                        HashMap<String, String> mapApk = AppUtil.getSingleInstance().getPackageNamePathMap(context);
                        Log.i(AppUtil.TAG, "卸载一个被检测的apk packageName = " + packageName + "path = " + mapApk.get(packageName));
                        AppUtil.getSingleInstance().removePackageByName(packageName);
                        InstallOneTestApk(context);
                    }
                }, 1000);
            }
        }
    }

    private void InstallOneTestApk(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> packageNamePathMap = AppUtil.getSingleInstance().getPackageNamePathMap(context);
                ArrayList<String> packageNameList = AppUtil.getSingleInstance().getPackageNameList(context, false);
                if (!packageNameList.isEmpty()) {
                    String packageName = packageNameList.get(0);
                    String path = packageNamePathMap.get(packageName);
                    Log.i(AppUtil.TAG, "InstallOneTestApk path = " + path);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                } else {
                    //检测完成，收集信息，整理输出内容
                    if (AppUtil.safe360.equals(AppUtil.getSingleInstance().currentSafe)) {
                        //360检测结束之后，腾讯检测
                        startNextSafe(context, AppUtil.safeTencent);
                    } else if (AppUtil.safeTencent.equals(AppUtil.getSingleInstance().currentSafe)) {
                        //腾讯检测结束之后，百度检测
                        startNextSafe(context, AppUtil.safeBaiDu);
                    } else if (AppUtil.safeBaiDu.equals(AppUtil.getSingleInstance().currentSafe)) {
                        //腾讯检测结束之后，发送邮件
                        AppUtil.getSingleInstance().startSendEmail();
                    }
                }
            }
        }, 800);
    }

    private void startNextSafe(Context context, String nextSafe) {
        AppUtil.getSingleInstance().currentSafe = nextSafe;
        HashMap<String, String> packageNamePathMap = AppUtil.getSingleInstance().getPackageNamePathMap(context);
        ArrayList<String> packageNameList = AppUtil.getSingleInstance().getPackageNameList(context, true);
        if (!packageNameList.isEmpty()) {
            String packageName = packageNameList.get(0);
            String path = packageNamePathMap.get(packageName);
            Log.i(AppUtil.TAG, "next safe InstallOneTestApk path = " + path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        }
    }
}
