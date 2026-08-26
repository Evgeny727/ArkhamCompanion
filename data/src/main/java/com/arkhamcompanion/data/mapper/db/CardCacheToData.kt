package com.arkhamcompanion.data.mapper.db

import com.arkhamcompanion.data.local.cards.CardCacheData
import com.arkhamcompanion.data.objects.CardCache

fun CardCache.toData() = CardCacheData(
    traits = traits,
    actions = actions,
    properties = properties,
    skillBoosts = skillBoosts,
    uses = uses,
    slots = slots,
    tags = tags,
    requiredCards = requiredCards,
    sideDeckRequiredCards = sideDeckRequiredCards,
    restrictedTo = restrictedTo,
    advanced = advanced,
    replacement = replacement,
    parallelCards = parallelCards,
    parallel = parallel,
    base = base,
    duplicates = duplicates,
    reprints = reprints,
    level = level,
    bound = bound,
    bonded = bonded,
    fronts = fronts,
    backs = backs,
    otherVersions = otherVersions,
    basePrints = basePrints,
)