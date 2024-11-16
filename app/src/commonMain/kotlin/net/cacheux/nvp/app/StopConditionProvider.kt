package net.cacheux.nvp.app

import net.cacheux.nvplib.StopCondition

fun interface StopConditionProvider {
    suspend fun getStopCondition(): StopCondition
}
