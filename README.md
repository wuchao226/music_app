# music_app
组件化架构实现 网易云音乐 app
## 基础组件介绍
> lib_net_work：网络请求库，基于okhttp完成API数据请求

> lib_image_loader：图片加载库，基于glide完成图片加载

> lib_video：视频加载库，完成视频流加载

> lib_base：公共逻辑库，重点完成各模块接口对外暴露

> lib_audio：音频播放库，完成歌曲的加载，播放，缓存，下载，事件分发，UI等核心功能

> lib_pullalive：保活库，基于jobServices的保活库，提升app的存活率

> lib_update：下载更新库，完成apk安装包下载及自动更新，FileProvider保证文件私有

> lib_share：分享库，完成文本，图片，音乐分享到第三方平台

> lib_common_ui：公共UI库，封装项目中所有的自定义view

> lib_qrcode：扫码库，提供扫码和生成二维码功能

> lib_webview：webview库，完成webView的加载，缓存，重定向等功能
