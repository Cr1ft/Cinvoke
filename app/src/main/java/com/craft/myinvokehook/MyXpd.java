package com.craft.myinvokehook;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.craft.myinvokehook.newphone.Newphone;
import com.craft.nativeJNI.NativeHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.robv.android.craftxpd.IXpdHookLoadPackage;
import de.robv.android.craftxpd.IXpdHookZygoteInit;
import de.robv.android.craftxpd.XC_MethodHook;
import de.robv.android.craftxpd.XpdBridge;
import de.robv.android.craftxpd.XpdHelpers;
import de.robv.android.craftxpd.callbacks.XC_LoadPackage;
/*
	注意！
	你需要关掉Instant Run才能在Android Studio里使用“运行App”，不然Xpd会出现找不到类的错误。
	Be Awared!
	You should disable Instant Run if you want to use 'Run App' from Android Studio, or Xpd Framework will not find module class from base.apk.
	https://developer.android.com/studio/run/#disable-ir
*/

public class MyXpd implements IXpdHookLoadPackage {

    String packageName;
    Boolean isFirstApplication;
    ClassLoader classLoader;
    String processName;
    ApplicationInfo appInfo;
    Context context;
    String provder = "com.craft.myinvokehook.provider";


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        gatherInfo(loadPackageParam);

        XpdHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        context = (Context) param.args[0];
                        ClassLoader loader = context.getClassLoader();
                        //Write your code here.
                        MultiprocessSharedPreferences.setAuthority(provder);
                        SharedPreferences sp = MultiprocessSharedPreferences.getSharedPreferences(context, "config", context.MODE_PRIVATE);
                        int inline = sp.getInt("OnInLineHook",0);
                        int fr1da = sp.getInt("OnFridaHook",0);
                        if(inline == 1){
                           String pkgee = sp.getString("inlineHookTarget","com.");
                            XpdBridge.log(packageName);
                           if(pkgee.equals(packageName)){
                               String MyInvokeSoTargetFileName = "/data/data/" + packageName + "/libCR.so";
                               //XpdBridge.log("pakagename="+MyInvokeSoTargetFileName);
                               injectInlineSo(packageName,MyInvokeSoTargetFileName);
                               NativeHelper.startMethodTrace();
                           }

                        }

                        if(fr1da == 1){
                            String pkgee = sp.getString("fridaHookTarget","com.");
                            XpdBridge.log(packageName);
                            if(pkgee.equals(packageName)){
                                String myfr1daSo = "/data/data/" + packageName + "/libCF.so";
                                String myfr1daconf = "/data/data/" + packageName + "/libCF.config.so";
                                //XpdBridge.log("pakagename="+myfr1daSo);
                                injectfr1daSo(packageName,myfr1daSo,myfr1daconf);
                            }
                        }

                        int isNewPhone = sp.getInt("OnNewPhone",0);
                        String targetPkg = sp.getString("NewPhoneTarget","");
                        if(isNewPhone == 1 && targetPkg.equals(packageName)){
                            if(inline != 1){
                                String MyInvokeSoTargetFileName = "/data/data/" + packageName + "/libCR.so";
                                injectInlineSo(packageName,MyInvokeSoTargetFileName);
                            }
                            String info = sp.getString("NewPhonInfo","");
                            String insInfo = sp.getString("NewInsInfo","");
                            Newphone newPhone = new Newphone(loader,context,info,insInfo);
                            newPhone.hook();
                        }
                    }
                }
        );
    }

    private void gatherInfo(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        packageName = loadPackageParam.packageName;
        isFirstApplication = loadPackageParam.isFirstApplication;
        classLoader = loadPackageParam.classLoader;
        processName = loadPackageParam.processName;
        appInfo = loadPackageParam.appInfo;
    }

    private void injectInlineSo(String packageName,String target){
        File mTarget = new File(target);
        File Targe64SO = new File("/sdcard/Android/data/"+packageName+"/files/Cinvoke/64/libCR.so");
        File Targe32SO = new File("/sdcard/Android/data/"+packageName+"/files/Cinvoke/32/libCR.so");
        if (System.getProperty("os.arch").indexOf("64") >= 0) {
            if (Targe64SO.exists()) {
                //XpdBridge.log("HOOKSTARXXXXXXXXXXXXX");
                if(!mTarget.exists()){
                    XpdBridge.log("COPYTO"+target);
                    mycopy(Targe64SO, mTarget);
                }

                System.load(target);
            }
        } else {
            if (Targe32SO.exists()) {
                //XpdBridge.log("HOOKSTARXXXXXXXXXXXXX");
                if(!mTarget.exists()){
                    XpdBridge.log("COPYTO"+target);
                    mycopy(Targe32SO, mTarget);
                }
                System.load(target);
            }
        }
    }

    private void injectfr1daSo(String packageName,String target,String config){
        File mTarget = new File(target);
        File mConfig = new File(config);
        File Fr1daTarge64SO = new File("/sdcard/Android/data/"+packageName+"/files/Cinvoke/64/libCF.so");
        File Fr1daTarge32SO = new File("/sdcard/Android/data/"+packageName+"/files/Cinvoke/32/libCF.so");
        File Fr1daConfig = new File("/sdcard/Android/data/"+packageName+"/files/Cinvoke/libCF.config.so");
        if(Fr1daConfig.exists()){
            XpdBridge.log("COPYTO"+mConfig);
            mycopy(Fr1daConfig, mConfig);
        }

        if (System.getProperty("os.arch").indexOf("64") >= 0) {
            if (Fr1daTarge64SO.exists()) {
                XpdBridge.log("Fr1da Inject");
                if(!mTarget.exists()){
                    XpdBridge.log("COPYTO"+Fr1daTarge64SO);
                    mycopy(Fr1daTarge64SO, mTarget);
                }

                System.load(target);
                XpdBridge.log("system load target");
            }
        } else {
            if (Fr1daTarge32SO.exists()) {
                XpdBridge.log("Fr1da Inject");
                if(!mTarget.exists()){
                    XpdBridge.log("COPYTO"+Fr1daTarge32SO);
                    mycopy(Fr1daTarge32SO, mTarget);
                }
                System.load(target);
                XpdBridge.log("system load target");
            }
        }


    }

    private static void mycopy(File srcFileName, File trcFileName) {
        InputStream in = null;
        OutputStream out = null;
        try {
            // in = File.open(srcFileName);
            in = new FileInputStream(srcFileName);
            out = new FileOutputStream(trcFileName);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = in.read(bytes)) != -1)
                out.write(bytes, 0, i);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
