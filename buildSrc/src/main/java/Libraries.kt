// Makes sense here due to libraries coordinates.
@file:Suppress("ObjectPropertyNaming")

object Libraries {
  object Android {
    const val GradlePlugin = "com.android.tools.build:gradle:4.0.2"

    const val ApplicationPluginId = "com.android.application"

    const val LibraryPluginId = "com.android.library"
  }

  object Kotlin {
    const val Version = "1.4.10"

    const val GradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$Version"

    const val StdLib = "org.jetbrains.kotlin:kotlin-stdlib:$Version"

    const val AndroidPluginId = "android"

    const val AndroidExtensionsPluginId = "android.extensions"

    const val KaptPluginId = "kapt"

    const val DokkaGradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:$Version"
  }

  object Coroutines {
    const val Version = "1.3.9"

    const val Core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$Version"
  }

  object Kotest {
    const val Version = "4.2.6"

    const val RunnerJunit5 = "io.kotest:kotest-runner-junit5-jvm:$Version"

    const val Assertions = "io.kotest:kotest-assertions-core-jvm:$Version"
  }

  object Detekt {
    const val Version = "1.14.1"

    const val GradlePluginId = "io.gitlab.arturbosch.detekt"

    const val GradlePlugin = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$Version"

    const val Formatting = "io.gitlab.arturbosch.detekt:detekt-formatting:$Version"

    const val Cli = "io.gitlab.arturbosch.detekt:detekt-cli:$Version"
  }

  object GradleVersions {
    const val Version = "0.33.0"

    const val GradlePluginId = "com.github.ben-manes.versions"

    const val GradlePlugin = "com.github.ben-manes:gradle-versions-plugin:$Version"
  }

  object Okhttp {
    const val Version = "4.9.0"

    const val Core = "com.squareup.okhttp3:okhttp:$Version"

    const val MockWebServer = "com.squareup.okhttp3:mockwebserver:$Version"
  }

  object Moshi {
    const val Version = "1.11.0"

    const val Core = "com.squareup.moshi:moshi:$Version"

    const val CodeGen = "com.squareup.moshi:moshi-kotlin-codegen:$Version"
  }

  @Suppress("ClassNaming")
  object AndroidX {

    object Version {
      const val Core = "1.3.1"

      const val AppCompat = "1.2.0"

      const val RecyclerView = "1.1.0"

      const val ConstraintLayout = "2.0.1"

      const val Fragment = "1.3.1"
    }

    const val Core = "androidx.core:core-ktx:${Version.Core}"

    const val AppCompat = "androidx.appcompat:appcompat:${Version.AppCompat}"

    const val RecyclerView = "androidx.recyclerview:recyclerview:${Version.RecyclerView}"

    const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:${Version.ConstraintLayout}"

    const val Fragment = "androidx.fragment:fragment-ktx:${Version.Fragment}"
  }

  object Design {
    const val Material = "com.google.android.material:material:1.2.1"
  }

  object Chucker {
    const val Core = "com.github.chuckerteam.chucker:library:3.3.0"
  }

  object MavenPublish {
    const val PluginId = "com.vanniktech.maven.publish"
  }

  @Suppress("ClassNaming")
  object MockK {
    const val Core = "io.mockk:mockk:1.10.6"
  }
}
