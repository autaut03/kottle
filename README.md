# Kottle
Kotlin language provider for Forge 1.13.2+. Originally a rewrite of [Shadowfacts's Forgelin](https://github.com/shadowfacts/Forgelin).

- Shades the Kotlin standard library, runtime, and reflect libraries so you don't have to.
- Provides a Forge `IModLanguageProvider` for using Kotlin `object` classes as your main mod class and adds support for
`object` instances for `@Mod.EventBusSubscriber`

## Usage
In your `build.gradle`:
```groovy
buildscript {
    repositories {
        jcenter()
    }
    
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}

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

# And that's the version of Kottle itself. 
# For Forge builds prior and including to 25.0.14, use 1.0.0.
# For anything newer, use 1.0.2
kottleVersion = 1.0.2
```
, in your `mods.toml`:
```toml
modLoader="kotlinfml"
loaderVersion="[1,)"
```

Finally, replace `FMLJavaModLoadingContext` references in your code with `FMLKotlinModLoadingContext`. For more info, check
out test sources [here](https://github.com/autaut03/kottle/tree/master/src/test/kotlin/net/alexwells/kottle) or my mod,
Roomery, [here](https://github.com/autaut03/roomery).
