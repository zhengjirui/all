package com.lechuang.shengxinyoupin.view.defineView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 查看图片。如果是小尺寸，就原尺寸；如果大于布局，就缩放到布局
 */
public class OriginalImageView extends View {
    public OriginalImageView(Context context) {
        super(context);
        init();
    }

    public OriginalImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OriginalImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public OriginalImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setBackgroundColor(0x80000000);
        canvasRect = new RectF();
    }

    private Rect bitmapRect;
    private RectF canvasRect;

    private Bitmap bitmap;

    /**
     * 设置图片并显示
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        setVisibility(View.VISIBLE);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            if (bitmapWidth <= canvasWidth && bitmapHeight <= canvasHeight) {
                canvas.drawBitmap(bitmap, (canvasWidth - bitmapWidth) / 2, (canvasHeight - bitmapHeight) / 2, null);
            } else {
                float s = Math.min(canvasWidth / (float) bitmapWidth, canvasHeight / (float) bitmapHeight);
                canvasRect.set((canvasWidth - bitmapWidth * s) / 2, (canvasHeight - bitmapHeight * s) / 2,
                        (canvasWidth + bitmapWidth * s) / 2, (canvasHeight + bitmapHeight * s) / 2);
            }
            canvas.drawBitmap(bitmap, bitmapRect, canvasRect, null);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.GONE) {
            bitmap = null;
        }
    }
}
