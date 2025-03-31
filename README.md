注: 此 SDK 包含 AndroidX 和 Android Support 版本.
- kfsdk_module_AAR 依赖的 SDK 皆为已编译的版本, 无法自定义 UI
- kfsdk_module_customUI 可以在 module ykfsdk 中自定义UI

注: 仓库依赖 jitpack

```gradle
repositories {
  mavenCentral()
  maven { url 'https://jitpack.io' }
}
```

如需在您的 App 中单独集成 SDK, 请直接安装 (注: 该版本无法自定义UI, 只能使用SDK默认)

Android Support 版本

```gradle
implementation 'com.github.kzlf:android-ykf-sdk:version'
```

AndroidX 版本

```gradle
implementation 'com.github.kzlf:androidX-ykf-sdk:version'
```


更详细的文档使用教程请参考仓库中 [安卓云客服SDK开发文档.pdf](https://github.com/KZLF/android-kf-sdk-demo/blob/main/安卓云客服SDK开发文档.pdf)
