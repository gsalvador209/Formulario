package com.tanucode.formulario.utils

import android.content.Context

class AndroidStringProvider(private val context: Context) : StringProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}