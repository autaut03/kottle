# Kottle
[![Build Status](https://img.shields.io/travis/com/autaut03/kottle.svg?style=flat)](https://travis-ci.com/autaut03/kottle)
[![Downloads](http://cf.way2muchnoise.eu/kottle.svg)](https://www.curseforge.com/minecraft/mc-mods/kottle)
[![Versions](http://cf.way2muchnoise.eu/versions/kottle.svg)](https://www.curseforge.com/minecraft/mc-mods/kottle/files)

Kotlin language provider for Forge 1.13-1.15. Originally a rewrite of [Shadowfacts's Forgelin](https://github.com/shadowfacts/Forgelin).

- Shades the Kotlin standard library, runtime, and reflect libraries so you don't have to.
- Provides a Forge `IModLanguageProvider` for using Kotlin `object` classes as your main mod class and adds support for
`object` instances for `@Mod.EventBusSubscriber`
- Currently provides Kotlin libraries with version `1.4-M1`, Kotlin coroutines version `1.3.5` and JetBrains annotations version `19.0.0`.

## Usage
First of all, make sure you're on Forge 31.0.0 or higher.

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
}
```
, in your `gradle.properties`:
```
# This is your kotlin gradle plugin version
kotlinVersion = 1.4-M1

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
