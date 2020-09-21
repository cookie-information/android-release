import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.plugins.BasePlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id(Libraries.Detekt.GradlePluginId) version Libraries.Detekt.Version
  id(Libraries.GradleVersions.GradlePluginId) version Libraries.GradleVersions.Version
}

buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  dependencies {
    classpath(Libraries.AndroidGradlePlugin)
    classpath(Libraries.Kotlin.GradlePlugin)
    classpath(Libraries.Detekt.GradlePlugin)
    classpath(Libraries.GradleVersions.GradlePlugin)
  }
}

allprojects {
  repositories {
    mavenCentral()
    google()
    jcenter()
  }

  tasks.withType<Test> {
    testLogging {
      events("skipped", "failed", "passed")
    }
    useJUnitPlatform()
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf(
        "-progressive"
      )
    }
  }

  plugins.withType<BasePlugin> {
    extension.compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }

    extensions.findByType<BaseExtension>()?.apply {
      compileSdkVersion(Build.CompileSdk)
      buildToolsVersion(Build.BuildToolsVersion)

      defaultConfig {
        minSdkVersion(Build.MinSdk)
        targetSdkVersion(Build.TargetSdk)
      }
    }
  }
}

dependencies {
  detekt(Libraries.Detekt.Formatting)
  detekt(Libraries.Detekt.Cli)
}

tasks.withType<Detekt> {
  parallel = true
  config.setFrom(rootProject.file("detekt-config.yml"))
  setSource(files(projectDir))
  exclude(subprojects.map { "${it.buildDir.relativeTo(rootDir).path}/" })
  reports {
    xml {
      enabled = true
      destination = file("build/reports/detekt/detekt-results.xml")
    }
    html.enabled = false
    txt.enabled = false
  }
}

tasks.register("check") {
  group = "Verification"
  description = "Allows to attach Detekt to the root project."
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    isNonStable(candidate.version) && !isNonStable(currentVersion)
  }
}

fun isNonStable(version: String): Boolean {
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  return !regex.matches(version)
}