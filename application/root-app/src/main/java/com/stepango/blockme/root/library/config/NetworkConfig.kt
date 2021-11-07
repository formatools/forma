package com.stepango.blockme.root.library.config

import com.stepango.blockme.core.network.library.Config

class NetworkConfig : Config {

    override val baseUrl: String = "https://gateway.marvel.com/"
    override val privateApiKey: String = "d66d90c45623d9a1daeb9a398a7ded8268a9628f"
    override val publicApiKey: String = "34fc91a3d879f19895b515d8273965f9"
}