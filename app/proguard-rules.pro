# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details about R8/ProGuard visit:
#   https://developer.android.com/studio/build/shrink-code
#   https://developer.android.com/studio/build/shrink-code#r8
#
# DESCRIPTION:
# This file contains rules for the R8/ProGuard shrinker used when building
# release APKs (or debug/prod variants you configure to use minification).
# The shrinker performs three steps:
#  1) Shrinking (remove unused classes/methods/fields)
#  2) Optimization (bytecode optimizations)
#  3) Obfuscation (rename symbols to smaller names)
#
# Some libraries or reflection-based code rely on class names, method names,
# field names, annotations or generated code; if R8 strips or renames them you
# will get runtime crashes (ClassNotFound, NoSuchMethod, JSON serialization
# failures, Room queries failing, etc.). The rules below protect such code.
#
# HOW TO USE / CUSTOMIZE:
# - Start with these conservative rules to avoid runtime problems.
# - Once you have a working release build, iteratively tighten rules to get
#   better shrinking and obfuscation. Narrow the -keep rules to specific
#   packages (e.g., com.myapp.model.** ) instead of entire app or library
#   packages.
# - Always test assembleRelease and run the release APK on devices/emulators
#   to catch runtime issues. Use the generated mapping.txt to diagnose
#   obfuscated symbols and missing classes.
# - If you use other libraries (for example Gson, Moshi, protobuf, Realm,
#   kotlinx.serialization, etc.) add their recommended rules here.

# Keep Kotlin metadata & common attributes used by reflection and libraries
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, SourceFile, LineNumberTable
-keepclassmembers class kotlin.Metadata { *; }

# Keep parcelable creators so Android can re-create Parcelable objects
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# -----------------------------------------------------------------------------
# App packages and model/data/ui classes
# -----------------------------------------------------------------------------
# Why: models (data classes), DAOs, ViewModels, and UI components are often
# referenced by frameworks, serialization, or navigation graphs. If your
# project organizes code under different subpackages, adjust the rules below.
# Narrow -keep rules when you know exactly which packages require preservation.
# Keeping too many classes reduces shrinking effectiveness (warning from
# validator about "overly broad keep rule").
# Example: if your models live in com.example.app.domain.model -> use that

-keep class com.wahyuagast.keamanansisteminformasimobile.data.** { *; }
-keep class com.wahyuagast.keamanansisteminformasimobile.model.** { *; }
-keep class com.wahyuagast.keamanansisteminformasimobile.ui.** { *; }
-keepclassmembers class com.wahyuagast.keamanansisteminformasimobile.data.** { *; }

# -----------------------------------------------------------------------------
# Gson (Google) rules
# -----------------------------------------------------------------------------
# If you use Gson for JSON (field-based serialization), keep SerializedName
# annotated fields so JSON mapping still works after obfuscation.
-dontwarn com.google.gson.**
-keep class com.google.gson.stream.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# -----------------------------------------------------------------------------
# Moshi rules (if used)
# -----------------------------------------------------------------------------
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }

# -----------------------------------------------------------------------------
# Retrofit / OkHttp / Okio
# -----------------------------------------------------------------------------
# Keep Retrofit annotated interfaces' methods so the library's reflection
# and annotation processing can locate endpoints and method signatures.
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# If you use kotlinx.serialization converters or other converters, keep their
# runtime classes as needed. Replace or remove if you don't use them.
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class kotlinx.serialization.** { *; }

# -----------------------------------------------------------------------------
# Room (SQLite) rules - Entities, DAOs, and Database
# -----------------------------------------------------------------------------
# Room generates and uses classes and SQL at runtime. Keep entities and the
# RoomDatabase subclass. If you use Flow/LiveData return types, keep the
# methods returning Flow too. If you have specific packages for entities or
# DAOs, further narrow these rules.
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
# If you use Flow return types in DAOs, keep them (example keeps any method
# that returns kotlinx.coroutines.flow.Flow and takes any args).
-keepclassmembers class * {
    kotlinx.coroutines.flow.Flow *(...);
}

# -----------------------------------------------------------------------------
# WorkManager
# -----------------------------------------------------------------------------
# Keep WorkManager workers and classes invoked by reflection
-keep class androidx.work.** { *; }

# -----------------------------------------------------------------------------
# Dependency Injection (Dagger / Hilt)
# -----------------------------------------------------------------------------
# Dagger/Hilt generate many classes. We keep dagger packages and add dontwarn
# for javax.inject to avoid warnings in R8 when javax.inject is used only
# at compile time.
-keep class dagger.** { *; }
-dontwarn dagger.**
-keep class javax.inject.** { *; }
-dontwarn javax.inject.**
# Note: explicit @javax.inject.Inject member rules were removed to avoid
# validator warnings; Dagger/Hilt-generated components that we keep will
# transitively preserve needed classes.

# -----------------------------------------------------------------------------
# Timber (logging) - optional
# -----------------------------------------------------------------------------
-dontwarn timber.log.**

# -----------------------------------------------------------------------------
# AndroidX reflection points (SavedState, Navigation, Lifecycle)
# -----------------------------------------------------------------------------
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }

# -----------------------------------------------------------------------------
# OkHttp internal reflection uses
# -----------------------------------------------------------------------------
-keep class okhttp3.internal.platform.** { *; }

# -----------------------------------------------------------------------------
# Enums and annotation processor runtime types
# -----------------------------------------------------------------------------
# Enums sometimes used by serialization; keep their members (safe default)
-keepclassmembers enum * { *; }

# Keep moshi kotlin reflect helpers if you use Moshi reflection adapter
-keep class com.squareup.moshi.kotlin.reflect.** { *; }

# -----------------------------------------------------------------------------
# Core Android components
# -----------------------------------------------------------------------------
# Keep common app entry points so they are not stripped (Activity/Service/etc.)
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.fragment.app.Fragment

# Misc common - avoid removing classes referenced by reflection
-keepclassmembers class * {
    java.lang.Class class$(java.lang.String);
}

# -----------------------------------------------------------------------------
# Line number and source file options (optional)
# -----------------------------------------------------------------------------
# If you rely on line numbers for crash reports but still want obfuscation,
# you can keep SourceFile/LineNumberTable attributes. Uncomment to keep them:
#-keepattributes SourceFile,LineNumberTable
# If you want to hide original source file names from mapping-controlled
# builds uncomment the rename attribute:
#-renamesourcefileattribute SourceFile

# -----------------------------------------------------------------------------
# NOTES and TESTING guidance
# -----------------------------------------------------------------------------
# 1) Build and test: run `./gradlew assembleRelease` and install the release
#    APK on a test device/emulator. Verify core flows (login, network calls,
#    navigation, database reads/writes) work correctly in release mode.
# 2) Mapping: after a release build, inspect `app/build/outputs/mapping/release/mapping.txt`
#    to understand how names were obfuscated. Keep the mapping file secure
#    if you need to symbolize crash reports.
# 3) Iterative tightening: if you see many classes kept and the APK is large,
#    narrow the -keep rules by replacing broad packages with the exact
#    packages that require preservation (e.g., com.myapp.model.**).
# 4) Library rules: Some third-party libraries publish recommended ProGuard
#    rules (e.g., Firebase, Gson, Room). If you depend on them, add those
#    rules here (many Gradle dependencies already merge those rules
#    automatically).
# 5) Security note: ProGuard/R8 obfuscation is not a security boundary. It
#    raises reverse-engineering difficulty but does not prevent determined
#    attackers from recovering logic or strings; protect secrets on the
#    server-side and use runtime protections where needed.

# End of file
