package ads

import core.Updating
import executables.Executable
import logs.Logging
import staging.NotFoundStateValue
import staging.StateHandling
import kotlin.random.Random

private val adsRandom = 0..5

interface AdsRandomizer {

    fun executableList() : List<Executable>

    class Base(
        private val mUpdating: Updating,
        private val mStates: StateHandling,
        private val mAdsList: List<Executable>,
        private val mMainList: List<Executable>,
        private val mAdTimeout: Long
    ) : AdsRandomizer {

        override fun executableList(): List<Executable> {
            val lastAdTime = try { mStates.state(mUpdating).long("lastAdTime") } catch (e: NotFoundStateValue) { -1L }
            return if (System.currentTimeMillis() - lastAdTime >= mAdTimeout) {
                val randomNum = adsRandom.random(Random(System.currentTimeMillis()))
                if (randomNum == 3) {
                    mStates.state(mUpdating).editor(mStates).apply {
                        putLong("lastAdTime", System.currentTimeMillis())
                    }.commit()
                    mAdsList
                } else {
                    mMainList
                }
            } else {
                mMainList
            }
        }
    }
}