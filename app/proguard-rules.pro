# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#### Nav and model args used for navigate
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

#### OkHttp, Retrofit and Moshi
-dontwarn okhttp3.**
-dontwarn retrofit2.Platform$Java8
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

#### DataStore
-keepclassmembers class ** extends com.google.protobuf.GeneratedMessageLite** {
   <fields>;
   public static ** parseFrom(java.io.InputStream);
   public static ** getDefaultInstance();
}

#### Local, Network and Arguments classes.
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.core.local.model.**
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.core.local.model.**$*
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.core.network.remote.response.**
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.core.network.remote.response.**$*
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.feature.**.*Args
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.feature.**.*Args$*
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.feature.**.*Args
-keepnames @kotlin.Metadata class com.vmh.mvvmjetpackcompose.feature.**.*Args$*

-keep class com.vmh.mvvmjetpackcompose.core.local.model.** { *; }
-keep class com.vmh.mvvmjetpackcompose.core.local.model.**$* { *; }
-keep class com.vmh.mvvmjetpackcompose.core.network.remote.response.** { *; }
-keep class com.vmh.mvvmjetpackcompose.core.network.remote.response.**$* { *; }
-keep class com.vmh.mvvmjetpackcompose.feature.**.*Args { *; }
-keep class com.vmh.mvvmjetpackcompose.feature.**.*Args$* { *; }
-keep class com.vmh.mvvmjetpackcompose.feature.**.*Args { *; }
-keep class com.vmh.mvvmjetpackcompose.feature.**.*Args$* { *; }

-keepclassmembers class com.vmh.mvvmjetpackcompose.core.local.model.** { *; }
-keepclassmembers class com.vmh.mvvmjetpackcompose.core.local.model.**$* { *; }
-keepclassmembers class com.vmh.mvvmjetpackcompose.core.network.remote.response.** { *; }
-keepclassmembers class com.vmh.mvvmjetpackcompose.core.network.remote.response.**$* { *; }
-keepclassmembers class com.vmh.mvvmjetpackcompose.feature.**.*Args { *; }
-keepclassmembers class com.vmh.mvvmjetpackcompose.feature.**.*Args$* { *; }
-keepclassmembers class com.vmh.mvvmjetpackcompose.feature.**.*Args { *; }
-keepclassmembers class com.vmh.mvvmjetpackcompose.feature.**.*Args$* { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#### Java 8
-keepnames @kotlin.Metadata class j$.time.**
-keepnames @kotlin.Metadata class j$.time.**$*
-keep class j$.time.** { *; }
-keep class j$.time.**$* { *; }
-keepclassmembers class j$.time.** { *; }
-keepclassmembers class j$.time.**$* { *; }

#### Firebase
-keep public class com.google.firebase.** { *; }
-keep class com.google.android.gms.internal.** { *; }

#### https://github.com/google/dagger/issues/4323#issuecomment-2319413509
-keepclasseswithmembers,includedescriptorclasses class * {
   @dagger.internal.KeepFieldType <fields>;
}
