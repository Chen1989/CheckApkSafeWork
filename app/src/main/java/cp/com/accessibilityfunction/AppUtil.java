package cp.com.accessibilityfunction;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

/**
 * Created by PengChen on 2017/3/9.
 */

public class AppUtil {
    public static final String TAG = "TTTTTTTT";
    public static final String safe360 = "com.qihoo360.mobilesafe";
    public static final String safeTencent = "com.tencent.qqpimsecure";
    public static final String safeBaiDu = "cn.opda.a.phonoalbumshoushou";
    public static final String EXTRA_INFO_PRE = "extraInfoPre";
    public static final String EXTRA_INFO_MID = "extraInfoMid";
    public static final String EXTRA_INFO_END = "extraInfoEnd";
    public static final int safeCount = 3;

    public String currentSafe = null;
    public String currentPackageName = null;
    public int softCount = 0;

    private HashMap<String, String> packageNamePathMap = null;
    private ArrayList<String> packageNameList = null;

    private HashSet<String> soft360ApkInfo = null;
    private HashSet<String> softTencentApkInfo = null;
    private HashSet<String> softBaiDuApkInfo = null;

    private static AppUtil singleInstance = null;
    private AppUtil(){}

    public static AppUtil getSingleInstance() {
        if (singleInstance == null) {
            synchronized (AppUtil.class) {
                if (singleInstance == null) {
                    singleInstance = new AppUtil();
                }
            }
        }
        return singleInstance;
    }

