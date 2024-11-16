package net.cacheux.nvp.model

data class DoseGroupConfig(
    /**
     * Max delay between two doses to be considered in the same group (in seconds)
     */
    val groupDelay: Int = 60,

    /**
     * Ignore first values from the group when counting total, if they are equals or below this value.
     */
    val ignoreBelow: Int = 20,
)
