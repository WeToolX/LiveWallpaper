package com.qiexi.livewallpaper.Service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MyWallpaperService extends WallpaperService {

    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    private int currentImgIndex = 0;

    @Override
    public Engine onCreateEngine() {
        // 加载壁纸图片
        loadWallpapers();
        return new CuteEngine();
    }

    /**
     * 从 Download/壁纸 文件夹中加载图片
     */
    private void loadWallpapers() {
        bitmapList.clear();
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "壁纸");
        if (!dir.exists() || !dir.isDirectory()) {
            Log.e("壁纸服务", "目录不存在: " + dir.getAbsolutePath());
            return;
        }

        // 筛选出数字开头且后缀为 jpg/png/jpeg 的文件
        File[] files = dir.listFiles((file, name) -> name.matches("^\\d+\\.(jpg|jpeg|png)$"));
        if (files != null && files.length > 0) {
            // 按数字顺序排序
            Arrays.sort(files, Comparator.comparingInt(f -> {
                String name = f.getName().split("\\.")[0];
                return Integer.parseInt(name);
            }));

            // 加载 Bitmap
            for (File f : files) {
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                if (bmp != null) {
                    bitmapList.add(bmp);
                    Log.d("壁纸服务", "加载图片: " + f.getName());
                }
            }
        }
    }

    /**
     * 根据索引获取当前图片
     */
    private Bitmap getBitmap(int index) {
        if (bitmapList.isEmpty()) return null;
        if (index < 0 || index >= bitmapList.size()) index = 0;
        return bitmapList.get(index);
    }

    class CuteEngine extends Engine {
        private final Paint paint = new Paint();
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean isFirstDraw = true;
        private float lastX;
        private final Runnable drawRunnable = this::drawFrame;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            setTouchEventsEnabled(true);
            showToast("壁纸引擎已创建");
        }

        /**
         * 手势监听：左右滑动切换壁纸
         */
        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastX = event.getX();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                float deltaX = event.getX() - lastX;
                if (Math.abs(deltaX) > 150) {
                    changeWallpaper(deltaX > 0 ? -1 : 1);
                }
            }
        }

        /**
         * 切换壁纸
         */
        private void changeWallpaper(int direction) {
            if (bitmapList.isEmpty()) return;

            currentImgIndex += direction;
            if (currentImgIndex >= bitmapList.size()) currentImgIndex = 0;
            if (currentImgIndex < 0) currentImgIndex = bitmapList.size() - 1;

            showToast("手动切换到壁纸：" + (currentImgIndex + 1));
            drawFrame();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) drawFrame();
            else handler.removeCallbacks(drawRunnable);
        }

        /**
         * 绘制当前壁纸
         */
        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);

                    Bitmap bg = getBitmap(currentImgIndex);
                    if (bg != null) {
                        int screenWidth = canvas.getWidth();
                        int screenHeight = canvas.getHeight();
                        int bitmapWidth = bg.getWidth();
                        int bitmapHeight = bg.getHeight();

                        float scale = Math.max((float) screenWidth / bitmapWidth,
                                (float) screenHeight / bitmapHeight);
                        float dx = (screenWidth - bitmapWidth * scale) / 2;
                        float dy = (screenHeight - bitmapHeight * scale) / 2;

                        Matrix matrix = new Matrix();
                        matrix.postScale(scale, scale);
                        matrix.postTranslate(dx, dy);

                        canvas.drawBitmap(bg, matrix, paint);

                        if (isFirstDraw) {
                            showToast("壁纸加载完成：" + (currentImgIndex + 1));
                            isFirstDraw = false;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("壁纸服务", "绘制失败", e);
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            handler.postDelayed(drawRunnable, 80);
        }

        /**
         * 显示 Toast 提示
         */
        private void showToast(String text) {
            try {
                handler.post(() ->
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
                );
                Log.d("壁纸服务", "showToast: " + text);
            } catch (Exception ignored) {}
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawRunnable);
        }
    }
}
