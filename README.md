# LiveWallpaper

一个基于 Android 的动态壁纸服务项目，支持从 `/Download/壁纸/` 目录中自动加载带编号的图片（如 `1.jpg`、`2.png` 等），并支持手势切换壁纸。

## 📱 功能特色

- 支持设置为系统动态壁纸
- 自动加载 `Download/壁纸/` 文件夹中的图片资源
- 图片支持格式：`jpg` / `png` / `jpeg`
- 按图片名数字顺序排序显示
- 手势滑动切换图片（左右滑动切换壁纸）
- 自适应屏幕缩放居中显示
- 实时 Toast 提示当前壁纸编号

## 🧩 权限要求

项目运行需申请以下权限：

- `READ_EXTERNAL_STORAGE`（Android 12 及以下）或 `READ_MEDIA_IMAGES`（Android 13+）：读取壁纸目录下的图片
- `SET_WALLPAPER`：设置系统动态壁纸

Android 14+ 系统下，如果使用 `READ_MEDIA_IMAGES`，系统可能触发部分照片访问机制，建议用户授予完整权限或使用 SAF 方式手动选择目录。


## 🛠️ 运行要求

- Android Studio Giraffe 以上（建议 Hedgehog/Koala）
- Gradle 8.7+
- Android Gradle Plugin 8.1.0+
- 编译 SDK：34
- 最低支持：API 24（Android 7.0）

## 🚀 快速开始

1. 克隆项目到英文路径目录（避免中文路径）：
   git clone https://github.com/yourname/LiveWallpaper.git
2.添加图片资源至手机：
/sdcard/Download/壁纸/1.jpg
/sdcard/Download/壁纸/2.png
...
使用 Android Studio 打开项目，并构建运行。

第一次启动会请求权限，允许即可。

App 启动后将自动跳转至动态壁纸设置界面，点击“设置壁纸”即可启用。
本项目遵循 MIT 开源协议，欢迎使用和修改。
