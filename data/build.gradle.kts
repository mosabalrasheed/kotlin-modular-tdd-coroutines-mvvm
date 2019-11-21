plugins {
    id(Plugins.androidLibrary)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinAndroidExtension)
    kotlin(Plugins.kotlinKapt)
}

val javaVersion: JavaVersion by extra { JavaVersion.VERSION_1_8 }
val baseUrl: String by project

android {
    compileSdkVersion(extra["compileSdkVersion"] as Int)
    defaultConfig {
        minSdkVersion(extra["minSdkVersion"] as Int)
        targetSdkVersion(extra["targetSdkVersion"] as Int)
    }

    buildTypes {
        maybeCreate(BuildType.RELEASE).apply {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        maybeCreate(BuildType.DEBUG).apply {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    buildTypes.forEach {
        it.buildConfigField("String", "baseUrl", baseUrl)
    }

    sourceSets {
        named(SourceSets.MAIN).configure {
            java.srcDirs(file("src/main/kotlin"))
        }
        named(SourceSets.TEST).configure {
            java.srcDirs(file("src/test/kotlin"))
        }
        named(SourceSets.ANDROID_TEST).configure {
            java.srcDirs(file("src/androidTest/kotlin"))
        }
    }

    packagingOptions {
        pickFirst("mockito-extensions/org.mockito.plugins.MockMaker")
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    lintOptions {
        isAbortOnError = false
        isWarningsAsErrors = true
        isCheckDependencies = true
        isIgnoreTestSources = true
        setLintConfig(file("lint.xml"))
        disable("GradleDeprecated")
        disable("OldTargetApi")
        disable("GradleDependency")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    api(project(Modules.base))

    kapt(Dependencies.dagger_compiler)

    api(Dependencies.gson)
    api(Dependencies.retrofit)
    api(Dependencies.okHttp)
    api(Dependencies.logging)

    addTestDependencies()
}
