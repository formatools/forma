package com.stepango.forma.feature


data class PluginConfigurationWrapper<T : Any>(val pluginName: String, val configuration: T)

interface BuildFeature {

    fun validate(): Unit

}