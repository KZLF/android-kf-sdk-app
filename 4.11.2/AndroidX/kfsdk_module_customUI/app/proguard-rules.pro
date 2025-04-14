
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes *DatabaseField*
-keepattributes *DatabaseTable*
-keepattributes *SerializedName*
-keep class **.R$* {*;}
-ignorewarnings


#**************************************imkf客服**必要配置****************************************
-dontwarn com.m7.imkfsdk.**
-keep class com.m7.imkfsdk.** { *; }
-dontwarn com.moor.imkf.**
-keep class com.moor.imkf.** { *; }
#软键盘
-dontwarn com.effective.android.panel.**
-keep class com.effective.android.panel.** { *; }

#---------依赖库------
#七牛
-dontwarn com.qiniu.**
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
# OkHttp3
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
#GSON
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {@com.google.gson.annotations.SerializedName <fields>;}
#eventbus
-dontwarn org.greenrobot.eventbus.**
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keepclassmembers class ** {
    public void onEvent*(**);
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
#glide
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
<init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

#OrmLite，sqlcipher
-keep class com.j256.** { *; }
-keep class com.j256.**
-keepclassmembers class com.j256.**
-keep enum com.j256.**
-keepclassmembers enum com.j256.**
-keep interface com.j256.**
-keepclassmembers interface com.j256.**
#-dontwarn net.sqlcipher.**
#-keep class net.sqlcipher.** {*;}
#**************************************imkf客服**必要配置**************************************












#**************************************imkf视频****************************************
#如果不使用视频功能并且没有添加视频依赖 可以注释此部分混淆
#如果使用视频功能  一定要配置这部分混淆
# React Native

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**
-keep,includedescriptorclasses class com.facebook.react.bridge.** { *; }
# WebRTC

-keep class org.webrtc.** { *; }
-dontwarn org.chromium.build.BuildHooksAndroid

# Jisti Meet SDK

-keep class org.jitsi.meet.** { *; }
-keep class org.jitsi.meet.sdk.** { *; }

# We added the following when we switched minifyEnabled on. Probably because we
# ran the app and hit problems...

-keep class com.facebook.react.bridge.CatalystInstanceImpl { *; }
-keep class com.facebook.react.bridge.ExecutorToken { *; }
-keep class com.facebook.react.bridge.JavaScriptExecutor { *; }
-keep class com.facebook.react.bridge.ModuleRegistryHolder { *; }
-keep class com.facebook.react.bridge.ReadableType { *; }
-keep class com.facebook.react.bridge.queue.NativeRunnable { *; }
-keep class com.facebook.react.devsupport.** { *; }

-dontwarn com.facebook.react.devsupport.**
-dontwarn com.google.appengine.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.servlet.**

# ^^^ We added the above when we switched minifyEnabled on.

# Rule to avoid build errors related to SVGs.
-keep public class com.horcrux.svg.** {*;}

# https://github.com/facebook/fresco/issues/2638
-keep public class com.facebook.imageutils.** {
   public *;
  }

#*************************************imkf视频****************************************



#*************************************bugly****************************************
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#*************************************bugly****************************************


#*************************************友盟****************************************
-keep class com.umeng.** { *; }

-keep class com.uc.** { *; }

-keep class com.efs.** { *; }

-keepclassmembers class *{
     public<init>(org.json.JSONObject);
}
-keepclassmembers enum *{
      publicstatic**[] values();
      publicstatic** valueOf(java.lang.String);
}
#*************************************友盟****************************************
