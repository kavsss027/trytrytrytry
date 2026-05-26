import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.File

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight.plugin)
}

kotlin {
    androidTarget()
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            runtimeOnly(compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material)
            implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.preview)
            
            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            // Koin DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Voyager Navigation
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.tab.navigator)
            implementation(libs.voyager.koin)

            // Coil Image & GIF
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.gif)

            // Supabase Client (via BOM)
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.auth)
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.storage)
            implementation(libs.supabase.functions)
            implementation(libs.supabase.compose.auth)

            // Lifecycle
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)

            // Coroutines & Serialization
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.gujaratifitness.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.gujaratifitness.app.database")
        }
    }
}

val generatedSourcesDir = layout.buildDirectory.dir("generated/config/src/commonMain/kotlin")

val generateBuildConfig by tasks.registering {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    
    val url = properties.getProperty("SUPABASE_URL") ?: ""
    val key = properties.getProperty("SUPABASE_ANON_KEY") ?: ""
    
    inputs.property("supabaseUrl", url)
    inputs.property("supabaseAnonKey", key)
    outputs.dir(generatedSourcesDir)
    
    doLast {
        val u = inputs.properties["supabaseUrl"] as String
        val k = inputs.properties["supabaseAnonKey"] as String
        val outDir = outputs.files.singleFile
        
        val buildConfigFile = File(outDir, "com/gujaratifitness/app/core/network/BuildConfig.kt")
        buildConfigFile.parentFile.mkdirs()
        buildConfigFile.writeText("""
            package com.gujaratifitness.app.core.network

            object BuildConfig {
                const val SUPABASE_URL = "$u"
                const val SUPABASE_ANON_KEY = "$k"
            }
        """.trimIndent())
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(generatedSourcesDir)
        }
    }
}

tasks.matching { it.name.startsWith("compile") || it.name.contains("Kotlin") }.configureEach {
    dependsOn(generateBuildConfig)
}

// Top level dependencies block deleted, moved to androidMain sourceSet dependencies