package com.craft.myinvokehook.newphone;

import com.alibaba.fastjson.JSON;
import com.craft.myinvokehook.Utils;

import java.util.HashMap;

public class PhoneMgr {
//
    public static PhoneInfo getRandomPhoneinfo(){
        String info = "{\"androidId\":\"522c0e2aa3c8eff6\",\"board\":\"SM-N9760\",\"bootloader\":\"unknown\",\"brand\":\"samsung\",\"buildID\":\"N2G48C\",\"codename\":\"REL\",\"device\":\"aosp\",\"display\":\"N2G48C\",\"fingerprint\":\"google/android_x86/x86:7.1.2/N2G48C/N975FXXU1ASGO:/release-keys\",\"getBSSID\":\"00:AA:81:9a:db:56\",\"getCellLocation\":\"[1028,32305,0]\",\"getDataActivity\":\"0\",\"getDeviceId\":\"865166029262592\",\"getExtraInfo\":\"\\\"A3681\\\"\",\"getIpAddress\":\"1157697708\",\"getLine1Number\":\"\",\"getLocalHost\":\"localhost/127.0.0.1\",\"getMacAddress\":\"00:db:36:9a:56:81\",\"getNetworkId\":\"0\",\"getNetworkOperator\":\"46000\",\"getNetworkOperatorName\":\"CHINA MOBILE\",\"getNetworkType\":\"0\",\"getRadioVersion\":\"\",\"getRssi\":\"-42\",\"getSSID\":\"\\\"A3681\\\"\",\"getSimOperator\":\"46000\",\"getSimOperatorName\":\"China Mobile GSM\",\"getSimSerialNumber\":\"89860012261199491919\",\"getSubscriberId\":\"460006753440678\",\"getSubtype\":\"0\",\"getType\":\"1\",\"getTypeName\":\"WIFI\",\"hardware\":\"android_x86\",\"host\":\"ubuntu\",\"incremental\":\"N975FXXU1ASGO\",\"manufacturer\":\"samsung\",\"model\":\"SM-N9760\",\"product\":\"SM-N9760\",\"release\":\"7.1.2\",\"scanResultsBSSID\":\"00:AA:81:9a:db:56\",\"scanResultsCapabilities\":\"[ESS]\",\"scanResultsFrequency\":\"2447\",\"scanResultsLevel\":\"-43\",\"scanResultsSSID\":\"A3681\",\"sdk\":\"25\",\"sdkInt\":\"25\",\"serial\":\"00108d52\",\"tags\":\"release-keys\",\"time\":\"1616072733000\",\"type\":\"user\",\"user\":\"build\",\"version\":\"09\",\"widthPixels\":\"2.0\"}";
        HashMap<String, String> map = getRandomBoard();
        PhoneInfo phoneInfo = JSON.parseObject(info, PhoneInfo.class);
        phoneInfo.setAndroidId(Utils.getRandomString(16));
        phoneInfo.setBoard(map.get("board"));
        phoneInfo.setBrand(map.get("brand"));
        phoneInfo.setManufacturer(map.get("brand"));
        phoneInfo.setModel(map.get("buildid"));
        phoneInfo.setSerial(Utils.getRandomString(16));
        phoneInfo.setBootloader(map.get("brand"));
        phoneInfo.setGetMacAddress(getRandomMac());
        phoneInfo.setScanResultsBSSID(getRandomMac());
        phoneInfo.setGetBSSID(getRandomMac());
        phoneInfo.setBuildID(map.get("buildid"));
        phoneInfo.setProduct(map.get("brand"));
        phoneInfo.setFingerprint(map.get("fingerprint"));
        phoneInfo.setDisplay(map.get("display"));
        phoneInfo.setGetDeviceId("86"+Utils.getRandomString(13,"1234567890"));
        phoneInfo.setGetSubscriberId(Utils.getRandomString(13,"123456789"));
        phoneInfo.setGetSimSerialNumber("8986"+Utils.getRandomString(16,"1234567890"));
        phoneInfo.setGetSSID(Utils.getRandomString(6));
        phoneInfo.setScanResultsSSID(Utils.getRandomString(6));
        phoneInfo.setGetExtraInfo(Utils.getRandomString(6));
        phoneInfo.setGetCellLocation("["+Utils.getRandomString(4,"123456789")+","+Utils.getRandomString(4,"123456789")+","+0+"]");
        phoneInfo.setHardware(map.get("hardw"));
        phoneInfo.setDevice(map.get("brand"));
        return phoneInfo;
    }

    private static HashMap<String,String> getRandomBoard(){
        String[] brandList = new String[]{"samsung","vovi","oppo","xiaomi","oneplus","huawei","lenovo"};
        String board = "msm"+Utils.getRandomString(4,"6789");
        int index = Integer.parseInt(Utils.getRandomString(1,"1234567"));
        String brand = brandList[index];
        String buildID = Utils.getRandomString(5,"NMF123456789XYZ").toUpperCase();
        String[] hardware = new String[]{"qcom","mtk","Kirin","exynos"};
        String hardw = hardware[Integer.parseInt(Utils.getRandomString(1,"1234"))];
        String display = brand+Utils.getRandomString(4)+"_"+Utils.getRandomString(2)+"_"+Utils.getRandomString(4);
        String fingerprint = "/"+hardw+"/"+brand+"/"+buildID+":/release-keys";
        HashMap<String,String> map = new HashMap<>();
        map.put("board",board);
        map.put("brand",brand);
        map.put("display",display);
        map.put("fingerprint",fingerprint);
        map.put("buildid",buildID);
        map.put("hardw",hardw);
        return map;
    }

    private static String getRandomMac(){
        //00:db:36:9a:56:81
        StringBuilder mac = new StringBuilder(Utils.getRandomString(12));
        mac.insert(2,":");
        mac.insert(5,":");
        mac.insert(8,":");
        mac.insert(11,":");
        mac.insert(14,":");
        return mac.toString();
    }


}
