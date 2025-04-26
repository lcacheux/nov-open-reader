package net.cacheux.nvp.app.repository

import net.cacheux.nvplib.DataReader

interface LoadablePenInfoRepository: PenInfoRepository {
    fun loadData(dataLoader: DataReader)
}
