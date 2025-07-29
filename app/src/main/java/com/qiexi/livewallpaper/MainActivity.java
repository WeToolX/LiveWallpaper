package com.qiexi.livewallpaper;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qiexi.livewallpaper.Service.MyWallpaperService;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 检查并申请权限
        requestPermissionsIfNeeded();
        finish();
    }

    /**
     * 检查权限并申请
     */
    private void requestPermissionsIfNeeded() {
        // 需要的权限列表
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SET_WALLPAPER
        };

        // Android 13 (API 33+) 新增了 READ_MEDIA_IMAGES 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.SET_WALLPAPER
            };
        }

        // 检查权限是否已授权
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        // 没有授权则申请
        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        } else {
            // 权限已授予，直接启动动态壁纸设置
            startLiveWallpaperService();
        }
    }

    /**
     * 启动动态壁纸设置界面
     */
    private void startLiveWallpaperService() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, MyWallpaperService.class));
        startActivity(intent);
    }

    /**
     * 权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startLiveWallpaperService();
            } else {
                // 没有权限提示用户
                finish();
            }
        }
    }
}
