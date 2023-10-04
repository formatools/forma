package tools.forma.sample.root.library.clock

import tools.forma.sample.common.util.clock.Clock

class AppClock : Clock {

    override fun currentTimeMillis(): Long =
        System.currentTimeMillis()
}