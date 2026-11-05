package com.arkhamcompanion.data.local.meta

data class FullPackEntity(
    val code: String,
    val cycleCode: String,
    val name: String,
    val cycleName: String,
    val position: Int,
    val cyclePosition: Int,
    val official: Boolean,
    val reprint: Boolean,
    val chapter: Int?
)
