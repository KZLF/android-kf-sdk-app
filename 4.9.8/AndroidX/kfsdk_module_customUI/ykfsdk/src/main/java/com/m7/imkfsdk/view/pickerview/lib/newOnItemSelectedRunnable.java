package com.m7.imkfsdk.view.pickerview.lib;

final class newOnItemSelectedRunnable implements Runnable {
    final newWheelView loopView;

    newOnItemSelectedRunnable(newWheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        loopView.onItemSelectedListener.onItemSelected(loopView.getCurrentItem());
    }
}
