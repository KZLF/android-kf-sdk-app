package com.m7.imkfsdk.chat.listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @ClassName CheckRobotViewTouchListener
 * @Description 切换机器人view TouchListener
 * @Author jiangbingxuan
 * @Date 2023/3/10 13:47
 * @Version 1.0
 */
public class CheckRobotViewTouchListener implements View.OnTouchListener {

    /**
     * 按下时的位置控件相对屏幕左上角的位置X
     */
    private int startDownX;
    /**
     * 按下时的位置控件距离屏幕左上角的位置Y
     */
    private int startDownY;
    /**
     * 控件相对屏幕左上角移动的位置X
     */
    private int lastMoveX;
    /**
     * 控件相对屏幕左上角移动的位置Y
     */
    private int lastMoveY;

    private boolean isIntercept = false;

    /**
     * 父布局
     */
    private RelativeLayout rl_container;

    public CheckRobotViewTouchListener(RelativeLayout rl_container) {
        this.rl_container = rl_container;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startDownX = lastMoveX = (int) event.getRawX();
                startDownY = lastMoveY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastMoveX;
                int dy = (int) event.getRawY() - lastMoveY;

                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;
                int right = v.getRight() + dx;
                int bottom = v.getBottom() + dy;
                //防止超出父布局边界
                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }
                if (right > rl_container.getWidth()) {
                    right = rl_container.getWidth();
                    left = right - v.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }
                if (bottom > rl_container.getHeight()) {
                    bottom = rl_container.getHeight();
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                lastMoveX = (int) event.getRawX();
                lastMoveY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //当抬起手指的时候如果在中间则需忘两边靠拢
                int left2 = v.getLeft();
                int right2 = v.getRight();
//                if (v.getLeft() + v.getWidth() / 2 >= rl_container.getWidth() / 2) {
                //超过一半的时候靠右
                right2 = rl_container.getWidth();
                left2 = right2 - v.getWidth();
//                }
//                else if (v.getLeft() + v.getWidth() / 2 < rl_container.getWidth() / 2) {
//                    //小于一半的时候靠左
//                    left2 = 0;
//                    right2 = left2 + v.getWidth();
//                }
                int lastMoveDx = Math.abs((int) event.getRawX() - startDownX);
                int lastMoveDy = Math.abs((int) event.getRawY() - startDownY);

                if (lastMoveDx > 5 || lastMoveDy > 5) {
                    //滑动为0像素太苛刻,不容易触发点击事件
//                if (0 != lastMoveDx || 0 != lastMoveDy) {
                    isIntercept = true;
                } else {
                    isIntercept = false;
                }
                startFloatAnim(v, left2);//执行靠拢动画
                break;
        }
        return isIntercept;
    }

    private void startFloatAnim(final View v, int endLeft) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(v.getLeft(), endLeft);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                v.layout(animatedValue, v.getTop(), animatedValue + v.getWidth(), v.getBottom());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 每次移动都要设置其layout，不然由于父布局可能嵌套listview，当父布局发生改变冲毁（如下拉刷新时）则移动的view会回到原来的位置
                RelativeLayout.LayoutParams lpFeedback = new RelativeLayout.LayoutParams(
                        v.getWidth(), v.getHeight());
                lpFeedback.leftMargin = v.getLeft();
                lpFeedback.topMargin = v.getTop();
                v.setLayoutParams(lpFeedback);
            }
        });
        valueAnimator.start();
    }
}
