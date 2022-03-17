package com.craft.myinvokehook;

import android.content.Context;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class Utils {
    public static void copyBigDataToSD(Context c, String filename,String strOutFileName) throws IOException
    {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = c.getAssets().open(filename);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    public static JSONObject readJsonFile(String file) throws IOException, JSONException {
        FileReader fr = new FileReader(new File(file));
        BufferedReader br = new BufferedReader(fr);
        StringBuilder fileStr = new StringBuilder();
        String tempStr = br.readLine();
        while(tempStr!=null) {
            fileStr.append(tempStr);
            tempStr = br.readLine();
        }
        br.close();
        fr.close();
        return new JSONObject(fileStr.toString());
    }
    public static void WriteJsonFile(String file,JSONObject json) throws IOException, JSONException {
        FileWriter fr = new FileWriter(new File(file));
        fr.write(json.toString());
        fr.close();
    }

    public static void WriteStringFile(String file,String json) throws IOException, JSONException {
        FileWriter fr = new FileWriter(new File(file));
        fr.write(json);
        fr.close();
    }

    public static void copyDir(String sourcePath, String newPath) {
        if(Build.VERSION.SDK_INT >=30){
            try {
                Runtime.getRuntime().exec(new String[]{"su","-c", String.format("%s %s %s","cp -r",sourcePath,newPath)});
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        File start = new File(sourcePath);
        File end = new File(newPath);
        String[] filePath = start.list();		//获取该文件夹下的所有文件以及目录的名字
        if(!end.exists()) {
            end.mkdir();
        }
        for(String temp:filePath) {
            //查看其数组中每一个是文件还是文件夹
            if(new File(sourcePath+File.separator+temp).isDirectory()) {
                //为文件夹，进行递归
                copyDir(sourcePath+File.separator+temp, newPath+File.separator+temp);
            }else {
                //为文件则进行拷贝
                copyFile(sourcePath+File.separator+temp, newPath+File.separator+temp);
            }
        }
    }

    public static void copyFile(String sourcePath, String newPath) {
        if(Build.VERSION.SDK_INT >=30){
            try {
                Runtime.getRuntime().exec(new String[]{"su","-c", String.format("%s %s %s","cp",sourcePath,newPath)});
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        File start = new File(sourcePath);
        File end = new File(newPath);
        try(BufferedInputStream bis=new BufferedInputStream(new FileInputStream(start));
            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(end))) {
            int len = 0;
            byte[] flush = new byte[1024];
            while((len=bis.read(flush)) != -1) {
                bos.write(flush, 0, len);
            }
            bos.flush();
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String getRandomString(int length){
        return getRandomString(length,"abcdef0123456789");
    }
    //length用户要求产生字符串的长度
    public static String getRandomString(int length,String pool){
        Random random=new Random();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<length;i++){
            int number=random.nextInt(pool.length()-1);
            sb.append(pool.charAt(number));
        }
        return sb.toString();
    }
}
