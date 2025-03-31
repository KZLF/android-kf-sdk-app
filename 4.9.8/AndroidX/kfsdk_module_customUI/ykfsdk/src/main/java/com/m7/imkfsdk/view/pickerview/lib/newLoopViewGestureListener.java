package com.m7.imkfsdk.view.pickerview.lib;

import android.view.MotionEvent;

final class newLoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    final newWheelView loopView;

    newLoopViewGestureListener(newWheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        loopView.scrollBy(velocityY);
        return true;
    }
}
