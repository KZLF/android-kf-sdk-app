package com.m7.imkfsdk.view.imageviewer;

import android.view.View;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2/4/21
 *     @desc   :
 *     @version: 1.0
 * </pre>
 */
public final class MoorViewHelper {
    private MoorViewHelper() {
    }

    public static void setScrollY(View view, int scrollY) {
        if (MoorAnimatorProxy.NEEDS_PROXY) {
            MoorAnimatorProxy.wrap(view).setScrollY(scrollY);
        } else {
            Honeycomb.setScrollY(view, scrollY);
        }
    }

    private static final class Honeycomb {
        static float getAlpha(View view) {
            return view.getAlpha();
        }

        static void setAlpha(View view, float alpha) {
            view.setAlpha(alpha);
        }

        static float getPivotX(View view) {
            return view.getPivotX();
        }

        static void setPivotX(View view, float pivotX) {
            view.setPivotX(pivotX);
        }

        static float getPivotY(View view) {
            return view.getPivotY();
        }

        static void setPivotY(View view, float pivotY) {
            view.setPivotY(pivotY);
        }

        static float getRotation(View view) {
            return view.getRotation();
        }

        static void setRotation(View view, float rotation) {
            view.setRotation(rotation);
        }

        static float getRotationX(View view) {
            return view.getRotationX();
        }

        static void setRotationX(View view, float rotationX) {
            view.setRotationX(rotationX);
        }

        static float getRotationY(View view) {
            return view.getRotationY();
        }

        static void setRotationY(View view, float rotationY) {
            view.setRotationY(rotationY);
        }

        static float getScaleX(View view) {
            return view.getScaleX();
        }

        static void setScaleX(View view, float scaleX) {
            view.setScaleX(scaleX);
        }

        static float getScaleY(View view) {
            return view.getScaleY();
        }

        static void setScaleY(View view, float scaleY) {
            view.setScaleY(scaleY);
        }

        static float getScrollX(View view) {
            return view.getScrollX();
        }

        static void setScrollX(View view, int scrollX) {
            view.setScrollX(scrollX);
        }

        static float getScrollY(View view) {
            return view.getScrollY();
        }

        static void setScrollY(View view, int scrollY) {
            view.setScrollY(scrollY);
        }

        static float getTranslationX(View view) {
            return view.getTranslationX();
        }

        static void setTranslationX(View view, float translationX) {
            view.setTranslationX(translationX);
        }

        static float getTranslationY(View view) {
            return view.getTranslationY();
        }

        static void setTranslationY(View view, float translationY) {
            view.setTranslationY(translationY);
        }

        static float getX(View view) {
            return view.getX();
        }

        static void setX(View view, float x) {
            view.setX(x);
        }

        static float getY(View view) {
            return view.getY();
        }

        static void setY(View view, float y) {
            view.setY(y);
        }
    }
}
