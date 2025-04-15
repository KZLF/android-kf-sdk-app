package com.m7.imkfsdk.utils;

/**
 * @ClassName OutlinedBulletSpan
 * @Description
 * @Author zhangheng
 * @Date 2024/12/17
 * @Version 1.0
 */
/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.BulletSpan;

/**
 * A span which styles paragraphs as bullet points (respecting layout direction).
 * <p>
 * BulletSpans must be attached from the first character to the last character of a single
 * paragraph, otherwise the bullet point will not be displayed but the first paragraph encountered
 * will have a leading margin.
 * <p>
 * BulletSpans allow configuring the following elements:
 * <ul>
 * <li><b>gap width</b> - the distance, in pixels, between the bullet point and the paragraph.
 * Default value is 2px.</li>
 * <li><b>color</b> - the bullet point color. By default, the bullet point color is 0 - no color,
 * so it uses the TextView's text color.</li>
 * <li><b>bullet radius</b> - the radius, in pixels, of the bullet point. Default value is
 * 4px.</li>
 * </ul>
 * For example, a BulletSpan using the default values can be constructed like this:
 * <pre>{@code
 *  SpannableString string = new SpannableString("Text with\nBullet point");
 *string.setSpan(new BulletSpan(), 10, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);}</pre>
 * <img src="{@docRoot}reference/android/images/text/style/defaultbulletspan.png" />
 * <figcaption>BulletSpan constructed with default values.</figcaption>
 * <p>
 * <p>
 * To construct a BulletSpan with a gap width of 40px, green bullet point and bullet radius of
 * 20px:
 * <pre>{@code
 *  SpannableString string = new SpannableString("Text with\nBullet point");
 *string.setSpan(new BulletSpan(40, color, 20), 10, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);}</pre>
 * <img src="{@docRoot}reference/android/images/text/style/custombulletspan.png" />
 * <figcaption>Customized BulletSpan.</figcaption>
 */
public class MoorBulletSpan extends BulletSpan {
    private int mRadius;
    private boolean mIsFill;
    public MoorBulletSpan(int gapWidth,int radius,boolean isFill) {
        super(gapWidth);
        mRadius = radius;
        mIsFill = isFill;
    }
    @Override
    public void drawLeadingMargin(@NonNull Canvas canvas, @NonNull Paint paint, int x, int dir, int top, int baseline, int bottom, @NonNull CharSequence text, int start, int end, boolean first, @Nullable Layout layout) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = paint.getStyle();
            paint.setStyle(mIsFill ? Paint.Style.FILL : Paint.Style.STROKE);

            final float yPosition = (top + bottom) / 2f - mRadius;
            final float xPosition = x + dir * mRadius;

            canvas.drawCircle(xPosition, yPosition, mRadius, paint);

            paint.setStyle(style);
        }
    }
}

