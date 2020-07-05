plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    kotlin("jvm") version "1.3.72"
}

group = "com.zzj"
version = "1.2-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.promeg:tinypinyin:2.0.3") // TinyPinyin核心包，约80KB
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1.2"
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """修复补全窗口自动关闭的 bug"""
    )
}
