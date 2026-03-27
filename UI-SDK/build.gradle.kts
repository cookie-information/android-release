plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish")
    id("signing")
}

android {
    namespace = "com.cookieinformation.mobileconsents.sdk.ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {


    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    //  from sdk //
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    implementation(platform("androidx.compose:compose-bom:2026.03.01"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-compose:2.9.7")

    implementation("com.cookieinformation:core:1.0.0")
}

mavenPublishing {
    coordinates("com.cookieinformation", "mobileconsents", System.getenv("VERSION") ?: "3.1.0")

    publishToMavenCentral(automaticRelease = false)

    signAllPublications()
    pom {
        name.set("mobileconsents")
        description.set("SDK for easy user consent management.")
        url.set("https://cookie-information.github.io/mcs-docs/")
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("CookieInformation")
                name.set("Integration Team")
                email.set("integrations@piwik.pro")
            }
        }
        scm {
            connection.set("git@github.com:cookie-information/android-release.git")
            developerConnection.set("git@github.com:cookie-information/android-release.git")
            url.set("https://github.com/cookie-information/android-release")
        }
    }
}

if (System.getenv("CI") == null) {
    signing {
        useGpgCmd()
        sign(publishing.publications)
    }
}
