package net.cacheux.nvp.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.DoseGroupConfig

class DoseListUseCase(
    private val storageRepository: StorageRepository,
    private val preferencesRepository: PreferencesRepository
) {
    fun getDoseGroups(pen: String?): Flow<List<DoseGroup>> {
        return combine(
            storageRepository.getDoseList(pen),
            preferencesRepository.groupEnabled.content,
            preferencesRepository.groupDelay.content,
            preferencesRepository.autoIgnoreEnabled.content,
            preferencesRepository.autoIgnoreValue.content
        ) { doseList, groupEnabled, groupDelay, autoIgnoreEnabled, autoIgnoreValue ->
            if (groupEnabled) {
                DoseGroup.createDoseGroups(doseList, DoseGroupConfig(
                    groupDelay = groupDelay,
                    ignoreBelow = if (autoIgnoreEnabled) autoIgnoreValue * 10 else -1
                ))
            } else {
                doseList.sortedBy { it.time }.map { DoseGroup(listOf(it)) }
            }
        }
    }
}