# Gujarati Fitness App — ProGuard/R8 Rules
# Applied during release builds for code shrinking and obfuscation

# ─── Kotlin Serialization ───────────────────────────────────
# Keep @Serializable classes and their generated serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all @Serializable data classes in our app
-keep,includedescriptorclasses class com.gujaratifitness.app.data.model.**$$serializer { *; }
-keepclassmembers class com.gujaratifitness.app.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.gujaratifitness.app.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep repository request/response classes
-keep,includedescriptorclasses class com.gujaratifitness.app.data.repository.**$$serializer { *; }
-keepclassmembers class com.gujaratifitness.app.data.repository.** {
    *** Companion;
}
-keepclasseswithmembers class com.gujaratifitness.app.data.repository.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ─── Ktor ───────────────────────────────────────────────────
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# ─── Supabase ───────────────────────────────────────────────
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# ─── Koin ───────────────────────────────────────────────────
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# ─── Voyager ────────────────────────────────────────────────
-keep class cafe.adriel.voyager.** { *; }
-dontwarn cafe.adriel.voyager.**

# ─── SQLDelight ─────────────────────────────────────────────
-keep class app.cash.sqldelight.** { *; }
-dontwarn app.cash.sqldelight.**

# ─── Coil ───────────────────────────────────────────────────
-keep class coil3.** { *; }
-dontwarn coil3.**

# ─── Coroutines ─────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ─── General Kotlin ─────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
