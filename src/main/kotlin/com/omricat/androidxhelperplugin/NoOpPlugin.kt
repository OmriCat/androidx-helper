package com.omricat.androidxhelperplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class NoOpPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // This Plugin class only exists to make it possible to apply the plugin in the plugins block
    }
}
