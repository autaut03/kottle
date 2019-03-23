# Kottle
Kotlin language provider for Forge 1.13.2+. Originally a rewrite of [Shadowfacts's Forgelin](https://github.com/shadowfacts/Forgelin).

- Shades the Kotlin standard library, runtime, and reflect libraries so you don't have to.
- Provides a Forge `IModLanguageProvider` for using Kotlin `object` classes as your main mod class and adds support for
`object` instances for `@Mod.EventBusSubscriber`

## Usage
First of all, make sure you're on Forge 25.0.15 or higher.

Then, in your `build.gradle`:
```groovy
buildscript {
    repositories {
        jcenter()
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
    maven { url 'https://minecraft.curseforge.com/api/maven/' }
}

configurations {
    mod
}

dependencies {
    compile "kottle:Kottle:$kottleVersion"
    mod "kottle:Kottle:$kottleVersion"
}

task installMods(type: Copy, dependsOn: "deinstallMods") {
    from { configurations.mod }
    include "**/*.jar"
    into file("run/mods")
}

task deinstallMods(type: Delete) {
    delete fileTree(dir: "run/mods", include: "*.jar")
}

project.afterEvaluate {
    project.tasks['prepareRuns'].dependsOn(project.tasks['installMods'])
}
```
, in your `gradle.properties`:
```
# This is your kotlin gradle plugin version. For now, use 1.3.21.
kotlinVersion = 1.3.21

# For now, use 1.0.6.
kottleVersion = 1.0.6
```
, in your `mods.toml`:
```toml
modLoader="kotlinfml"
loaderVersion="[1,)"
```

Finally, replace `FMLJavaModLoadingContext` references in your code with `FMLKotlinModLoadingContext` and
`Mod.EventBusSubcriber` with `KotlinEventBusSubcriber`. For more info, checkout test sources 
[here](https://github.com/autaut03/kottle/tree/master/src/test/kotlin/net/alexwells/kottle) or my mod,
Roomery, [here](https://github.com/autaut03/roomery).
