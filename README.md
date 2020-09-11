AndroidX Gradle Helper Plugin
-----------------------------

A gradle plugin which is automatically generated from Google's maven repository which provides extension properties to make applying AndroidX dependencies easier.

```kotlin
build.gradle.kts

dependencies {
    implementation(androidx.lifecycle.lifecycleCommon("1.0-RC2"))
    implementation(androidx.fragment.fragmentKtx("$1.0.2"))
}
```
