package com.stepango.blockme.root.library.clock

import com.stepango.blockme.common.util.clock.Clock

class AppClock : Clock {

    override fun currentTimeMillis(): Long =
        System.currentTimeMillis()
}