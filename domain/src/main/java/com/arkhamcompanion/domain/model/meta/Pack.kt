package com.arkhamcompanion.domain.model.meta

data class Pack(
    val code: String,
    val cycleCode: String,
    val name: String,
    val cycleName: String,
    val position: Int,
    val cyclePosition: Int,
    val official: Boolean,
    val reprint: Boolean,
    val chapter: Int?,
)
