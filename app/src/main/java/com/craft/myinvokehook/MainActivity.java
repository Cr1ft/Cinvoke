package com.craft.myinvokehook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.craft.myinvokehook.newphone.PhoneMgr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layout;
    private SharedPreferences sp;
    private TextView info;

    @SuppressLint("SdCardPath")
    private String configFile = "/sdcard/Cinvoke/config.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = findViewById(R.id.hook_status_tv);
        MultiprocessSharedPreferences.setAuthority("com.craft.myinvokehook.provider");
        sp = MultiprocessSharedPreferences.getSharedPreferences(this, "config", 0);

        myRequetPermission();
        AddInlineHookView();
        AddNewPhoneView();
        AddFridaHookView();


    }


    public void AddInlineHookView() {
        final Button selectApp = findViewById(R.id.select_app);
        final Switch status = findViewById(R.id.inlinehook_start_sw);
        final AppAdapter appAdapter = new AppAdapter(this);
        final AlertDialog selector = new AlertDialog.Builder(this)
                .setTitle("Select App")
                .setAdapter(appAdapter, new DialogInterface.OnClickListener() {
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String InlineHookhookee = ((PackageInfo) appAdapter.getItem(i)).packageName;
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("inlineHookTarget", InlineHookhookee);
                        ed.commit();
                        update();
                        dialogInterface.dismiss();
                    }
                })
                .create();


        selectApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.show();
            }
        });

        int onInLineHook = sp.getInt("OnInLineHook", 0);

        if (onInLineHook == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setChecked(true);
                }
            });
        }
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(
                    CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置LinearLayout垂直布局
                    SharedPreferences.Editor ed = sp.edit();
                    String target = sp.getString("inlineHookTarget","null");
                    if(!"null".equals(target)){
                        if(!(new File("/sdcard/Android/data/"+target+"/files/Cinvoke").exists())){
                            Utils.copyDir("/sdcard/Cinvoke","/sdcard/Android/data/"+target+"/files");
                        }
                    }
                    ed.putInt("OnInLineHook", 1);
                    ed.commit();
                    update();
                } else {
                    //设置水平布局
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putInt("OnInLineHook", 0);
                    ed.commit();
                    update();

                }
            }
        });
        update();

    }

    public void AddFridaHookView() {
        //mod:0 (wait) 1 (resume) 2 (ScriptFile)
        //ToDo
        Button select = findViewById(R.id.select_frida_target_btn);
        final AppAdapter appAdapter = new AppAdapter(this);
        final AlertDialog selector = new AlertDialog.Builder(this)
                .setTitle("Select App")
                .setAdapter(appAdapter, new DialogInterface.OnClickListener() {
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fridaTarget = ((PackageInfo) appAdapter.getItem(i)).packageName;
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("fridaHookTarget", fridaTarget);
                        ed.commit();
                        update();
                        dialogInterface.dismiss();
                    }
                })
                .create();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.show();
            }
        });
        RadioGroup fridaModRadio = findViewById(R.id.select_frida_mod);
        RadioButton fridaModListen = findViewById(R.id.select_frida_mod_listen);
        RadioButton fridaModScript = findViewById(R.id.select_frida_mod_script);

        final LinearLayout fridaModTagView = findViewById(R.id.frida_layout);
        final LayoutInflater inflater = LayoutInflater.from(this);

        fridaModRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.select_frida_mod_listen){
                    fridaModTagView.removeAllViews();
                    LinearLayout addLayout = (LinearLayout) inflater.inflate(
                            R.layout.activity_main_frida_listen, null).findViewById(R.id.frida_listen_xml);
                    fridaModTagView.addView(addLayout);

                    RadioGroup fridaResumeRoWaitGroup = findViewById(R.id.frida_resume_or_wait);
                    RadioButton fridaResumeBtn = findViewById(R.id.frida_listen_resume);
                    RadioButton fridaWaitBtn = findViewById(R.id.frida_listen_wait);
                    final EditText fridaListenPort = findViewById(R.id.frida_listen_port);
                    Button fridaListenStartInject = findViewById(R.id.frida_listen_start_inject);

                    if(sp.getInt("frdaMod",-1) == 0){
                        fridaWaitBtn.setChecked(true);
                    }else if(sp.getInt("frdaMod",-1)==1){
                        fridaResumeBtn.setChecked(true);
                    }

                    fridaListenPort.setText( String.valueOf(sp.getInt("fridaPort",27042)));
                    fridaResumeRoWaitGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            if(i == R.id.frida_listen_resume){
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putInt("frdaMod", 1);
                                ed.commit();
                                update();

                            }else if(i == R.id.frida_listen_wait){
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putInt("frdaMod", 0);
                                ed.commit();
                                update();
                            }
                        }
                    });

                    fridaListenStartInject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            int port = Integer.parseInt(fridaListenPort.getText().toString().trim());
                            String mod = "resume";
                            if(sp.getInt("frdaMod",-1) == 0){
                                mod = "wait";
                            }
                            String json = String.format("{\"interaction\":{\"type\":\"listen\",\"address\":\"0.0.0.0\",\"port\":%d,\"on_port_conflict\":\"fail\",\"on_load\":\"%s\"}}",port,mod);
                            try {
                                Utils.WriteJsonFile("/sdcard/Cinvoke/libCF.config.so",new JSONObject(json));
                                String target = sp.getString("fridaHookTarget","null");


                                SharedPreferences.Editor ed = sp.edit();
                                ed.putInt("OnFridaHook", 1);
                                ed.putInt("fridaPort",port);
                                ed.putString("fridaScriptPath","N/A");
                                ed.commit();
                                update();

                                int status = sp.getInt("OnFridaHook",0);
                                if(!"null".equals(target) && status == 1){
                                    if(!(new File("/sdcard/Android/data/"+target+"/files/Cinvoke").exists())){
                                        Utils.copyDir("/sdcard/Cinvoke","/sdcard/Android/data/"+target+"/files");
                                    }else{
                                        Utils.copyFile("/sdcard/Cinvoke/libCF.config.so","/sdcard/Android/data/"+target+"/files/Cinvoke/libCF.config.so");
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }else if(i == R.id.select_frida_mod_script){
                    fridaModTagView.removeAllViews();
                    LinearLayout addLayout = (LinearLayout) inflater.inflate(
                            R.layout.activity_main_frida_script, null).findViewById(R.id.frida_script_xml);
                    fridaModTagView.addView(addLayout);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putInt("frdaMod", 2);
                    ed.commit();
                    update();
                    final EditText fridaScriptPath = findViewById(R.id.frida_script_path);
                    String p = sp.getString("fridaScriptPath","");
                    fridaScriptPath.setText(p);
                    Button fridaScriptInject = findViewById(R.id.frida_script_start_inject);
                    fridaScriptInject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String path = fridaScriptPath.getText().toString().trim();
                            String json = String.format("{\"interaction\":{\"type\":\"script\",\"path\":\"%s\"}}",path);
                            try {
                                Utils.WriteStringFile("/sdcard/Cinvoke/libCF.config.so",json);
                                String target = sp.getString("fridaHookTarget","null");
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putInt("OnFridaHook", 1);
                                ed.putString("fridaScriptPath",path);
                                ed.putInt("fridaPort",0);
                                ed.commit();
                                int status = sp.getInt("OnFridaHook",0);
                                if(!"null".equals(target) && status == 1){
                                    if(!(new File("/sdcard/Android/data/"+target+"/files/Cinvoke").exists())){
                                        Utils.copyDir("/sdcard/Cinvoke","/sdcard/Android/data/"+target+"/files");
                                    }else{
                                        Utils.copyFile("/sdcard/Cinvoke/libCF.config.so","/sdcard/Android/data/"+target+"/files/Cinvoke/libCF.config.so");
                                    }
                                }
                                update();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });
        if(sp.getInt("frdaMod",-1) == 2){
            fridaModScript.setChecked(true);
        }else if(sp.getInt("frdaMod",-1)==0 || sp.getInt("frdaMod",-1) == 1){
            fridaModListen.setChecked(true);
        }


    }

    public void AddNewPhoneView() {
        final Button selectApp = findViewById(R.id.select_newphone_btn);
        final Switch status = findViewById(R.id.select_newphone_sw);
        final AppAdapter appAdapter = new AppAdapter(this);
        final AlertDialog selector = new AlertDialog.Builder(this)
                .setTitle("Select App")
                .setAdapter(appAdapter, new DialogInterface.OnClickListener() {
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String InlineHookhookee = ((PackageInfo) appAdapter.getItem(i)).packageName;
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("NewPhoneTarget", InlineHookhookee);
                        ed.commit();
                        update();
                        dialogInterface.dismiss();
                    }
                })
                .create();


        selectApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.show();
            }
        });

        int onInLineHook = sp.getInt("OnNewPhone", 0);

        if (onInLineHook == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setChecked(true);
                }
            });
        }
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(
                    CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置LinearLayout垂直布局
                    String info = JSON.toJSONString(PhoneMgr.getRandomPhoneinfo());
                    SharedPreferences.Editor ed = sp.edit();
                    String target = sp.getString("NewPhoneTarget","null");
                    if(!"null".equals(target)){
                        if(!(new File("/sdcard/Android/data/"+target+"/files/Cinvoke").exists())){
                            Utils.copyDir("/sdcard/Cinvoke","/sdcard/Android/data/"+target+"/files");
                        }
                    }
                    ed.putString("NewPhonInfo",info);
                    ed.putInt("OnNewPhone", 1);
                    ed.putString("NewInsInfo",String.valueOf(System.currentTimeMillis()));
                    ed.commit();
                    update();
                } else {
                    //设置水平布局
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putInt("OnNewPhone", 0);
                    ed.putString("NewPhonInfo","");
                    ed.commit();
                    update();

                }
            }
        });
        update();
    }

    public void update() {
//{
//  "OnInLineHook": 0,
//  "inlineHookTarget":"",
//  "OnFridaHook": 0,
//  "fridaHookTarget": "",
//  "frdaMod": "",
//  "fridaPort": ""
//}

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int onInLineHook = sp.getInt("OnInLineHook", 0);
                String inlineHookTarget = sp.getString("inlineHookTarget", "N/A");
                int onFridaHook = sp.getInt("OnFridaHook", 0);
                String fridaHookTarget = sp.getString("fridaHookTarget", "N/A");
                int frdaMod = sp.getInt("frdaMod", 0);
                int fridaPort = sp.getInt("fridaPort", 0);
                String fridaScriptPath = sp.getString("fridaScriptPath","N/A");
                int OnNewPhone = sp.getInt("OnNewPhone",0);
                String NewPhoneTarget = sp.getString("NewPhoneTarget","N/A");
                String fm = "";
                switch (frdaMod) {
                    case 0:
                        fm = "wait";
                        break;
                    case 1:
                        fm = "Resume";
                        break;
                    case 2:
                        fm = "File";
                        break;

                }


                String status = "\nInline Hook status:  " + onInLineHook +
                        "\nInline Hook:   " + inlineHookTarget +
                        "\nFrida Hook status:   " + onFridaHook +
                        "\nFrida Hook:   " + fridaHookTarget +
                        "\nFrida Prot:  " + fridaPort +
                        "\nFrida Mod:   " + fm +
                        "\nFrida Script:   "+fridaScriptPath+
                        "\nOnNewPhone:   " + OnNewPhone +
                        "\nNewPhoneTarget： "+NewPhoneTarget;
                info.setText(status);
            }
        });


    }

    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
        }
    }


}
