<h1 align="center">
  Kottle
  <br>
  <a href="https://travis-ci.com/autaut03/kottle"><img src="https://img.shields.io/travis/com/autaut03/kottle.svg?style=flat" alt="Build Status"/></a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/kottle"><img src="http://cf.way2muchnoise.eu/kottle.svg" alt="Downloads"/></a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/kottle/files"><img src="http://cf.way2muchnoise.eu/versions/kottle.svg" alt="Versions"/></a>
  <br>
  <img src="https://img.shields.io/badge/Kotlin-1.4.0-blue" alt="Kotlin Version"/>
  <img src="https://img.shields.io/badge/Kotlin_Coroutines-1.3.9-blue" alt="Kotlin Coroutines Version"/>
  <img src="https://img.shields.io/badge/JetBrains_Annotations-19.0.0-blue" alt="JetBrains Annotations Version"/>
</h1>


Kotlin language provider for Forge 1.13-1.16. Originally a rewrite of [Shadowfacts's Forgelin](https://github.com/shadowfacts/Forgelin).

- Shades the Kotlin standard library, runtime, and reflect libraries so you don't have to.
- Provides a Forge `IModLanguageProvider` for using Kotlin `object` classes as your main mod class and adds support for
`object` instances for `@Mod.EventBusSubscriber`
- Currently provides Kotlin libraries with version `1.4.0`, Kotlin coroutines version `1.3.9` and JetBrains annotations version `19.0.0`.

## Usage
First of all, make sure you're on Forge 31.0.0 or higher. 

**Optionally:** If you want to use Kotlin 1.4 features you'll have to use the 1.4.0 Kotlin gradle plugin, which requires
you to update your Gradle wrapper to use Gradle 5.6.4 (run `./gradlew wrapper --gradle-version 5.6.4`). This version of 
Gradle isn't officially supported by ForgeGradle, however it seems to work fine. If you don't need Kotlin 1.4 features
you're free to continue using the version of the Kotlin plugin you currently are using.

Then, in your `build.gradle`:
```groovy
buildscript {
    repositories {
        jcenter()
        maven { url "https://dl.bintray.com/kotlin/kotlin-eap" }
    }

    dependencies {
        // Remove this line: classpath group: 'net.minecraftforge.gradle', name: 'ForgeGradle', version: '3.+', changing: true
        // trove is forked and used by kotlin and we have to use it instead of the one forked by Forge
        // to avoid compilation errors on Linux (see PR #2)
        classpath(group: 'net.minecraftforge.gradle', name: 'ForgeGradle', version: '3.+', changing: true) {
            exclude group: 'trove', module: 'trove'
        }
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}

apply plugin: 'kotlin'

repositories {
    maven { url 'https://dl.bintray.com/kotlin/kotlin-eap' }
    maven { url 'https://minecraft.curseforge.com/api/maven/' }
}

dependencies {
    implementation "kottle:Kottle:$kottleVersion"
    // implementation "kottle:Kottle:$kottleVersion:slim" // if you want to pull in the kotlin libraries yourself
}
```
, in your `gradle.properties`:
```properties
# This is your kotlin gradle plugin version
kotlinVersion = 1.4.0

# Change this to the most recent release version from CurseForge
kottleVersion = 2.0.0
```
, in your `mods.toml`:
```toml
modLoader="kotlinfml"
loaderVersion="[2,)"
```

Finally, replace `FMLJavaModLoadingContext` references in your code with `FMLKotlinModLoadingContext`.

For more info, checkout test sources 
[here](https://github.com/autaut03/kottle/tree/master/src/test/kotlin/net/alexwells/kottle) or my mod,
Roomery, [here](https://github.com/autaut03/roomery).

## FAQ
- `addListener(SetupLifecycle::registerItems)` crashes the game:

  TL;DR: nothing that can be done about it. Use other methods. See https://github.com/autaut03/kottle/issues/8
