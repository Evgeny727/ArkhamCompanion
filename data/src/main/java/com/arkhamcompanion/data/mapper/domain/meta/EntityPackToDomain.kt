package com.arkhamcompanion.data.mapper.domain.meta

import com.arkhamcompanion.data.local.meta.FullPackEntity
import com.arkhamcompanion.domain.model.meta.Pack

internal fun FullPackEntity.toDomain(): Pack = Pack(
    code = code,
    cycleCode = cycleCode,
    name = name,
    cycleName = cycleName,
    position = position,
    cyclePosition = cyclePosition,
    official = official,
    reprint = reprint,
    chapter = chapter,
)