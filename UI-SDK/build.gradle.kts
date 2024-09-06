import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").reader())

val sonatypeUsername = localProperties.getProperty("sonatypeUsername")
val sonatypePassword = localProperties.getProperty("sonatypePassword")

android {
    namespace = "com.cookieinformation.mobileconsents.sdk.ui"
    compileSdk = 34

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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }


    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    //implementation ("androidx.fragment:fragment-ktx:1.6.2")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    //  from sdk //
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("com.cookieinformation:core:0.0.12")
}

publishing {
    publications {

        create<MavenPublication>("androidLib") {
            groupId = "com.cookieinformation"
            artifactId = "mobileconsents"
            version = "3.0.0"

            afterEvaluate {
                from(components["release"])
            }

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
                        id.set("HannaCookieInformation")
                        name.set("Hanna Shulman")
                        email.set("hsc@cookieinformation.com")
                    }
                }
                scm {
                    connection.set("git@github.com:cookie-information/android-release.git")
                    developerConnection.set("git@github.com:cookie-information/android-release.git")
                    url.set("https://github.com/cookie-information/android-release")
                }
            }

            repositories {
                maven {
                    // The name of the repository (can be anything, for reference only)
                    name = "Local"
                    // Specify the file path to the local Maven repository
                    // Change the path to where you want to publish your library locally
                    url = uri("file://${System.getProperty("user.home")}/.m2/repository")
                }

                maven {
                    name = "Sonatype"
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = sonatypeUsername
                        password = sonatypePassword
                    }
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    useInMemoryPgpKeys(
        findProperty("signing.keyId") as String?,
        findProperty("signing.secretKey") as String?,
        findProperty("signing.password") as String?,
    )
    sign(publishing.publications)
}


 