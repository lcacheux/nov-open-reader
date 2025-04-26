package net.cacheux.nvp.app

import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvp.ui.testDateTime
import net.cacheux.nvplib.storage.DoseStorage

fun DoseStorage.insertData() {
    val pen1 = PenInfos(
        serial = "ABCD1234",
        model = "NovoPen 6"
    )

    val pen2 = PenInfos(
        serial = "ABCD5678",
        model = "NovoPen 6"
    )

    runBlocking {
        addDose(testDose(1, 12, 0, 0, 20), pen1)
        addDose(testDose(1, 12, 0, 10, 40), pen1)
        addDose(testDose(1, 12, 0, 20, 160), pen1)
        addDose(testDose(1, 12, 0, 40, 160), pen1)

        addDose(testDose(1, 12, 10, 0, 20), pen1)
        addDose(testDose(1, 12, 10, 10, 60), pen1)
        addDose(testDose(1, 12, 10, 20, 400), pen1)

        addDose(testDose(1, 12, 10, 30, 20), pen2)
        addDose(testDose(1, 12, 10, 40, 520), pen2)
    }
}

fun testDose(day: Int, hour: Int, minute: Int, second: Int, value: Int) = Dose(
    time = testDateTime(hour, minute, second, date = day),
    value = value
)
