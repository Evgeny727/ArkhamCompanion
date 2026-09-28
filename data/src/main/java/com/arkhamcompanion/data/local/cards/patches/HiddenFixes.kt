package com.arkhamcompanion.data.local.cards.patches

internal fun loadHiddenFixesPatches(): List<CardPatch> = listOf(
    CardPatch(
        code = "01000",
        values = EntityPatch(
            hidden = PatchValue.Set(false),
        )
    ),
    CardPatch(
        code = "03325",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03325b"),
        )
    ),
    CardPatch(
        code = "03325b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03326a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03326b"),
        )
    ),
    CardPatch(
        code = "03326b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03326c",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03326d"),
        )
    ),
    CardPatch(
        code = "03326d",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03327a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03327b"),
        )
    ),
    CardPatch(
        code = "03327b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03327c",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03327d"),
        )
    ),
    CardPatch(
        code = "03327d",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03327e",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03327f"),
        )
    ),
    CardPatch(
        code = "03327f",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03328a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03328b"),
            hidden = PatchValue.Set(false),
        )
    ),
    CardPatch(
        code = "03328b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03328c",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03328d"),
        )
    ),
    CardPatch(
        code = "03328d",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03328e",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03328f"),
        )
    ),
    CardPatch(
        code = "03328f",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03329a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03329b"),
        )
    ),
    CardPatch(
        code = "03329b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03329c",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03329d"),
        )
    ),
    CardPatch(
        code = "03329d",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03330",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03330b"),
        )
    ),
    CardPatch(
        code = "03330b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "03331",
        values = EntityPatch(
            backLinkId = PatchValue.Set("03331b"),
        )
    ),
    CardPatch(
        code = "03331b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "04325",
        values = EntityPatch(
            backLinkId = PatchValue.Set("04325b"),
            hidden = PatchValue.Set(false),
        )
    ),
    CardPatch(
        code = "04325b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "04326",
        values = EntityPatch(
            backLinkId = PatchValue.Set("04326b"),
            hidden = PatchValue.Set(false),
        )
    ),
    CardPatch(
        code = "04326b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "04206b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "05085",
        values = EntityPatch(
            backLinkId = PatchValue.Set("05085b"),
            hidden = PatchValue.Set(false),
        )
    ),
    CardPatch(
        code = "05085b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "06015b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "07292a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("07292b"),
            hidden = PatchValue.Set(false),
        )
    ),
    CardPatch(
        code = "07292b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "07300",
        values = EntityPatch(
            backLinkId = PatchValue.Set("07300b"),
            hidden = PatchValue.Set(false),
        )
    ),
    CardPatch(
        code = "07300b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10584b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10593b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10594b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10595b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10596b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10597b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10598b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10599b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "10665b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "11068b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "11559b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "50026b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "50032b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "51026",
        values = EntityPatch(
            backLinkId = PatchValue.Set("51026b"),
        )
    ),
    CardPatch(
        code = "51026b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "83022a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("83022b"),
        )
    ),
    CardPatch(
        code = "83022b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "83023a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("83023b"),
        )
    ),
    CardPatch(
        code = "83023b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "83024a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("83024b"),
        )
    ),
    CardPatch(
        code = "83024b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "83025a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("83025b"),
        )
    ),
    CardPatch(
        code = "83025b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "83026a",
        values = EntityPatch(
            backLinkId = PatchValue.Set("83026b"),
        )
    ),
    CardPatch(
        code = "83026b",
        values = EntityPatch(
            backLinkId = PatchValue.Set(null),
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "87037b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "87036b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "88032b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
    CardPatch(
        code = "90066b",
        values = EntityPatch(
            hidden = PatchValue.Set(true),
        )
    ),
)