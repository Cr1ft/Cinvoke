package com.craft.myinvokehook;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends BaseAdapter {
    Context context;
    List<PackageInfo> packageInfo;

    AppAdapter(Context context) {
        this.context = context;
        packageInfo = context.getPackageManager().getInstalledPackages(0);
    }

    @Override
    public int getCount() {
        return packageInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return packageInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //调整了一下应用选择器的外观，感谢@smartdone大佬建议和帮助
        //https://github.com/monkeylord/XServer/pull/1/commits/ab718e13a8ef1486f43e1023f62e312b3ff10307
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        //创建图标
        ImageView iv_app_icon = new ImageView(context);
        iv_app_icon.setImageDrawable(packageInfo.get(i).applicationInfo.loadIcon(context.getPackageManager()));
        iv_app_icon.setLayoutParams(new ViewGroup.LayoutParams(80, 80));
        //iv_app_icon.setAdjustViewBounds(true);
        iv_app_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv_app_icon.setPadding(5,5,5,5);
        linearLayout.addView(iv_app_icon);
        //创建文本描述
        LinearLayout textLayout=new LinearLayout(context);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textLayout.setOrientation(LinearLayout.VERTICAL);

        TextView app_display_name = new TextView(context);
        app_display_name.setPadding(5, 15, 5, 3);
        app_display_name.getPaint().setFakeBoldText(true);
        app_display_name.setText(packageInfo.get(i).applicationInfo.loadLabel(context.getPackageManager()));

        TextView app_package_name = new TextView(context);
        app_package_name.setPadding(5, 3, 5, 5);
        app_package_name.setText( packageInfo.get(i).packageName);

        textLayout.addView(app_display_name);
        textLayout.addView(app_package_name);

        linearLayout.addView(textLayout);
        return linearLayout;
    }
}
