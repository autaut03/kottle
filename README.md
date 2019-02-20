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
        classpath group: 'net.minecraftforge.gradle', name: 'ForgeGradle', version: '3.+', changing: true
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}
apply plugin: 'net.minecraftforge.gradle'
apply plugin: 'kotlin'

repositories {
    maven { url 'https://minecraft.curseforge.com/api/maven/' }
}

dependencies {
    compile "kottle:Kottle:$kottleVersion"
}
```
, in your `gradle.properties`:
```
# This is your kotlin gradle plugin version. For now, use 1.3.11.
kotlinVersion = 1.3.11

# For now, use 1.0.4.
kottleVersion = 1.0.4
```
, in your `mods.toml`:
```toml
modLoader="kotlinfml"
loaderVersion="[1,)"
```

if you prefer using version 1.3.21, in your `build.gradle`:
```groovy
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath(group: 'net.minecraftforge.gradle', name: 'ForgeGradle', version: '3.+', changing: true) {
            exclude group: 'trove', module: 'trove'
        }
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}
```
, in your `gradle.properties`
```
# This is your kotlin gradle plugin version. For now, use 1.3.21.
kotlinVersion = 1.3.21

# For now, use 1.0.4.
kottleVersion = 1.0.4
```

Then download Kottle from [here](https://minecraft.curseforge.com/projects/kottle/files) and drop it into your `run/mods`
folder of MDK. Create the folder if it doesn't exist.

Finally, replace `FMLJavaModLoadingContext` references in your code with `FMLKotlinModLoadingContext` and
`Mod.EventBusSubcriber` with `KotlinEventBusSubcriber`. For more info, checkout test sources 
[here](https://github.com/autaut03/kottle/tree/master/src/test/kotlin/net/alexwells/kottle) or my mod,
Roomery, [here](https://github.com/autaut03/roomery).
