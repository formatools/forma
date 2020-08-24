object Grip {
    private lateinit var _configuration: Configuration
    val configuration: Configuration get() = _configuration

    fun configure(configuration: Configuration) {
        _configuration = configuration
    }
}

