package net.cacheux.nvp.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.InsulinUnit
import net.cacheux.nvp.model.IoB
import java.util.concurrent.TimeUnit
import org.apache.commons.math3.special.Gamma

class IoBUseCase(
    private val preferencesRepository: PreferencesRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // How fast insulin is absorbed (Novorapid: 1.5)
    val absorptionRate: Double = 1.5


    fun calculate(doseGroups: Flow<List<DoseGroup>>, time: Flow<Long>): Flow<IoB?> {
        return combine(
            doseGroups,
            time,
            preferencesRepository.groupIoB.content,
            preferencesRepository.insulinPeak.content.map { TimeUnit.MINUTES.toMillis(it.toLong()) },
            preferencesRepository.delta.content.map { TimeUnit.MINUTES.toMillis(it.toLong()) },
            preferencesRepository.insulinDuration.content.map { TimeUnit.HOURS.toMillis(it.toLong()) },
        ) { values ->
            val doseGroups = values[0] as List<DoseGroup>
            val time = values[1] as Long
            val enabled = values[2] as Boolean
            val insulinPeak = values[3] as Long
            val delta = values[4] as Long
            val insulinDuration = values[5] as Long

            if (!enabled) {
                return@combine null
            }

            val nextTime = time + delta

            val remaining = InsulinUnit(
                doseGroups
                    .sortedByDescending { it.getTime() }
                    .takeWhile { time - it.getTime() <= insulinDuration }
                    .sumOf {
                        (it.getTotal() * this.fraction(
                            time,
                            it.getTime(),
                            insulinPeak
                        )).toInt()
                    }
            )

            val next = InsulinUnit(
                doseGroups
                    .sortedByDescending { it.getTime() }
                    .takeWhile { nextTime - it.getTime() <= insulinDuration }
                    .sumOf {
                        (it.getTotal() * this.fraction(
                            nextTime,
                            it.getTime(),
                            insulinPeak
                        )).toInt()
                    }
            )

            IoB(
                time = time,
                remaining = remaining,
                serial = doseGroups.lastOrNull()?.getSerial() ?: "",
                current = remaining - next,
                delta = delta
            )
        }
    }

    fun fraction(
        time: Long,
        doseTime: Long,
        insulinPeak: Long,
    ): Double {
        val timeSinceBolus = time - doseTime
        val x = absorptionRate * timeSinceBolus / insulinPeak
        val r = Gamma.regularizedGammaQ(absorptionRate + 1, x)

        return r
    }
}