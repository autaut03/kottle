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

repositories {
	maven { url 'https://minecraft.curseforge.com/api/maven/' }
}

dependencies {
	compile "kottle:Kottle:$kottleVersion"
}
```
, in your `gradle.properties`:
```
# This is your kotlin gradle plugin version. Doesn't matter that much, but
# preferably keep it even with one used in Kottle's gradle.properties (found on github)
kotlinVersion = 1.3.21

# And that's the version of Kottle itself. 
kottleVersion = 1.0.0
```
, in your `mods.toml`:
```toml
modLoader="kotlinfml"
loaderVersion="[1,)"
```

Finally, replace `FMLJavaModLoadingContext` references in your code with `FMLKotlinModLoadingContext`. For more info, check
out test sources [here](https://github.com/autaut03/kottle/tree/master/src/test/kotlin/net/alexwells/kottle) or my mod,
Roomery, [here](https://github.com/autaut03/roomery).
