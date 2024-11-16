package net.cacheux.nvp.app.utils

import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvplib.data.PenResultData

fun PenResultData.penInfos() = PenInfos(model, serial)