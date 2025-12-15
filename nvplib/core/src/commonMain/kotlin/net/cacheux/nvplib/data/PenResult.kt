package net.cacheux.nvplib.data

/**
 * Result returned once [net.cacheux.nvplib.NvpController] finished reading pen.
 */
sealed class PenResult {
    data class Success(val data: PenResultData): PenResult()
    data class Failure(val message: String): PenResult()
}

/**
 * Contains the full result of the pen reading.
 */
data class PenResultData(
    val model: String,
    val serial: String,
    val startTime: Long,
    val doseList: List<InsulinDose>
)