    //重启某个应用
    public static void startAppWithPackageName(Context context, String packageName){
        PackageManager manager = context.getPackageManager();
        manager.setApplicationEnabledSetting(packageName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Intent intent = manager.getLaunchIntentForPackage(packageName);
        if (intent != null){
            context.startActivity(intent);
        } else {
            Log.i("TAG", "包名错误，没有安装该应用");
        }
    }

    //停用某个应用
    public static void finishAppWithPackageName(Context context, String packageName){
        PackageManager manager = context.getPackageManager();
        manager.setApplicationEnabledSetting(packageName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
    }

    /**
     * 判断自己的应用的AccessibilityService是否在运行
     *
     * @return
     */
    public static boolean serviceIsRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getClassName().equals(context.getPackageName() + ".AccessibilityFun")) {
                return true;
            }
        }
        return false;
    }

    //通过路径安装一个app
    public static void installAppWithPath(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.i(TAG, "startInstallTestApk path = " + apkPath);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //通过包名卸载一个app
    public static void uninstallAppWithPackageName(Context context, String packageName, String extraInfo) {
        if (packageName == null || packageName.isEmpty()) {
            return;
        }
        PackageManager manager = context.getPackageManager();
        try {
            manager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("extraInfo", extraInfo);
        context.startActivity(intent);
    }

    //截图
    public static void  screenShots(String command)
    {
        try {

            Process process = Runtime.getRuntime().exec("su\n");
            process.getOutputStream().write((command + "\n").getBytes());
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            process.waitFor();
            Log.i(TAG, "截图完成*********");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //回到桌面
    public static void backToHome(Context context) {
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);
    }

    public static Boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    //被检测的包，路径
    public HashMap<String, String> getPackageNamePathMap(Context context) {
        if (packageNamePathMap == null ){
            packageNamePathMap = new HashMap<String, String>();
            File[] apkPaths = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/app_shadu_apk").listFiles();
            for (int i = 0; i < apkPaths.length; i++) {
                String apkPath = apkPaths[i].getAbsolutePath();
                PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
                if (packageInfo != null){
                    ApplicationInfo info = packageInfo.applicationInfo;
                    String packageName = info.packageName;
//                    Log.i(TAG, "packageName = " + packageName);
                    packageNamePathMap.put(packageName, apkPath);
                } else {
                    Log.i(TAG, "文件不存在 " );
                }
            }
        }
        return packageNamePathMap;
    }

    public ArrayList<String> getPackageNameList(Context context, Boolean isRepeat) {
        if (isRepeat) {
            packageNameList = new ArrayList<String>();
            File[] apkPaths = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/app_shadu_apk").listFiles();
            for (int i = 0; i < apkPaths.length; i++) {
                String apkPath = apkPaths[i].getAbsolutePath();
                PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
                if (packageInfo != null){
                    ApplicationInfo info = packageInfo.applicationInfo;
                    String packageName = info.packageName;
//                    Log.i(TAG, "packageName = " + packageName);
                    packageNameList.add(packageName);
                } else {
                    Log.i(TAG, "文件不存在 " );
                }
            }
        } else {
            if (packageNameList == null) {
                packageNameList = new ArrayList<String>();
                File[] apkPaths = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/app_shadu_apk").listFiles();
                for (int i = 0; i < apkPaths.length; i++) {
                    String apkPath = apkPaths[i].getAbsolutePath();
                    PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
                    if (packageInfo != null){
                        ApplicationInfo info = packageInfo.applicationInfo;
                        String packageName = info.packageName;
                        Log.i(TAG, "packageName = " + packageName);
                        packageNameList.add(packageName);
                    } else {
                        Log.i(TAG, "文件不存在 " );
                    }
                }
            }
        }
        return packageNameList;
    }

    public void removePackageByName(String packageName) {
        if (packageNameList != null && !packageNameList.isEmpty()) {
            packageNameList.remove(packageName);
        }
    }

    public Boolean containPackageName(String packageName) {
        if (packageNameList != null) {
            return packageNameList.contains(packageName);
        }
        return false;
    }

    public void addSoftBaoDuApkInfo(String softSafe, String apkFileName) {
        if (softSafe == AppUtil.safe360) {
            if (soft360ApkInfo == null) {
                soft360ApkInfo = new HashSet<String>();
            }
            soft360ApkInfo.add(apkFileName);
        }
        if (softSafe == AppUtil.safeTencent) {
            if (softTencentApkInfo == null) {
                softTencentApkInfo = new HashSet<String>();
            }
            softTencentApkInfo.add(apkFileName);
        }
        if (softSafe == AppUtil.safeBaiDu) {
            if (softBaiDuApkInfo == null) {
                softBaiDuApkInfo = new HashSet<String>();
            }
            softBaiDuApkInfo.add(apkFileName);
        }
    }

    public void clearSoftBaoDuApkInfo() {
        soft360ApkInfo.clear();
        softTencentApkInfo.clear();
        softBaiDuApkInfo.clear();
    }

    public void startSendEmail(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailSender sender = new EmailSender();
                    //设置服务器地址和端口，网上搜的到
                    sender.setProperties("smtp.ym.163.com", "25");
                    //分别设置发件人，邮件标题和文本内容
                    String messageInfo = getResultInfo();

                    sender.setMessage("peng.chen@joyreach.com", "报毒自动检测结果", messageInfo);
                    //设置收件人
                    sender.setReceiver(new String[]{"554131215@qq.com","haijun.tang@joyreach.com","tao.liu@joyreach.com",
                    "xiaoqi.wang@joyreach.com", "xiaoyu.zhou@joyreach.com", "shuo.wang@joyreach.com",
                            "min.yu@joyreach.com","miaowei.su@joyreach.com","yunhai.zhan@joyreach.com"});
                    //添加附件
                    //这个附件的路径是我手机里的啊，要发你得换成你手机里正确的路径
                    File file = new File("/sdcard/app_shadu_pic/");
                    File[] fileList = file.listFiles();
                    for (int i = 0; i < fileList.length; i++) {
                        sender.addAttachment(fileList[i].getAbsolutePath());
                    }
                    //发送邮件
                    sender.sendEmail("smtp.ym.163.com", "peng.chen@joyreach.com", "111111");
                    //<span style="font-family: Arial, Helvetica, sans-serif;">sender.setMessage("你的163邮箱账号", "EmailS//ender", "Java Mail ！");这里面两个邮箱账号要一致</span>

                } catch (AddressException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getResultInfo() {
        String result = "";
        if (soft360ApkInfo != null && !soft360ApkInfo.isEmpty()) {
            result = result + "360报毒：<br>";
            Iterator<String> it = soft360ApkInfo.iterator();
            while (it.hasNext()) {
                result = result + it.next() + "<br>";
            }
        }
        if (softTencentApkInfo != null && !softTencentApkInfo.isEmpty()) {
            result = result + "<br><br>腾讯报毒：<br>";
            Iterator<String> it = softTencentApkInfo.iterator();
            while (it.hasNext()) {
                result = result + it.next() + "<br>";
            }
        }
        if (softBaiDuApkInfo != null && !softBaiDuApkInfo.isEmpty()) {
            result = result + "<br><br>百度报毒：<br>";
            Iterator<String> it = softBaiDuApkInfo.iterator();
            while (it.hasNext()) {
                result = result + it.next() + "<br>";
            }
        }
        result = result + "<br>";
        return result;
    }
}
