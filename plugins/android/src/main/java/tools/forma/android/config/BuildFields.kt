package tools.forma.android.config

interface BuildFields {
    val values: Map<String, Any>
}

class EmptyBuildFields : BuildFields {
    override val values: Map<String, Any> = emptyMap()
}

class MutableBuildFields : BuildFields {
    private val _values: MutableMap<String, Any> = mutableMapOf()
    override val values: Map<String, Any> = _values

    fun add(name: String, value: String): MutableBuildFields {
        _values[name] = value
        return this
    }

    fun add(name: String, value: Int): MutableBuildFields {
        _values[name] = value
        return this
    }

    fun add(name: String, value: Boolean): MutableBuildFields {
        _values[name] = value
        return this
    }
}

fun emptyBuildFields() = EmptyBuildFields()

fun buildFields() = MutableBuildFields()