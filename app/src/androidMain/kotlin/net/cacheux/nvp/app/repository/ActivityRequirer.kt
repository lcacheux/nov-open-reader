package net.cacheux.nvp.app.repository

import android.app.Activity

interface ActivityRequirer {
    fun setActivity(activity: Activity?)
}
