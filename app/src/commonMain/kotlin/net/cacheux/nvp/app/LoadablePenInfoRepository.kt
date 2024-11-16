package net.cacheux.nvp.app

import net.cacheux.nvplib.DataReader

interface LoadablePenInfoRepository: PenInfoRepository {
    fun loadData(dataLoader: DataReader)
}
