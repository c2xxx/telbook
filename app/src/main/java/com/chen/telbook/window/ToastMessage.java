package com.chen.telbook.window;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.libchen.Logger;
import com.chen.telbook.MyApplication;
import com.chen.telbook.R;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.helper.TelBookXmlHelper;
import com.chen.telbook.utils.ImageGlide;

import java.util.Map;

/**
 * Created by ChenHui on 2016/12/21.
 */

public class ToastMessage {
    ViewHolder viewHolder;

    private ToastMessage() {
    }

    private static ToastMessage instance;

    public static ToastMessage getInstance() {
        if (instance == null) {
            synchronized (ToastMessage.class) {
                if (instance == null) {
                    instance = new ToastMessage();
                }
            }
        }
        return instance;
    }

    public void showNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            dismiss();
            return;
        }
        number = number.replace(" ", "").replace("-", "");
//        ToastUtil.show("显示号码" + number);
        createFloatView(MyApplication.getContext());
        show(true);
        if (viewHolder != null) {
            String img = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3912390486,749487084&fm=58";
            Map<String, TelNum> map = TelBookXmlHelper.loadLocolData();
            TelNum telNum = map.get(number);
            if (telNum == null) {
                dismiss();
            } else {
                viewHolder.show(telNum.getImg(), telNum.getName(), number);
            }
        }
    }

    private static class ViewHolder {
        View root;
        TextView tvName;
        TextView tvNumber;
        ImageView ivImg;

        private void init(View view) {
            root = view;
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvNumber = (TextView) view.findViewById(R.id.tvNumber);
            ivImg = (ImageView) view.findViewById(R.id.ivImg);
//            initMoveListener();
        }

        private void show(String img, String name, String number) {
            ImageGlide.show(MyApplication.getContext(), img, ivImg);
            tvName.setText(name);
            tvNumber.setText(number);
        }

        private void setVisible(boolean isShow) {
            root.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }

        /**
         * 对windowManager进行设置
         *
         * @return
         */
        public WindowManager.LayoutParams getParams() {
            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
            //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
            //wmParams.type = LayoutParams.TYPE_PHONE;
            //wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            //设置图片格式，效果为背景透明
            wmParams.format = PixelFormat.RGBA_8888;
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            //wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            //设置可以显示在状态栏上
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

            //设置悬浮窗口长宽数据
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.x = 1000;

            return wmParams;
        }
    }

    private void createFloatView(Context context) {
        if (viewHolder != null) {
            return;
        }
        View footbar = View.inflate(context, R.layout.view_toast_message, null);
        ViewHolder vh = new ViewHolder();
        vh.init(footbar);
        footbar.setTag(vh);
        footbar.setClickable(false);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        footbar.setVisibility(View.GONE);
        footbar.setAlpha(0.8f);
        try {
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission(android.Manifest.permission.SYSTEM_ALERT_WINDOW, context.getPackageName()));
            if (permission) {
                wm.addView(footbar, vh.getParams());
                viewHolder = vh;
            } else {
                Logger.d("木有这个权限 SYSTEM_ALERT_WINDOW");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e);
        }
    }

    public void dismiss() {
//        ToastUtil.show("结束显示号码");
        show(false);
    }

    private void show(boolean isShow) {
        if (viewHolder != null) {
            viewHolder.setVisible(isShow);
        }
    }
}