package cp.com.accessibilityfunction;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class MainActivity extends Activity {
    private Button btnClick;
    private Button btnAccDu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClick = (Button)findViewById(R.id.btn_click);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startAppWithPackageName(getApplicationContext(), "com.huawei.systemmanager");
                Log.i("MainActivity", "点击按钮");
                if (serviceIsRunning()) {
                    Toast.makeText(getApplicationContext(), "服务已经开启", Toast.LENGTH_SHORT).show();
                    Log.i(AppUtil.TAG, "服务已经开启");
                } else {
                    startAccessibilityService();
                }
            }
        });
        btnAccDu = (Button)findViewById(R.id.btn_acc_apk);
        btnAccDu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppUtil.startAppWithPackageName(getApplicationContext(), "cn.opda.a.phonoalbumshoushou");
//                AppUtil.startAppWithPackageName(getApplicationContext(), "com.tencent.qqpimsecure");
//                AppUtil.startAppWithPackageName(getApplicationContext(), "com.qihoo360.mobilesafe");

                //首先检测是否已经安装了需要检测的apk,如果安装过，直接卸载
//                ArrayList<String> nameList = AppUtil.getSingleInstance().getPackageNameList(getApplicationContext(), true);
//                String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                String common = "/system/bin/screencap -p " + "/sdcard/app_shadu_pic/sdhfjh" + "_"+ "fffffsgffff.jpg";
////                String common = "/system/bin/screencap -p " + rootPath + "/app_shadu_pic/sdhfjh" + "_"+ "fffffsgffff.jpg";
//                Log.i(AppUtil.TAG, "AAAAAAAAAAAA " + common);com.tencent.qqpimsecure_UQMinSdkBPreInstallNoNative#晓琪#酷跑飞车(HZHC_05).apk.jpg
//                AppUtil.screenShots("/system/bin/screencap -p /sdcard/app_shadu_pic/com.tenQMicure_UQMfhdg\\(sst.apk.jpg");

                AppUtil.finishAppWithPackageName(getApplicationContext(), AppUtil.safe360);
                AppUtil.finishAppWithPackageName(getApplicationContext(), AppUtil.safeBaiDu);
                AppUtil.finishAppWithPackageName(getApplicationContext(), AppUtil.safeTencent);

                String name = AppUtil.getSingleInstance().getPackageNameList(getApplicationContext(), true).get(0);
                HashMap<String, String> nameMap = AppUtil.getSingleInstance().getPackageNamePathMap(getApplicationContext());
                String path = nameMap.get(name);
                AppUtil.getSingleInstance().currentSafe = AppUtil.safe360;
                AppUtil.getSingleInstance().softCount = 0;
                AppUtil.getSingleInstance().currentPackageName = name;
                AppUtil.installAppWithPath(getApplicationContext(), path);

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Log.i(AppUtil.TAG, "发送邮件");
//                            EmailSender sender = new EmailSender();
//                            //设置服务器地址和端口，网上搜的到
//                            sender.setProperties("smtp.ym.163.com", "25");
//                            //分别设置发件人，邮件标题和文本内容
//                            String messageInfo = "测试用的<br>caca擦擦";
//
//                            sender.setMessage("peng.chen@joyreach.com", "报毒自动检测结果", messageInfo);
//                            //设置收件人
//                            sender.setReceiver(new String[]{"554131215@qq.com"});
//                            //添加附件
//                            //这个附件的路径是我手机里的啊，要发你得换成你手机里正确的路径
////                            File file = new File("/sdcard/app_shadu_pic/");
////                            File[] fileList = file.listFiles();
////                            for (int i = 0; i < fileList.length; i++) {
////                                sender.addAttachment(fileList[i].getAbsolutePath());
////                            }
////                    sender.addAttachment("/sdcard/app_shadu_pic/提醒.txt");
////                    sender.addAttachment("/sdcard/app_shadu_pic/自动检测报毒.txt");
//                            //发送邮件
//                            sender.sendEmail("smtp.ym.163.com", "peng.chen@joyreach.com", "111111");
//                            //<span style="font-family: Arial, Helvetica, sans-serif;">sender.setMessage("你的163邮箱账号", "EmailS//ender", "Java Mail ！");这里面两个邮箱账号要一致</span>
//
//                        } catch (AddressException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (MessagingException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
            }
        });
    }

    /**
     * 判断自己的应用的AccessibilityService是否在运行
     *
     * @return
     */
    private boolean serviceIsRunning() {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getClassName().equals(getPackageName() + ".AccessibilityFun")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 前往设置界面开启服务
     */
    private void startAccessibilityService() {
        new AlertDialog.Builder(this)
                .setTitle("开启辅助功能")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("使用此项功能需要您开启辅助功能")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 隐式调用系统设置界面
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }


}
