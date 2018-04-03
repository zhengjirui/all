package com.lechuang.shengxinyoupin.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 圆角图片
 */
public class FilletBitmapDrawable extends Drawable {
    private Paint paintStroke = new Paint();
    private Paint paintBitmap = new Paint();
    private RectF canvasRectF = new RectF();
    private Rect bitmapRect = new Rect();
    private Bitmap bitmap;
    /**
     * 圆角的半径的比例
     */
    private float radiusRatio;

    /**
     * @param radiusRatio 圆角半径和画布宽带的比例 0~0.5
     */
    public FilletBitmapDrawable(Bitmap bitmap, float radiusRatio) {
        this.bitmap = bitmap;
        paintStroke.setAntiAlias(true);
        paintBitmap.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        this.radiusRatio = radiusRatio;
        int bitmapWith = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        bitmapRect.set((int) (bitmapWith * 0.05), (int) (bitmapHeight * 0.05), (int) (bitmapWith * 0.95), (int) (bitmapHeight * 0.95));
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        canvasRectF.set(getBounds());
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        System.out.println("PortraitDrawable.draw，canvas：" + canvas.getWidth() + "," + canvas.getHeight());
        if (bitmap != null) {
            canvas.saveLayer(canvasRectF, null, Canvas.ALL_SAVE_FLAG);
            float radius = canvas.getWidth() * radiusRatio;
            canvas.drawRoundRect(canvasRectF, radius, radius, paintStroke);
            canvas.drawBitmap(bitmap, bitmapRect, canvasRectF, paintBitmap);
        }
    }


    @Override
    public int getIntrinsicWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmap.getHeight();
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
