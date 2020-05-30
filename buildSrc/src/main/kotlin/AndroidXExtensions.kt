import org.gradle.api.artifacts.dsl.DependencyHandler

abstract class AndroidXDependencies internal constructor() {

    val arch = Arch()

    val test = object : Test() {}

}

val DependencyHandler.androidX: AndroidXDependencies get() = object : AndroidXDependencies() {}

