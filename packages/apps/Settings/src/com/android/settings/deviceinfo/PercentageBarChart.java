/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.RectF;
import com.android.settings.R;

import java.util.Collection;

/**
 * Draws a horizontal bar chart with colored slices, each represented by
 * {@link Entry}.
 */
public class PercentageBarChart extends View {
    private final Paint mEmptyPaint = new Paint();

    private Collection<Entry> mEntries;

    private int mMinTickWidth = 1;

    public static class Entry implements Comparable<Entry> {
        public final int order;
        public final float percentage;
        public final Paint paint;

        protected Entry(int order, float percentage, Paint paint) {
            this.order = order;
            this.percentage = percentage;
            this.paint = paint;
            /*HQ_xupeixin at 2015-09-17 modified for antialias effect in chart begin*/
            this.paint.setAntiAlias(true);
            /*HQ_xupeixin at 2015-09-17 modified  end*/
        }

        @Override
        public int compareTo(Entry another) {
            return order - another.order;
        }
    }

    public PercentageBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentageBarChart);
        mMinTickWidth = a.getDimensionPixelSize(R.styleable.PercentageBarChart_minTickWidth, 1);
        int emptyColor = a.getColor(R.styleable.PercentageBarChart_emptyColor, Color.BLACK);
        a.recycle();

        mEmptyPaint.setColor(emptyColor);
        mEmptyPaint.setStyle(Paint.Style.FILL);
        /*HQ_xupeixin at 2015-09-17 modified for antialias effect in chart begin*/
        mEmptyPaint.setAntiAlias(true);
        /*HQ_xupeixin at 2015-09-17 modified  end*/
    }
	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int left = getPaddingLeft();
        final int right = getWidth() - getPaddingRight();
        final int top = getPaddingTop();
        final int bottom = getHeight() - getPaddingBottom();

        final int width = right - left;

        final boolean isLayoutRtl = isLayoutRtl();
        if (isLayoutRtl) {
            float nextX = right;

            if (mEntries != null) {
                for (final Entry e : mEntries) {
                    final float entryWidth;
                    if (e.percentage == 0.0f) {
                        entryWidth = 0.0f;
                    } else {
                        entryWidth = Math.max(mMinTickWidth, width * e.percentage);
                    }

                    final float lastX = nextX - entryWidth;
                    if (lastX < left) {
                        canvas.drawRect(left, top, nextX, bottom, e.paint);
                        return;
                    }

                    canvas.drawRect(lastX, top, nextX, bottom, e.paint);
                    nextX = lastX;
                }
            }

            canvas.drawRect(left, top, nextX, bottom, mEmptyPaint);
        } else {
            float lastX = left;
			//modify by HQ_zhouguo at 20150914 start
			int i = 0;
            if (mEntries != null) {
                for (final Entry e : mEntries) {
                    final float entryWidth;
                    if (e.percentage == 0.0f) {
                        entryWidth = 0.0f;
                    } else {
                        entryWidth = Math.max(mMinTickWidth, width * e.percentage);
                    }

                    final float nextX = lastX + entryWidth;
                    if (nextX > right) {
                        canvas.drawRect(lastX, top, right, bottom, e.paint);
                        return;
                    }
                    if(i == 0){
						float cx = nextX;
						float cy = (bottom - top)/2;
						RectF oval2 = new RectF(lastX, top, 2*(nextX - lastX), bottom);

						if(cx <= 25){
							canvas.drawArc(oval2, 90, 180, true, e.paint);
						}else{
							RectF oval3 = new RectF(lastX, top, 50, bottom);
							canvas.drawArc(oval3, 90, 180, true, e.paint);
							canvas.drawRect(25, top, nextX >= right - 25 ? right - 25 : nextX, bottom, e.paint);
						}
					}else{
						canvas.drawRect(lastX, top, nextX, bottom, e.paint);
					}
					i ++;
                    lastX = nextX;
                }
            }

            canvas.drawRect(lastX, top, right - 25, bottom, mEmptyPaint);
			RectF oval4 = new RectF(right - 50, top, right, bottom);
			canvas.drawArc(oval4, -90, 180, true, mEmptyPaint);
			//modify by HQ_zhouguo at 20150914 end
        }
    }

    /**
     * Sets the background for this chart. Callers are responsible for later
     * calling {@link #invalidate()}.
     */
    @Override
    public void setBackgroundColor(int color) {
        mEmptyPaint.setColor(color);
    }

    /**
     * Adds a new slice to the percentage bar chart. Callers are responsible for
     * later calling {@link #invalidate()}.
     * 
     * @param percentage the total width that
     * @param color the color to draw the entry
     */
    public static Entry createEntry(int order, float percentage, int color) {
        final Paint p = new Paint();
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        return new Entry(order, percentage, p);
    }

    public void setEntries(Collection<Entry> entries) {
        mEntries = entries;
    }
}
