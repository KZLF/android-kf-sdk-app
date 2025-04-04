package com.m7.imkfsdk.view.pickerview.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.view.pickerview.listener.OnDismissListener;
import com.m7.imkfsdk.view.pickerview.utils.PickerViewAnimateUtil;

public class newBasePickerView {
    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
    );

    private final Context context;
    protected ViewGroup contentContainer;
    public ViewGroup decorView;//显示pickerview的根View,默认是activity的根view
    private ViewGroup rootView;//附加View 的 根View
    private View dialogView;//附加Dialog 的 根View

    protected int pickerview_timebtn_nor = 0xFF057dff;
    protected int pickerview_timebtn_pre = 0xFFc2daf5;
    protected int pickerview_bg_topbar = 0xFFf5f5f5;
    protected int pickerview_topbar_title = 0xFF000000;
    protected int bgColor_default = 0xFFFFFFFF;

    private OnDismissListener onDismissListener;
    private boolean dismissing;

    private Animation outAnim;
    private Animation inAnim;
    private boolean isShowing;
    private final int gravity = Gravity.BOTTOM;


    private Dialog mDialog;
    private boolean cancelable;//是否能取消

    protected View clickView;//是通过哪个View弹出的

    private boolean isAnim = true;
    public newBasePickerView(Context context) {
        this.context = context;

        /*initViews();
        init();
        initEvents();*/
    }

    protected void initViews(int backgroudId) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //如果是对话框模式
        dialogView =  layoutInflater.inflate(R.layout.ykfsdk_kf_layout_basepickerview, null);
        //这个是真正要加载时间选取器的父布局
        contentContainer = dialogView.findViewById(R.id.content_container);


//        if (isDialog()) {
//
//        } else {
//            //如果只是要显示在屏幕的下方
//            //decorView是activity的根View
//            if (decorView == null) {
//                decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
//            }
//            //将控件添加到decorView中
//            rootView = (ViewGroup) layoutInflater.inflate(R.layout.kf_layout_basepickerview, decorView, false);
//            rootView.setLayoutParams(new FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
//            ));
////            if (backgroudId != 0) {
////                rootView.setBackgroundColor(backgroudId);
////            }
//            // rootView.setBackgroundColor(ContextCompat.getColor(context,backgroudId));
//            //这个是真正要加载时间选取器的父布局
//            contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
//            contentContainer.setLayoutParams(params);
//        }
        setKeyBackCancelable(true);
    }

    protected void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    protected void initEvents() {
    }


    /**
     * @param v (是通过哪个View弹出的)
     * @param isAnim  是否显示动画效果
     */
    public void show(View v, boolean isAnim) {
        this.clickView = v;
        this.isAnim = isAnim;
        show();
    }

    public void show(boolean isAnim) {
        this.isAnim = isAnim;
        show();
    }

    public void show(View v) {
        this.clickView = v;
        show();
    }


    /**
     * 添加View到根视图
     */
    public void show() {
            showDialog();

    }


    /**
     * show的时候调用
     *
     * @param view 这个View
     */
    private void onAttached(View view) {
        decorView.addView(view);
        if(isAnim){
            contentContainer.startAnimation(inAnim);
        }
    }


    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {

            return false;


    }

    public void dismiss() {
        dismissDialog();

    }

    public void dismissImmediately() {

        decorView.post(new Runnable() {
            @Override
            public void run() {
                //从根视图移除
                decorView.removeView(rootView);
                isShowing = false;
                dismissing = false;
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(newBasePickerView.this);
                }
            }
        });


    }

    public Animation getInAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    public Animation getOutAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }

    public newBasePickerView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public void setKeyBackCancelable(boolean isCancelable) {

        View View;
        View = dialogView;


        View.setFocusable(isCancelable);
        View.setFocusableInTouchMode(isCancelable);
        if (isCancelable) {
            View.setOnKeyListener(onKeyBackListener);
        } else {
            View.setOnKeyListener(null);
        }
    }

    private final View.OnKeyListener onKeyBackListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_DOWN
                    && isShowing()) {
                dismiss();
                return true;
            }
            return false;
        }
    };

    protected newBasePickerView setOutSideCancelable(boolean isCancelable) {

        if (rootView != null) {
            View view = rootView.findViewById(R.id.outmost_container);

            if (isCancelable) {
                view.setOnTouchListener(onCancelableTouchListener);
            } else {
                view.setOnTouchListener(null);
            }
        }

        return this;
    }

    /**
     * 设置对话框模式是否可以点击外部取消
     *
     * @param cancelable
     */
    public void setDialogOutSideCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }


    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };

    public View findViewById(int id) {
        return contentContainer.findViewById(id);
    }

    protected void createDialog() {
        if (dialogView != null) {
            mDialog = new Dialog(context, R.style.ykfsdk_BottomDialog);
            mDialog.setContentView(dialogView);

//            mDialog.getWindow().setWindowAnimations(R.style.kf_pickerview_dialogAnim);
            Window dialogWindow = mDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
//            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//            lp.x = 0; // 新位置X坐标
//            lp.y = 0; // 新位置Y坐标
//            lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度
//            dialogView.measure(0, 0);
//            lp.height = dialogView.getMeasuredHeight();
//            lp.alpha = 9f; // 透明度
//            dialogWindow.setAttributes(lp);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (onDismissListener != null) {
                        onDismissListener.onDismiss(newBasePickerView.this);
                    }
                }
            });
            //给背景设置点击事件,这样当点击内容以外的地方会关闭界面
            dialogView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

    }

    public void showDialog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }



}
