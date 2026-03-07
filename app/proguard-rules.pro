# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ── Gson ──────────────────────────────────────────────────────────────────────
# Gson uses reflection on generic types; keep data models and TypeToken
-keepattributes Signature
-keepattributes *Annotation*

# Keep all data model classes used with Gson
-keep class com.example.sbtv.data.model.** { *; }

# Gson specific classes
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# ── OkHttp ────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ── VLC ───────────────────────────────────────────────────────────────────────
-keep class org.videolan.** { *; }
-dontwarn org.videolan.**

# ── ExoPlayer / Media3 ───────────────────────────────────────────────────────
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ── Coil ──────────────────────────────────────────────────────────────────────
-dontwarn coil.**

# ── General ───────────────────────────────────────────────────────────────────
# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Compose runtime stability
-keep class androidx.compose.** { *; }