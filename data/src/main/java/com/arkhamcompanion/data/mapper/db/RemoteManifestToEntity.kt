package com.arkhamcompanion.data.mapper.db

import com.arkhamcompanion.data.local.decks.DecksManifestEntity
import com.arkhamcompanion.data.remote.dto.DecksManifestDto

internal fun DecksManifestDto.toEntity(userId: String): DecksManifestEntity =
    DecksManifestEntity(
        version = version,
        arkhamdbSyncToken = arkhamdbSyncToken,
        userId = userId
    )