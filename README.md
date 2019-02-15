# Kottle
Kotlin language provider for Forge 1.13.2+. Originally a rewrite of [Shadowfacts's Forgelin](https://github.com/shadowfacts/Forgelin).

- Shades the Kotlin standard library, runtime, and reflect libraries so you don't have to.
- Provides a Forge `IModLanguageProvider` for using Kotlin `object` classes as your main mod class and adds support for
`object` instances for `@Mod.EventBusSubscriber`

## Usage
Set up your default Kotlin dev environment (IDEA can help you with that), then in your `build.gradle`:
```groovy
repositories {
	jcenter()
}

dependencies {
	compile 'net.alexwells:kottle:1.0.0'
}
```
Then in your `mods.toml`:
```toml
modLoader="kotlinfml"
loaderVersion="[1,)"
```

Finally, replace `FMLJavaModLoadingContext` references in your code with `FMLKotlinModLoadingContext`. For more info, check
out test sources [here](https://github.com/autaut03/kottle/tree/master/src/test/kotlin/net/alexwells/kottle).
