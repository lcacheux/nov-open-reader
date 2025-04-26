package net.cacheux.nvp.app.repository

import net.cacheux.nvplib.StopCondition

fun interface StopConditionProvider {
    suspend fun getStopCondition(): StopCondition
}
