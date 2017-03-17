package cp.com.accessibilityfunction;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by PengChen on 2017/3/8.
 */

public class AccessibilityFun extends AccessibilityService {
    /**
     * Callback for {@link AccessibilityEvent}s.
     *
     * @param event The new event. This event is owned by the caller and cannot be used after
     *              this method returns. Services wishing to use the event after this method returns should
     *              make a copy.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        setupApk(event);
        tencentTips(event);
        googlePlayService(event);
        String packageName = (String)event.getPackageName();
        if (packageName.equals("cn.opda.a.phonoalbumshoushou")) {
            doBaiDuTest(event);
        } else if (packageName.equals("com.tencent.qqpimsecure")) {
            doTencentTest(event);
        } else if (packageName.equals("com.qihoo360.mobilesafe")) {
            do360Test(event);
        }
    }

    /**
     * Callback for interrupting the accessibility feedback.
     */
    @Override
    public void onInterrupt() {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.notificationTimeout = 80;
        info.packageNames = new String[]{"com.com.android.packageinstaller",
                "com.sec.android.app.capabilitymanager",
                "com.tencent.qqpimsecure",//Tencent
                "com.qihoo360.mobilesafe",//360
                "cn.opda.a.phonoalbumshoushou",//百度
                "com.samsung.android.sm"};
        setServiceInfo(info);
        super.onServiceConnected();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void setupApk(AccessibilityEvent event){
        String name = (String) event.getPackageName();
        if (name.equals("com.android.packageinstaller")){
            Log.i(AppUtil.TAG, "EventType = " + event.getEventType());
            AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
            if (rootNodeInfo != null){
                List<AccessibilityNodeInfo> stop_nodes = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                                "com.android.packageinstaller:id/ok_button");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    Log.i(AppUtil.TAG, "ok_button EventType = " + event.getEventType() + " size = " + stop_nodes.size());
                    for (int i = 0; i < stop_nodes.size(); i++){
                        AccessibilityNodeInfo node = stop_nodes.get(i);
                        if (node.isEnabled() && node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }

            //安装完成之后的完成、打开按钮
            rootNodeInfo = getRootInActiveWindow();
            if (rootNodeInfo != null){
                List<AccessibilityNodeInfo> stop_nodes = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                                "com.android.packageinstaller:id/done_button");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    Log.i(AppUtil.TAG, "done_button EventType = " + event.getEventType() + " size = " + stop_nodes.size());
                    for (int i = 0; i < stop_nodes.size(); i++){
                        AccessibilityNodeInfo node = stop_nodes.get(i);
                        if (node.isEnabled() && node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }

            //出错提示,也可能是360点击立即处理之后的卸载提示
            rootNodeInfo = getRootInActiveWindow();
            if (rootNodeInfo != null){
                List<AccessibilityNodeInfo> stop_nodes = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                                "android:id/button1");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    Log.i(AppUtil.TAG, "button1 EventType = " + event.getEventType() + " size = " + stop_nodes.size());
                    for (int i = 0; i < stop_nodes.size(); i++){
                        AccessibilityNodeInfo node = stop_nodes.get(i);
                        if (node.isEnabled() && node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
            //android:id/button3底部或者中间弹出来的dialogActivity(确定按钮)
            rootNodeInfo = getRootInActiveWindow();
            if (rootNodeInfo != null){
                List<AccessibilityNodeInfo> stop_nodes = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                        "android:id/button3");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    Log.i(AppUtil.TAG, "button3 EventType = " + event.getEventType() + " size = " + stop_nodes.size());
                    for (int i = 0; i < stop_nodes.size(); i++){
                        AccessibilityNodeInfo node = stop_nodes.get(i);
                        if (node.isEnabled() && node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }

            //360手机安装GooglePlay时，弹出木马应用的提示
            rootNodeInfo = getRootInActiveWindow();
            if (rootNodeInfo != null){
                List<AccessibilityNodeInfo> stop_nodes = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                        "com.android.packageinstaller:id/danger_dialog_BTN_goon");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    Log.i(AppUtil.TAG, "danger_dialog_BTN_goon EventType = " + event.getEventType() + " size = " + stop_nodes.size());
                    for (int i = 0; i < stop_nodes.size(); i++){
                        AccessibilityNodeInfo node = stop_nodes.get(i);
                        String continueText = (String)node.getText();
                        Log.i(AppUtil.TAG, "continueText = " + continueText);
                        if (node.isEnabled() && node.isClickable() && "继续安装".equals(continueText)) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }

        //安装完成之后的确定按钮
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        if (rootNodeInfo != null){
            List<AccessibilityNodeInfo> stop_nodes = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                            "com.sec.android.app.capabilitymanager:id/confirm");
            if (stop_nodes != null && !stop_nodes.isEmpty()) {
                Log.i(AppUtil.TAG, "confirm EventType = " + event.getEventType() + " size = " + stop_nodes.size());
                for (int i = 0; i < stop_nodes.size(); i++){
                    AccessibilityNodeInfo node = stop_nodes.get(i);
                    if (node.isEnabled() && node.isClickable()) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }
    }

    //腾讯安装完成之后提示删除安装包
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void tencentTips(AccessibilityEvent event){
        String name = (String) event.getPackageName();
        if (name.equals("com.tencent.qqpimsecure")){
            Log.i(AppUtil.TAG, "tencentTips" + event.toString());
            AccessibilityNodeInfo info = event.getSource();
            if (info != null){
                List<AccessibilityNodeInfo> stop_nodes =
                        info.findAccessibilityNodeInfosByViewId(
                                "com.tencent.qqpimsecure:id/dp");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    for (int i = 0; i < stop_nodes.size(); i++){
                        AccessibilityNodeInfo node = stop_nodes.get(i);
                        if (node.isEnabled() && node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }

    //可能是samsung系统安装
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void googlePlayService(AccessibilityEvent event){
        String name = (String) event.getPackageName();
        if (name.equals("com.samsung.android.sm")){
            Log.i(AppUtil.TAG, "googlePlayService" + event.toString());
            AccessibilityNodeInfo info = event.getSource();
            if (info != null){
                List<AccessibilityNodeInfo> stop_nodes =
                        info.findAccessibilityNodeInfosByViewId(
                                "android:id/button1");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    for (int i = 0; i < stop_nodes.size(); i++){
                        AccessibilityNodeInfo node = stop_nodes.get(i);
                        if (node.isEnabled() && node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }

    //百度杀毒测试适配
    public void doBaiDuTest(AccessibilityEvent event){
        AccessibilityNodeInfo info = getRootInActiveWindow();//event.getSource(); //getRootInActiveWindow();
        if (info != null) {
//            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("点击优化");
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("百宝箱");
            if (infoList != null && !infoList.isEmpty()) {
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    int count = 0;
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode != null && parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("手机充值");
            if (infoList != null && !infoList.isEmpty()) {
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    process.getOutputStream().write(("input tap 150 400" + "\n").getBytes());
                    process.getOutputStream().write("exit\n".getBytes());
                    process.getOutputStream().flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("个人隐私可能被泄露");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "百度 infoList.size() = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    int count = 0;
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode != null && parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("建议卸载");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "建议卸载 infoList.size() = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode != null && parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("立即卸载");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, " 立即卸载 infoList.size() = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode != null && parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("应用详情");
            if (infoList != null && !infoList.isEmpty()) {
                //截图，之后卸载
                Log.i(AppUtil.TAG, "应用详情 infoList.size() = " + infoList.size());
                AppUtil.getSingleInstance().softCount = 0;
                hasVirusProcess(AppUtil.safeBaiDu);
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {//没有报毒的情况，这种不会用到。点击优化的时候可能出现
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("完成");
            List<AccessibilityNodeInfo> infoListDaoDu = info.findAccessibilityNodeInfosByText("个人隐私可能被泄露");
            if (infoList != null && !infoList.isEmpty() && (infoListDaoDu == null || infoListDaoDu.isEmpty())) {
                Log.i(AppUtil.TAG, "baudu wanc infoList.size() = " + infoList.size());
                AppUtil.getSingleInstance().softCount = 0;
                AppUtil.backToHome(getApplicationContext());
                noVirusProcess(AppUtil.safeBaiDu);
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {//没有报毒的情况
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("手机状态安全");
            if (infoList != null && !infoList.isEmpty()) {
                //可以点击(50,100)坐标
                Log.i(AppUtil.TAG, "baidu infoList.size() = " + infoList.size());
                if (AppUtil.getSingleInstance().softCount < AppUtil.safeCount) {
                    AppUtil.getSingleInstance().softCount++;
                    try {
                        Process process = Runtime.getRuntime().exec("su");
                        process.getOutputStream().write(("input tap 50 100" + "\n").getBytes());
                        process.getOutputStream().write("exit\n".getBytes());
                        process.getOutputStream().flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AppUtil.getSingleInstance().softCount = 0;
                    AppUtil.backToHome(getApplicationContext());
                    noVirusProcess(AppUtil.safeBaiDu);
                }
            }
        }
    }

    //腾讯杀毒测试适配
    public void doTencentTest(AccessibilityEvent event){
        AccessibilityNodeInfo info = getRootInActiveWindow();//event.getSource(); //getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("安全防护");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "安全防护 EventType = " + event.getEventType() + " size = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    int count = 0;
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode != null && parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("立即扫描");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "立即扫描 EventType = " + event.getEventType() + " size = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    int count = 0;
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode != null && parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
                return;
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("处理");
            List<AccessibilityNodeInfo> dangerInfoList = info.findAccessibilityNodeInfosByText("危险");
            if (infoList != null && !infoList.isEmpty() && dangerInfoList != null && !dangerInfoList.isEmpty()) {
                Log.i(AppUtil.TAG, "处理危险 EventType = " + event.getEventType() + " size = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    int count = 0;
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode != null && parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("风险项详情");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "风险项详情 EventType = " + event.getEventType() + " size = " + infoList.size());
                Log.i(AppUtil.TAG, "准备截图");
                AppUtil.getSingleInstance().softCount = 0;
                hasVirusProcess(AppUtil.safeTencent);
            }
        }
        //出现完成，表示没有报毒
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("完成");
            if (infoList != null && !infoList.isEmpty()) {
                //直接点击操作就可以
                Log.i(AppUtil.TAG, "完成 没有报毒 EventType = " + event.getEventType() + " size = " + infoList.size());
                if (AppUtil.getSingleInstance().softCount < AppUtil.safeCount) {
                    AppUtil.getSingleInstance().softCount++;
                    AccessibilityNodeInfo nodeInfo;
                    for (int i = 0; i < infoList.size(); i++) {
                        nodeInfo = infoList.get(i);
                        if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                } else {
                    AppUtil.getSingleInstance().softCount = 0;
                    AppUtil.backToHome(getApplicationContext());
                    noVirusProcess(AppUtil.safeTencent);
                }
            }
        }
    }

    //360杀毒测试适配
    public void do360Test(AccessibilityEvent event){
        AccessibilityNodeInfo info = getRootInActiveWindow();//event.getSource(); //getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("手机杀毒");
            //com.qihoo360.mobilesafe:id/g8
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "手机杀毒 EventType = " + event.getEventType() + " size = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    String nodeID = nodeInfo.getViewIdResourceName();
                    boolean trueIDfu = nodeID.equals("com.qihoo360.mobilesafe:id/fu");
                    boolean trueIDg8 = nodeID.equals("com.qihoo360.mobilesafe:id/g8");
                    int count = 0;
                    if ((trueIDfu || trueIDg8) && nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if ((trueIDfu || trueIDg8) && count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("快速扫描");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "快速扫描 EventType = " + event.getEventType() + " size = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    int count = 0;
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
                        if (parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("[木马]建议清除");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "[木马]建议清除 EventType = " + event.getEventType() + " size = " + infoList.size());
                AccessibilityNodeInfo nodeInfo;
                for (int i = 0; i < infoList.size(); i++) {
                    nodeInfo = infoList.get(i);
                    int count = 0;
                    if (nodeInfo.isEnabled() && nodeInfo.isClickable()) {
                        count = 1;
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (count == 0) {
                        AccessibilityNodeInfo parentNode = nodeInfo.getParent().getParent();
                        if (parentNode.isEnabled() && parentNode.isClickable()) {
                            parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("立即处理");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "立即处理 EventType = " + event.getEventType() + " size = " + infoList.size());
//                //截图，之后卸载
                AppUtil.getSingleInstance().softCount = 0;
                hasVirusProcess(AppUtil.safe360);
            }
        }
        //没有报毒的情况
        info = getRootInActiveWindow();
        if (info != null) {
            List<AccessibilityNodeInfo> infoList = info.findAccessibilityNodeInfosByText("未发现安全风险");
            if (infoList != null && !infoList.isEmpty()) {
                Log.i(AppUtil.TAG, "未发现安全风险 EventType = " + event.getEventType() + " size = " + infoList.size());
                if (AppUtil.getSingleInstance().softCount < AppUtil.safeCount && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                    AppUtil.getSingleInstance().softCount++;
                    //点击坐标(50,100)
                    try {
                        Process process = Runtime.getRuntime().exec("su");
                        process.getOutputStream().write(("input tap 50 100" + "\n").getBytes());
                        process.getOutputStream().write("exit\n".getBytes());
                        process.getOutputStream().flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AppUtil.getSingleInstance().softCount = 0;
                    AppUtil.backToHome(getApplicationContext());
                    noVirusProcess(AppUtil.safe360);
                }

            }
        }
    }

    private void hasVirusProcess(final String softShaDuName) {
//        String package1 = AppUtil.getSingleInstance().getPackageNameList(getApplicationContext(), false).get(0);
        String package1 = AppUtil.getSingleInstance().currentPackageName;
        String apkPath = AppUtil.getSingleInstance().getPackageNamePathMap(getApplicationContext()).get(package1);
        String[] fileName = apkPath.split("/");
        String apkFileName = fileName[fileName.length - 1];
        String picName = apkFileName + ".jpg";
        picName = picName.replace("(", "\\(");
        picName = picName.replace(")", "\\)");
//        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String common = "/system/bin/screencap -p " + "/sdcard/app_shadu_pic/" + AppUtil.getSingleInstance().currentSafe + "_"+ picName;
        Log.i(AppUtil.TAG, " AAAAAAAAAAAA " + common);
        AppUtil.screenShots(common);

        AppUtil.getSingleInstance().addSoftBaoDuApkInfo(AppUtil.getSingleInstance().currentSafe, apkFileName);
        Log.i(AppUtil.TAG, "截图完成，返回桌面");
        AppUtil.backToHome(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                noVirusProcess(softShaDuName);
            }
        }, 1000);
        //保存必要的信息

    }

    //关闭杀毒软件，卸载apk
    private void noVirusProcess(String softShaDuName) {
//        AppUtil.backToHome(getApplicationContext());
        AppUtil.finishAppWithPackageName(getApplicationContext(),AppUtil.getSingleInstance().currentSafe);
        String packageName = AppUtil.getSingleInstance().getPackageNameList(getApplicationContext(), false).get(0);
        AppUtil.uninstallAppWithPackageName(getApplicationContext(),
                AppUtil.getSingleInstance().currentPackageName, AppUtil.EXTRA_INFO_MID);
    }

}
