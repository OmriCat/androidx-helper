@file:Suppress("NOTHING_TO_INLINE")

class Arch internal constructor() {
    private val groupPrefix = "androidx.arch"
    val paging =  Paging()
    class Paging {

    }

    val core = Core(groupPrefix)
    class Core internal constructor(groupPrefix: String) {

        private val group = "$groupPrefix.core"
        private val artifactPrefix = "core-"

        private inline fun artifact(artifact: String, version: String) = "$group:$artifactPrefix$artifact:$version"

        fun common(version: String) = artifact("common", version)
        fun core(version: String) = artifact("core", version)

    }
}
