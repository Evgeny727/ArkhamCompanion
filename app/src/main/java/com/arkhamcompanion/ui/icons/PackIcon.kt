package com.arkhamcompanion.ui.icons

import androidx.compose.ui.text.font.FontFamily
import com.arkhamcompanion.ui.theme.EncounterIconsFont

enum class PackIcon(
    override val stringCode: String,
    override val code: Int
) : IconGlyph {
    Tic("tic", 0x0041),
    Dream("dream", 0x0042),
    TheCircleUndone("the_circle_undone", 0x0043),
    Rttcu("rttcu", 0x0044),
    TheForgottenAge("the_forgotten_age", 0x0045),
    ReturnToTheForgottenAge("return_to_the_forgotten_age", 0x0046),
    Carcosa("carcosa", 0x0047),
    ReturnToThePathToCarcosa("return_to_the_path_to_carcosa", 0x0048),
    Set("set", 0x0049),
    ReturnToTheDunwichLegacy("return_to_the_dunwich_legacy", 0x004A),
    Nate("nate", 0x004B),
    Harvey("harvey", 0x004D),
    Jacqueline("jacqueline", 0x004E),
    Winifred("winifred", 0x004F),
    Stella("stella", 0x0050),
    Rougerauo2("rougerauo2", 0x0051),
    Venice1("venice1", 0x0052),
    Lol("lol", 0x0053),
    Guardians("guardians", 0x0054),
    Excelsior("excelsior", 0x0055),
    BlobSet("blob_set", 0x0056),
    Rtnotz("rtnotz", 0x0058),
    Tdcp("tdcp", 0xE900),
    CourtOfTheAncients("court_of_the_ancients", 0xE901),
    TdcFlood("tdc_flood", 0xE902),
    ObsidianCanyons("obsidian_canyons", 0xE903),
    OneLastJob("one_last_job", 0xE904),
    Pilgrims("pilgrims", 0xE905),
    Rlyeh("rlyeh", 0xE906),
    SepulchreOfTheSleeper("sepulchre_of_the_sleeper", 0xE907),
    StarSpawn("star_spawn", 0xE908),
    Stowaways("stowaways", 0xE909),
    TheDoomOfArkhamPart2("the_doom_of_arkham_part_2", 0xE90A),
    TdcExpedition("tdc_expedition", 0xE90B),
    TheWesternWall("the_western_wall", 0xE90C),
    UnderseaCreatures("undersea_creatures", 0xE90D),
    Tdcc("tdcc", 0xE90E),
    TheDoomOfArkhamPart1("the_doom_of_arkham_part_1", 0xE90F),
    TheDrownedQuarter("the_drowned_quarter", 0xE910),
    TheApiary("the_apiary", 0xE911),
    Tasks("tasks", 0xE912),
    TheInescapable("the_inescapable", 0xE913),
    ElderMist("elder_mist", 0xE914),
    Dreams("dreams", 0xE915),
    Domination("domination", 0xE916),
    DeepOnes("deep_ones", 0xE917),
    CosmicLegacy("cosmic_legacy", 0xE918),
    AlienMachinery("alien_machinery", 0xE919),
    ZcxCircusExMortis("zcx_circus_ex_mortis", 0xE91A),
    ZcxOneNightOnly("zcx_one_night_only", 0xE91B),
    ZcxPrimrosePath("zcx_primrose_path", 0xE91C),
    ZcxHarmsWay("zcx_harms_way", 0xE91D),
    ZcxAllPointsWest("zcx_all_points_west", 0xE91E),
    ZcxPiperAtTheGatesOfDawn("zcx_piper_at_the_gates_of_dawn", 0xE91F),
    ZcxBacchanalia("zcx_bacchanalia", 0xE920),
    ZcxRedSunrise("zcx_red_sunrise", 0xE921),
    ZcxThousandToOne("zcx_thousand_to_one", 0xE922),
    ZcxChildrenOfTheGoat("zcx_children_of_the_goat", 0xE923),
    ZcxCircusGrounds("zcx_circus_grounds", 0xE924),
    ZcxCultOfShubNiggurath("zcx_cult_of_shub_niggurath", 0xE925),
    ZcxDestinyAndProphecy("zcx_destiny_and_prophecy", 0xE926),
    ZcxIllusoryTricks("zcx_illusory_tricks", 0xE927),
    ZcxLunaticNight("zcx_lunatic_night", 0xE928),
    ZcxNewMoonDaredevils("zcx_new_moon_daredevils", 0xE929),
    ZcxNewMoonEntertainers("zcx_new_moon_entertainers", 0xE92A),
    ZcxPanickedMasses("zcx_panicked_masses", 0xE92B),
    ZcxPrimordialEvils("zcx_primordial_evils", 0xE92C),
    ZcxSavageWoods("zcx_savage_woods", 0xE92D),
    TheGrandVault("the_grand_vault", 0xE92E),
    AgesUnwound("ages_unwound", 0xE92F),
    ANightOfFire("a_night_of_fire", 0xE930),
    AWorldTornDownAgain("a_world_torn_down_again", 0xE931),
    AWorldTornDown("a_world_torn_down", 0xE932),
    AYearToPlan("a_year_to_plan", 0xE933),
    AgentsOfAforgomon("agents_of_aforgomon", 0xE934),
    Missions("missions", 0xE935),
    Myriad("myriad", 0xE936),
    NightOfTheRitual("night_of_the_ritual", 0xE937),
    Nyctophobia("nyctophobia", 0xE938),
    Paradox("paradox", 0xE939),
    ShiftingReality("shifting_reality", 0xE93A),
    TheMyriadGentleman("the_myriad_gentleman", 0xE93B),
    Thugs("thugs", 0xE93C),
    TimeRunsOut("time_runs_out", 0xE93D),
    UnleashedChaos("unleashed_chaos", 0xE93E),
    UnravellingYears("unravelling_years", 0xE93F),
    Unstuck("unstuck", 0xE940),
    ArchaicEvils("archaic_evils", 0xE941),
    UnnaturalStone("unnatural_stone", 0xE942),
    UnfriendlyPorts("unfriendly_ports", 0xE943),
    HeartOfDarkness("heart_of_darkness", 0xE944),
    AfricaIsWatching("africa_is_watching", 0xE945),
    ToTheHeartOfTheCongo("to_the_heart_of_the_congo", 0xE946),
    TheAvatarOfDarkness("the_avatar_of_darkness", 0xE947),
    TheDarkness("the_darkness", 0xE948),
    LandsOfTheCongo("lands_of_the_congo", 0xE949),
    CultOfDarkness("cult_of_darkness", 0xE94A),
    AfricanWildlife("african_wildlife", 0xE94B),
    ArchitectsOfChaos("architects_of_chaos", 0xE94C),
    AwakenedMadness("awakened_madness", 0xE94D),
    Countermeasures("countermeasures", 0xE94E),
    CultOfCthulhu("cult_of_cthulhu", 0xE94F),
    DeepDreams("deep_dreams", 0xE950),
    GrandCompass("grand_compass", 0xE951),
    ShadowyAgents("shadowy_agents", 0xE952),
    SpawnOfRyleh("spawn_of_ryleh", 0xE953),
    StormAndSea("storm_and_sea", 0xE954),
    TombOfDeadDreams("tomb_of_dead_dreams", 0xE955),
    PyroclasticFlow("pyroclastic_flow", 0xE956),
    BloodFromStones("blood_from_stones", 0xE957),
    AcrossDreadfulWaters("across_dreadful_waters", 0xE958),
    CrumblingMasonry("crumbling_masonry", 0xE959),
    PrivateLives("private_lives", 0xE95A),
    GoingTwice("going_twice", 0xE95B),
    LostMoorings("lost_moorings", 0xE95C),
    Zcf("zcf", 0xE95D),
    Quito("quito", 0xE95E),
    Reykjavik("reykjavik", 0xE95F),
    SanJuan("san_juan", 0xE960),
    Arkham("arkham", 0xE961),
    Cairo("cairo", 0xE962),
    MonteCarlo("monte_carlo", 0xE963),
    NewOrleans("new_orleans", 0xE964),
    Zoz("zoz", 0xE965),
    TheRoadToOz("the_road_to_oz", 0xE966),
    DeepImpact("deep_impact", 0xE967),
    DoubleWhammy("double_whammy", 0xE968),
    ChasingRainbows("chasing_rainbows", 0xE969),
    MiseryLovesCompany("misery_loves_company", 0xE96A),
    HallOfTheMountainKing("hall_of_the_mountain_king", 0xE96B),
    DefenseOfTheRealm("defense_of_the_realm", 0xE96C),
    TrueColours("true_colours", 0xE96D),
    AlienVibrance("alien_vibrance", 0xE96E),
    BlightedLand("blighted_land", 0xE96F),
    ChromaticInfection("chromatic_infection", 0xE970),
    CompanionsOfOz("companions_of_oz", 0xE971),
    MunchkinCountry("munchkin_country", 0xE972),
    QuadlingCountry("quadling_country", 0xE973),
    WinkieCountry("winkie_country", 0xE974),
    GillikinCountry("gillikin_country", 0xE975),
    EmeraldCity("emerald_city", 0xE976),
    FerociousBeasts("ferocious_beasts", 0xE977),
    HorridInfection("horrid_infection", 0xE978),
    Nomes("nomes", 0xE979),
    PrincessOfOz("princess_of_oz", 0xE97A),
    PrismaticEvils("prismatic_evils", 0xE97B),
    SpiralingDecay("spiraling_decay", 0xE97C),
    TerrorOutOfSpace("terror_out_of_space", 0xE97D),
    TheColourItself("the_colour_itself", 0xE97E),
    ViolentInvasion("violent_invasion", 0xE97F),
    WickedWitches("wicked_witches", 0xE980),
    Venice("venice", 0xE981),
    YborCity("ybor_city", 0xE982),
    Bermuda2("bermuda_2", 0xE983),
    Perth("perth", 0xE984),
    Nairobi("nairobi", 0xE985),
    Manokwari("manokwari", 0xE986),
    Kabul("kabul", 0xE987),
    Tunguska("tunguska", 0xE988),
    Rome("rome", 0xE989),
    Kathmandu("kathmandu", 0xE98A),
    HongKong("hong_kong", 0xE98B),
    Bombay("bombay", 0xE98C),
    Havana("havana", 0xE98D),
    KualaLampur("kuala_lampur", 0xE98E),
    BuenosAires("buenos_aires", 0xE98F),
    Shanghai("shanghai", 0xE990),
    Lagos("lagos", 0xE991),
    Stockholm("stockholm", 0xE992),
    Tokyo("tokyo", 0xE993),
    Sydney("sydney", 0xE994),
    SanFrancisco("san_francisco", 0xE995),
    RioDeJaniero("rio_de_janiero", 0xE996),
    Moscow("moscow", 0xE997),
    Marrakesh("marrakesh", 0xE998),
    London("london", 0xE999),
    Constantinople("constantinople", 0xE99A),
    Bermuda("bermuda", 0xE99B),
    Anchorage("anchorage", 0xE99C),
    Alexandria("alexandria", 0xE99D),
    AgentsOfTheOutside("agents_of_the_outside", 0xE99E),
    BeyondTheBeyond("beyond_the_beyond", 0xE99F),
    AgentsOfYuggoth("agents_of_yuggoth", 0xE9A0),
    CleanupCrew("cleanup_crew", 0xE9A1),
    CongressOfTheKeys("congress_of_the_keys", 0xE9A2),
    CrimsonConspiracy("crimson_conspiracy", 0xE9A3),
    DancingMad("dancing_mad", 0xE9A4),
    DarkVeiling("dark_veiling", 0xE9A5),
    DeadHeat("dead_heat", 0xE9A6),
    DealingsInTheDark("dealings_in_the_dark", 0xE9A7),
    DogsOfWar("dogs_of_war", 0xE9A8),
    Globetrotting("globetrotting", 0xE9A9),
    MysteriesAbound("mysteries_abound", 0xE9AA),
    OnThinIce("on_thin_ice", 0xE9AB),
    Outsiders("outsiders", 0xE9AC),
    RedCoterie("red_coterie", 0xE9AD),
    RiddlesAndRain("riddles_and_rain", 0xE9AE),
    SanguineShadows("sanguine_shadows", 0xE9AF),
    WithoutATrace("without_a_trace", 0xE9B0),
    StrangeHappenings("strange_happenings", 0xE9B1),
    SpreadingCorruption("spreading_corruption", 0xE9B2),
    SpatialAnomaly("spatial_anomaly", 0xE9B3),
    ShadowOfADoubt("shadow_of_a_doubt", 0xE9B4),
    ShadesOfSorrow("shades_of_sorrow", 0xE9B5),
    SecretWar("secret_war", 0xE9B6),
    ScarletSorcery("scarlet_sorcery", 0xE9B7),
    Tskc("tskc", 0xE9B8),
    Fhvp("fhvp", 0xE9B9),
    Fhvc("fhvc", 0xE9BA),
    TheFirstDay("the_first_day", 0xE9BB),
    TheSecondDay("the_second_day", 0xE9BC),
    TheFinalDay("the_final_day", 0xE9BD),
    AgentsOfTheColour("agents_of_the_colour", 0xE9BE),
    Blight("blight", 0xE9BF),
    DayOfRain("day_of_rain", 0xE9C0),
    DayOfRest("day_of_rest", 0xE9C1),
    DayOfTheFeast("day_of_the_feast", 0xE9C2),
    FateOfTheVale("fate_of_the_vale", 0xE9C3),
    Fire("fire", 0xE9C4),
    Heirlooms("heirlooms", 0xE9C5),
    TheLostSister("the_lost_sister", 0xE9C6),
    WrittenInRock("written_in_rock", 0xE9C7),
    Transfiguration("transfiguration", 0xE9C8),
    TheVale("the_vale", 0xE9C9),
    TheTwistedHollow("the_twisted_hollow", 0xE9CA),
    TheThingInTheDepths("the_thing_in_the_depths", 0xE9CB),
    TheSilentHeath("the_silent_heath", 0xE9CC),
    TheLongestNight("the_longest_night", 0xE9CD),
    TheForest("the_forest", 0xE9CE),
    Residents("residents", 0xE9CF),
    Refractions("refractions", 0xE9D0),
    HemlockHouse("hemlock_house", 0xE9D1),
    Mutations("mutations", 0xE9D2),
    HorrorsInTheRock("horrors_in_the_rock", 0xE9D3),
    Myconids("myconids", 0xE9D4),
    Tsk("tsk", 0xE9D5),
    Penguins("penguins", 0xE9D6),
    SilenceAndMystery("silence_and_mystery", 0xE9D7),
    SleepingNightmares("sleeping_nightmares", 0xE9D8),
    StirringInTheDeep("stirring_in_the_deep", 0xE9D9),
    TekeliLi("tekeli_li", 0xE9DA),
    TheCrash("the_crash", 0xE9DB),
    TheGreatSeal("the_great_seal", 0xE9DC),
    Shoggoths("shoggoths", 0xE9DD),
    NamelessHorrors("nameless_horrors", 0xE9DE),
    Miasma("miasma", 0xE9DF),
    MemorialsOfTheLost("memorials_of_the_lost", 0xE9E0),
    LostInTheNight("lost_in_the_night", 0xE9E1),
    LeftBehind("left_behind", 0xE9E2),
    HazardsOfAntarctica("hazards_of_antarctica", 0xE9E3),
    ExpeditionTeam("expedition_team", 0xE9E4),
    ElderThings("elder_things", 0xE9E5),
    DeadlyWeather("deadly_weather", 0xE9E6),
    CreaturesInTheIce("creatures_in_the_ice", 0xE9E7),
    AgentsOfTheUnknown("agents_of_the_unknown", 0xE9E8),
    TheHeartOfMadness("the_heart_of_madness", 0xE9E9),
    CityOfTheElderThings("city_of_the_elder_things", 0xE9EA),
    ToTheForbiddenPeaks("to_the_forbidden_peaks", 0xE9EB),
    FatalMirage("fatal_mirage", 0xE9EC),
    IceAndDeath("ice_and_death", 0xE9ED),
    EoeCampaign("eoe_campaign", 0xE9EE),
    Eoe("eoe", 0xE9EF),
    WonderlandBoons("wonderland_boons", 0xE9F0),
    WarpedReality("warped_reality", 0xE9F1),
    WalrusAndCarpenter("walrus_and_carpenter", 0xE9F2),
    RiddlesAndGames("riddles_and_games", 0xE9F3),
    Jabberwock("jabberwock", 0xE9F4),
    GurathnakasShadows("gurathnakas_shadows", 0xE9F5),
    Chessmen("chessmen", 0xE9F6),
    CheshireCat("cheshire_cat", 0xE9F7),
    CardGuards("card_guards", 0xE9F8),
    AliceInArkham("alice_in_arkham", 0xE9F9),
    LucidNightmare("lucid_nightmare", 0xE9FA),
    WhiteQueen("white_queen", 0xE9FB),
    FoolsMate("fools_mate", 0xE9FC),
    LionAndUnicorn("lion_and_unicorn", 0xE9FD),
    SiblingRivalry("sibling_rivalry", 0xE9FE),
    HumptyDumpty("humpty_dumpty", 0xE9FF),
    WildSnarkChase("wild_snark_chase", 0xEA00),
    GryphonAndMockTurtle("gryphon_and_mock_turtle", 0xEA01),
    Duchess("duchess", 0xEA02),
    TempestInATeapot("tempest_in_a_teapot", 0xEA03),
    Caterpillar("caterpillar", 0xEA04),
    ASeaOfTroubles("a_sea_of_troubles", 0xEA05),
    Dodo("dodo", 0xEA06),
    ArkhamInWonderland("arkham_in_wonderland", 0xEA07),
    AliceInWonderland("alice_in_wonderland", 0xEA08),
    TheWarning("the_warning", 0xEA09),
    TheFallen("the_fallen", 0xEA0A),
    TheCrown("the_crown", 0xEA0B),
    RunicOaths("runic_oaths", 0xEA0C),
    Lacuna("lacuna", 0xEA0D),
    Hudulfolk("hudulfolk", 0xEA0E),
    GoldenCircle("golden_circle", 0xEA0F),
    GlacialMists("glacial_mists", 0xEA10),
    Draugar("draugar", 0xEA11),
    Zce("zce", 0xEA12),
    NightOnTheTown("night_on_the_town", 0xEA13),
    DeadByDawn("dead_by_dawn", 0xEA14),
    MourningStroll("mourning_stroll", 0xEA15),
    HighNoonDescent("high_noon_descent", 0xEA16),
    TheAfternoonWar("the_afternoon_war", 0xEA17),
    DeathAtSundown("death_at_sundown", 0xEA18),
    TheMidnightHour("the_midnight_hour", 0xEA19),
    AgencySurvivors("agency_survivors", 0xEA1A),
    AgentsOfCthugua("agents_of_cthugua", 0xEA1B),
    FallenArkham("fallen_arkham", 0xEA1C),
    GiftsOfThePlaguebearer("gifts_of_the_plaguebearer", 0xEA1D),
    HazeOfMiasma("haze_of_miasma", 0xEA1E),
    LifeAndDeath("life_and_death", 0xEA1F),
    TenuousAllies("tenuous_allies", 0xEA20),
    ThePlaguebearersCommands("the_plaguebearers_commands", 0xEA21),
    UnboundPower("unbound_power", 0xEA22),
    LateRisers("late_risers", 0xEA23),
    CallOfThePlaguebearer("call_of_the_plaguebearer", 0xEA24),
    InTheShadowOfEarth("in_the_shadow_of_earth", 0xEA25),
    TheBoogeyman("the_boogeyman", 0xEA26),
    InterstellarPredators("interstellar_predators", 0xEA27),
    HastursGaze("hasturs_gaze", 0xEA28),
    FragmentOfCarcosa("fragment_of_carcosa", 0xEA29),
    Endtimes("endtimes", 0xEA2A),
    Starfall("starfall", 0xEA2B),
    DeepSpace("deep_space", 0xEA2C),
    DarkPast("dark_past", 0xEA2D),
    ArtificialIntelligence("artificial_intelligence", 0xEA2E),
    Anachronism("anachronism", 0xEA2F),
    TheMachineInYellow("the_machine_in_yellow", 0xEA30),
    StrangeMoons("strange_moons", 0xEA31),
    LostQuantum("lost_quantum", 0xEA32),
    ElectricNightmare("electric_nightmare", 0xEA33),
    TheTatterdemalion("the_tatterdemalion", 0xEA34),
    WonderlandBanes("wonderland_banes", 0xEA35),
    BleedingHearts("bleeding_hearts", 0xEA36),
    Berserkers("berserkers", 0xEA37),
    IntoTheMaelstrom("into_the_maelstrom", 0xEA38),
    Rttic("rttic", 0xEA39),
    ReturnToThePitOfDespair("return_to_the_pit_of_despair", 0xEA3A),
    ReturnToTheVanishingOfElinaHarper("return_to_the_vanishing_of_elina_harper", 0xEA3B),
    ReturnToInTooDeep("return_to_in_too_deep", 0xEA3C),
    ReturnToDevilReef("return_to_devil_reef", 0xEA3D),
    ReturnToHorrorInHighGear("return_to_horror_in_high_gear", 0xEA3E),
    ReturnToALightInTheFog("return_to_a_light_in_the_fog", 0xEA3F),
    ReturnToTheLairOfDagon("return_to_the_lair_of_dagon", 0xEA40),
    ReturnToIntoTheMaelstrom("return_to_into_the_maelstrom", 0xEA41),
    BarricadedDoors("barricaded_doors", 0xEA42),
    Occultation("occultation", 0xEA43),
    InnsmouthHaze("innsmouth_haze", 0xEA44),
    ReturnToFloodedCaverns("return_to_flooded_caverns", 0xEA45),
    RollingTide("rolling_tide", 0xEA46),
    StalkersOfCthulhu("stalkers_of_cthulhu", 0xEA47),
    LairOfDagon("lair_of_dagon", 0xEA48),
    ALightInTheFog("a_light_in_the_fog", 0xEA49),
    HorrorInHighGear("horror_in_high_gear", 0xEA4A),
    DevilReef("devil_reef", 0xEA4B),
    InTooDeep("in_too_deep", 0xEA4C),
    AgentsOfDagon("agents_of_dagon", 0xEA4D),
    DisappearanceOfElinaHarper("disappearance_of_elina_harper", 0xEA4E),
    GrottoOfDespair("grotto_of_despair", 0xEA4F),
    CreaturesFromBelow("creatures_from_below", 0xEA50),
    RisingTide("rising_tide", 0xEA51),
    FloodedCaves("flooded_caves", 0xEA52),
    AgentsOfHydra("agents_of_hydra", 0xEA53),
    Locals("locals", 0xEA54),
    Malfunction("malfunction", 0xEA55),
    Syzygy("syzygy", 0xEA56),
    ShatteredMemories("shattered_memories", 0xEA57),
    FogOverInnsmouth("fog_over_innsmouth", 0xEA58),
    Zoogs("zoogs", 0xEA59),
    DescentIntoThePitch("descent_into_the_pitch", 0xEA5A),
    TerrorOfTheVale("terror_of_the_vale", 0xEA5B),
    WhispersOfHypnos("whispers_of_hypnos", 0xEA5C),
    WakingNightmare("waking_nightmare", 0xEA5D),
    Spiders("spiders", 0xEA5E),
    MergingRealities("merging_realities", 0xEA5F),
    Dreamlands("dreamlands", 0xEA60),
    DreamersCurse("dreamers_curse", 0xEA61),
    CreaturesOfTheUnderworld("creatures_of_the_underworld", 0xEA62),
    Corsairs("corsairs", 0xEA63),
    BeyondTheGatesOfSleep("beyond_the_gates_of_sleep", 0xEA64),
    AgentsOfNyarlathotep("agents_of_nyarlathotep", 0xEA65),
    WeaverOfTheCosmos("weaver_of_the_cosmos", 0xEA66),
    WhereGodsDwell("where_gods_dwell", 0xEA67),
    PointOfNoReturn("point_of_no_return", 0xEA68),
    DarkSideOfTheMoon("dark_side_of_the_moon", 0xEA69),
    AThousandShapesOfHorror("a_thousand_shapes_of_horror", 0xEA6A),
    TheSearchForKadath("the_search_for_kadath", 0xEA6B),
    AgentsOfAtlachNacha("agents_of_atlach_nacha", 0xEA6C),
    ColdFog("cold_fog", 0xEA6D),
    CityOfTheDamned("city_of_the_damned", 0xEA6E),
    BloodthirstySpirits("bloodthirsty_spirits", 0xEA6F),
    SpectralRealm("spectral_realm", 0xEA70),
    ThreateningEvil("threatening_evil", 0xEA71),
    UnspeakableFate("unspeakable_fate", 0xEA72),
    Witchwork("witchwork", 0xEA73),
    ReturnToBeforeTheBlackThrone("return_to_before_the_black_throne", 0xEA74),
    ReturnToInTheClutchesOfChaos("return_to_in_the_clutches_of_chaos", 0xEA75),
    ReturnToUnionAndDisillusion("return_to_union_and_disillusion", 0xEA76),
    ReturnToForTheGreaterGood("return_to_for_the_greater_good", 0xEA77),
    ReturnToTheWagesOfSin("return_to_the_wages_of_sin", 0xEA78),
    ReturnToTheSecretName("return_to_the_secret_name", 0xEA79),
    ReturnToAtDeathsDoorstep("return_to_at_deaths_doorstep", 0xEA7A),
    ReturnToTheWitchingHour("return_to_the_witching_hour", 0xEA7B),
    ReturnToDisappearanceAtTheTwilightEstate("return_to_disappearance_at_the_twilight_estate", 0xEA7C),
    MusicOfTheDamned("music_of_the_damned", 0xEA7D),
    SecretsOfTheUniverse("secrets_of_the_universe", 0xEA7E),
    Witchcraft("witchcraft", 0xEA7F),
    TrappedSpirits("trapped_spirits", 0xEA80),
    TheWatcher("the_watcher", 0xEA81),
    SpectralPredators("spectral_predators", 0xEA82),
    SilverTwilightLodge("silver_twilight_lodge", 0xEA83),
    RealmOfDeath("realm_of_death", 0xEA84),
    InexorableFate("inexorable_fate", 0xEA85),
    CityOfSins("city_of_sins", 0xEA86),
    AnettesCoven("anettes_coven", 0xEA87),
    AgentsOfAzathoth("agents_of_azathoth", 0xEA88),
    BeforeTheBlackThrone("before_the_black_throne", 0xEA89),
    InTheClutchesOfChaos("in_the_clutches_of_chaos", 0xEA8A),
    UnionAndDisillusion("union_and_disillusion", 0xEA8B),
    ForTheGreaterGood("for_the_greater_good", 0xEA8C),
    TheWagesOfSin("the_wages_of_sin", 0xEA8D),
    TheSecretName("the_secret_name", 0xEA8E),
    AtDeathsDoorstep("at_deaths_doorstep", 0xEA8F),
    TheWitchingHour("the_witching_hour", 0xEA90),
    DisappearanceAtTheTwilightEstate("disappearance_at_the_twilight_estate", 0xEA91),
    CultOfPnakotus("cult_of_pnakotus", 0xEA92),
    DoomedExpedition("doomed_expedition", 0xEA93),
    TemporalHunters("temporal_hunters", 0xEA94),
    VenomousHate("venomous_hate", 0xEA95),
    ReturnToTheRainforest("return_to_the_rainforest", 0xEA96),
    ReturnToTurnBackTime("return_to_turn_back_time", 0xEA97),
    ReturnToShatteredAeons("return_to_shattered_aeons", 0xEA98),
    ReturnToTheDepthsOfYoth("return_to_the_depths_of_yoth", 0xEA99),
    ReturnToCityOfArchives("return_to_city_of_archives", 0xEA9A),
    ReturnToTheHeartOfTheElders("return_to_the_heart_of_the_elders", 0xEA9B),
    ReturnToKnyan("return_to_knyan", 0xEA9C),
    ReturnToPillarsOfJudgement("return_to_pillars_of_judgement", 0xEA9D),
    ReturnToTheBoundaryBeyond("return_to_the_boundary_beyond", 0xEA9E),
    ReturnToThreadsOfFate("return_to_threads_of_fate", 0xEA9F),
    ReturnToTheDoomOfEztli("return_to_the_doom_of_eztli", 0xEAA0),
    ReturnToTheUntamedWilds("return_to_the_untamed_wilds", 0xEAA1),
    TemporalFlux("temporal_flux", 0xEAA2),
    Serpents("serpents", 0xEAA3),
    Rainforest("rainforest", 0xEAA4),
    Poison("poison", 0xEAA5),
    PnakoticBrotherhood("pnakotic_brotherhood", 0xEAA6),
    GuardiansOfTime("guardians_of_time", 0xEAA7),
    ForgottenRuins("forgotten_ruins", 0xEAA8),
    Expedition("expedition", 0xEAA9),
    DeadlyTraps("deadly_traps", 0xEAAA),
    AgentsOfYig("agents_of_yig", 0xEAAB),
    YigsVenom("yigs_venom", 0xEAAC),
    TurnBackTime("turn_back_time", 0xEAAD),
    ShatteredAeons("shattered_aeons", 0xEAAE),
    TheDepthsOfYoth("the_depths_of_yoth", 0xEAAF),
    CityOfArchives("city_of_archives", 0xEAB0),
    HeartOfTheElders("heart_of_the_elders", 0xEAB1),
    Knyan("knyan", 0xEAB2),
    PillarsOfJudgement("pillars_of_judgement", 0xEAB3),
    TheBoundaryBeyond("the_boundary_beyond", 0xEAB4),
    ThreadsOfFate("threads_of_fate", 0xEAB5),
    TheDoomOfEztli("the_doom_of_eztli", 0xEAB6),
    TheUntamedWilds("the_untamed_wilds", 0xEAB7),
    FinalAnnihilation("final_annihilation", 0xEAB8),
    SpiralingInferno("spiraling_inferno", 0xEAB9),
    ScorchedWasteland("scorched_wasteland", 0xEABA),
    MalevolentRitual("malevolent_ritual", 0xEABB),
    Gala("gala", 0xEABC),
    FilmFatale("film_fatale", 0xEABD),
    ForgottenIsland("forgotten_island", 0xEABE),
    FilmFataleEncounter("film_fatale_encounter", 0xEABF),
    CosmicJourney("cosmic_journey", 0xEAC0),
    AbominableContessa("abominable_contessa", 0xEAC1),
    WhenTheWorldScreamed("when_the_world_screamed", 0xEAC2),
    Onigawa("onigawa", 0xEAC3),
    Darkham("darkham", 0xEAC4),
    DepravedLegions("depraved_legions", 0xEAC5),
    LegionsOfFire("legions_of_fire", 0xEAC6),
    MysteriousBenefits("mysterious_benefits", 0xEAC7),
    AnythingOnce("anything_once", 0xEAC8),
    UnscrupulousInvestments("unscrupulous_investments", 0xEAC9),
    SomethingBig("something_big", 0xEACA),
    TooNoble("too_noble", 0xEACB),
    ArkhamIncidents("arkham_incidents", 0xEACC),
    Rop("rop", 0xEACD),
    Zoc("zoc", 0xEACE),
    EnthrallingEncore("enthralling_encore", 0xEACF),
    LaidToRest("laid_to_rest", 0xEAD0),
    AncientHunger("ancient_hunger", 0xEAD1),
    Legendary("legendary", 0xEAD2),
    WildBeasts("wild_beasts", 0xEAD3),
    Magyar("magyar", 0xEAD4),
    WitchCult("witch_cult", 0xEAD5),
    NaturalHazards("natural_hazards", 0xEAD6),
    Occultism("occultism", 0xEAD7),
    Tomes("tomes", 0xEAD8),
    TheBlackStone("the_black_stone", 0xEAD9),
    Fortune("fortune", 0xEADA),
    HannihahValley("hannihah_valley", 0xEADB),
    WendigosMyth("wendigos_myth", 0xEADC),
    Wendigo("wendigo", 0xEADD),
    TheFallOfTheHouseOfUsher("the_fall_of_the_house_of_usher", 0xEADE),
    LostCathedral("lost_cathedral", 0xEADF),
    MadeFlesh("made_flesh", 0xEAE0),
    UnnaturalDisturbances("unnatural_disturbances", 0xEAE1),
    InhospitableLocality("inhospitable_locality", 0xEAE2),
    MeteoricPhenomenon("meteoric_phenomenon", 0xEAE3),
    Beta("beta", 0xEAE4),
    Zez("zez", 0xEAE5),
    MachinationsThroughTime("machinations_through_time", 0xEAE6),
    Mtt("mtt", 0xEAE7),
    SinkingShip("sinking_ship", 0xEAE8),
    DeepOnes1("deep_ones1", 0xEAE9),
    ConsternationOnTheConstellation("consternation_on_the_constellation", 0xEAEA),
    PlanInShambles("plan_in_shambles", 0xEAEB),
    FortunesChosen("fortunes_chosen", 0xEAEC),
    FortuneAndFolly("fortune_and_folly", 0xEAED),
    Roulette("roulette", 0xEAEE),
    MeddlingOfMeowlathotep("meddling_of_meowlathotep", 0xEAEF),
    BarkhamHorror("barkham_horror", 0xEAF0),
    DeathOfTheStars("death_of_the_stars", 0xEAF1),
    AssimilatingSwarm("assimilating_swarm", 0xEAF2),
    ChildrenOfParadise("children_of_paradise", 0xEAF3),
    WarOfTheOuterGods("war_of_the_outer_gods", 0xEAF4),
    Wotog("wotog", 0xEAF5),
    RedTideRising("red_tide_rising", 0xEAF6),
    ByTheBook("by_the_book", 0xEAF7),
    BadBlood("bad_blood", 0xEAF8),
    AllOrNothing("all_or_nothing", 0xEAF9),
    ReadOrDie("read_or_die", 0xEAFA),
    Migo("migo", 0xEAFB),
    TheBlobThatAteEverything("the_blob_that_ate_everything", 0xEAFC),
    VileExperiments("vile_experiments", 0xEAFD),
    SinsOfThePast("sins_of_the_past", 0xEAFE),
    ExcelsiorManagement("excelsior_management", 0xEAFF),
    DarkRituals("dark_rituals", 0xEB00),
    AlienInterference("alien_interference", 0xEB01),
    MurderAtTheExcelsiorHotel("murder_at_the_excelsior_hotel", 0xEB02),
    NightsUsurper("nights_usurper", 0xEB03),
    EternalSlumber("eternal_slumber", 0xEB04),
    SandsOfEgypt("sands_of_egypt", 0xEB05),
    BrotherhoodOfTheBeast("brotherhood_of_the_beast", 0xEB06),
    AbyssalTribute("abyssal_tribute", 0xEB07),
    AbyssalGifts("abyssal_gifts", 0xEB08),
    SingleGroup("single_group", 0xEB09),
    EpicMultiplayer("epic_multiplayer", 0xEB0A),
    InTheLabyrinthsOfLunacy("in_the_labyrinths_of_lunacy", 0xEB0B),
    Carnevale("carnevale", 0xEB0C),
    CurseOfTheRougarou("curse_of_the_rougarou", 0xEB0D),
    TheBayou("the_bayou", 0xEB0E),
    Afflicted("afflicted", 0xEB0F),
    Novella("novella", 0xEB10),
    Blbe("blbe", 0xEB11),
    BlobThatAteEverythingElse("blob_that_ate_everything_else", 0xEB12),
    MigoIncursion2("migo_incursion_2", 0xEB13),
    InhabitantsOfCarcosa("inhabitants_of_carcosa", 0xEB14),
    TheStranger("the_stranger", 0xEB15),
    ReturnToCurtainCall("return_to_curtain_call", 0xEB16),
    ReturnToTheLastKing("return_to_the_last_king", 0xEB17),
    ReturnToEchoesOfThePast("return_to_echoes_of_the_past", 0xEB18),
    ReturnToTheUnspeakableOath("return_to_the_unspeakable_oath", 0xEB19),
    ReturnToThePhantomOfTruth("return_to_the_phantom_of_truth", 0xEB1A),
    ReturnToThePallidMask("return_to_the_pallid_mask", 0xEB1B),
    ReturnToBlackStarsRise("return_to_black_stars_rise", 0xEB1C),
    ReturnToDimCarcosa("return_to_dim_carcosa", 0xEB1D),
    DelusoryEvils("delusory_evils", 0xEB1E),
    NeuroticFear("neurotic_fear", 0xEB1F),
    HastursEnvoys("hasturs_envoys", 0xEB20),
    DecayingReality("decaying_reality", 0xEB21),
    MaddeningDelusions("maddening_delusions", 0xEB22),
    Hauntings("hauntings", 0xEB23),
    HastursGift("hasturs_gift", 0xEB24),
    EvilPortents("evil_portents", 0xEB25),
    Delusions("delusions", 0xEB26),
    DecayAndFilth("decay_and_filth", 0xEB27),
    CultOfTheYellowSign("cult_of_the_yellow_sign", 0xEB28),
    Byakhee("byakhee", 0xEB29),
    DimCarcosa("dim_carcosa", 0xEB2A),
    TheVortexAbove("the_vortex_above", 0xEB2B),
    TheFloodBelow("the_flood_below", 0xEB2C),
    BlackStarsRise("black_stars_rise", 0xEB2D),
    ThePallidMask("the_pallid_mask", 0xEB2E),
    APhantomOfTruth("a_phantom_of_truth", 0xEB2F),
    TheUnspeakableOath("the_unspeakable_oath", 0xEB30),
    EchoesOfThePast("echoes_of_the_past", 0xEB31),
    TheLastKing("the_last_king", 0xEB32),
    CurtainCall("curtain_call", 0xEB33),
    Whippoorwills("whippoorwills", 0xEB34),
    ReturnToExtracurricularActivities("return_to_extracurricular_activities", 0xEB35),
    ReturnToTheHouseAlwaysWins("return_to_the_house_always_wins", 0xEB36),
    ReturnToTheMiskatonicMuseum("return_to_the_miskatonic_museum", 0xEB37),
    ReturnToTheEssexCountyExpress("return_to_the_essex_county_express", 0xEB38),
    ReturnToBloodOnTheAltar("return_to_blood_on_the_altar", 0xEB39),
    ReturnToUndimensionedAndUnseen("return_to_undimensioned_and_unseen", 0xEB3A),
    ReturnToWhereDoomAwaits("return_to_where_doom_awaits", 0xEB3B),
    ReturnToLostInTimeAndSpace("return_to_lost_in_time_and_space", 0xEB3C),
    ResurgentEvils("resurgent_evils", 0xEB3D),
    ErraticFear("erratic_fear", 0xEB3E),
    CreepingCold("creeping_cold", 0xEB3F),
    SecretDoors("secret_doors", 0xEB40),
    YogSothothsEmissaries("yog_sothoths_emissaries", 0xEB41),
    BeyondTheThreshold("beyond_the_threshold", 0xEB42),
    TheBeyond("the_beyond", 0xEB43),
    Sorcery("sorcery", 0xEB44),
    NaomisCrew("naomis_crew", 0xEB45),
    HideousAbominations("hideous_abominations", 0xEB46),
    Dunwich("dunwich", 0xEB47),
    BishopsThralls("bishops_thralls", 0xEB48),
    BeastThralls("beast_thralls", 0xEB49),
    BadLuck("bad_luck", 0xEB4A),
    LostInTimeAndSpace("lost_in_time_and_space", 0xEB4B),
    WhereDoomAwaits("where_doom_awaits", 0xEB4C),
    UndimensionedAndUnseen("undimensioned_and_unseen", 0xEB4D),
    BloodOnTheAltar("blood_on_the_altar", 0xEB4E),
    TheEssexCountyExpress("the_essex_county_express", 0xEB4F),
    TheMiskatonicMuseum("the_miskatonic_museum", 0xEB50),
    ArmitagesFate("armitages_fate", 0xEB51),
    TheHouseAlwaysWins("the_house_always_wins", 0xEB52),
    ExtracurricularActivity("extracurricular_activity", 0xEB53),
    GhoulsOfUmrdhoth("ghouls_of_umrdhoth", 0xEB54),
    TheDevourersCult("the_devourers_cult", 0xEB55),
    ReturnToTheGathering("return_to_the_gathering", 0xEB56),
    ReturnToTheMidnightMasks("return_to_the_midnight_masks", 0xEB57),
    ReturnCult("return_cult", 0xEB58),
    ReturnToTheDevourerBelow("return_to_the_devourer_below", 0xEB59),
    Core2026("core_2026", 0xEB5A),
    TheDevourerBelow("the_devourer_below", 0xEB5B),
    CultOfUmordoth("cult_of_umordoth", 0xEB5C),
    MidnightMasks("midnight_masks", 0xEB5D),
    TheGathering("the_gathering", 0xEB5E),
    StrikingFear("striking_fear", 0xEB5F),
    Rats("rats", 0xEB60),
    Nightgaunts("nightgaunts", 0xEB61),
    LockedDoors("locked_doors", 0xEB62),
    AgentsOfShub("agents_of_shub", 0xEB63),
    AgentsOfHastur("agents_of_hastur", 0xEB64),
    Core("core", 0xEB65),
    ChillingCold("chilling_cold", 0xEB66),
    Ghouls("ghouls", 0xEB67),
    AgentsOfCthulhu("agents_of_cthulhu", 0xEB68),
    AgentsOfYog("agents_of_yog", 0xEB69),
    DarkCult("dark_cult", 0xEB6A),
    AncientEvils("ancient_evils", 0xEB6B),
    Marie("marie", 0xEB6C),
    Andre("andre", 0xEB6D),
    Miguel("miguel", 0xEB6E),
    AgentsOfZburamoarte("agents_of_zburamoarte", 0xEB6F),
    ArcaneLock("arcane_lock", 0xEB70),
    ArkhamCh2("arkham_ch2", 0xEB71),
    AshenPilgrims("ashen_pilgrims", 0xEB72),
    BadWeather("bad_weather", 0xEB73),
    Bystanders("bystanders", 0xEB74),
    DeadEnds("dead_ends", 0xEB75),
    CosmicEvils("cosmic_evils", 0xEB76),
    CultistsCh2("cultists_ch2", 0xEB77),
    EldritchLore("eldritch_lore", 0xEB78),
    FireCh2("fire_ch2", 0xEB79),
    FlyingTerrors("flying_terrors", 0xEB7A),
    GangsOfArkham("gangs_of_arkham", 0xEB7B),
    Hallucinations("hallucinations", 0xEB7C),
    MadScience("mad_science", 0xEB7D),
    MiskatonicUniversity("miskatonic_university", 0xEB7E),
    PeopleOfArkham("people_of_arkham", 0xEB7F),
    QueenOfAsh("queen_of_ash", 0xEB80),
    ReekingDecay("reeking_decay", 0xEB81),
    ArkhamSewers("arkham_sewers", 0xEB82),
    SmokeAndMirrors("smoke_and_mirrors", 0xEB83),
    SpreadingFlames("spreading_flames", 0xEB84),
    Torment("torment", 0xEB85),
    WhippoorwillsCh2("whippoorwills_ch2", 0xEB86),
    Carolyn("carolyn", 0xEB87),
    Tommy("tommy", 0xEB88),
    BloodBlight("blood_blight", 0xEB89),
    BloodMoney("blood_money", 0xEB8A),
    BloodMoon("blood_moon", 0xEB8B),
    Bloodthirst("bloodthirst", 0xEB8C),
    ChildrenOfBlood("children_of_blood", 0xEB8D),
    Cob("cob", 0xEB8E),
    FriendsInLowPlaces("friends_in_low_places", 0xEB8F),
    Hunted("hunted", 0xEB90),
    Infected("infected", 0xEB91),
    Misinformation("misinformation", 0xEB92),
    Mongrels("mongrels", 0xEB93),
    NewHorizons("new_horizons", 0xEB94),
    PreyedUpon("preyed_upon", 0xEB95),
    RiverOfBlood("river_of_blood", 0xEB96),
    SanguineSecrets("sanguine_secrets", 0xEB97),
    Stalked("stalked", 0xEB98),
    Vermin("vermin", 0xEB99);

    override val glyph: String
        get() = code.toChar().toString()

    override val fontFamily: FontFamily
        get() = EncounterIconsFont

    companion object {
        private val byPackCode = entries.associateBy { it.stringCode }

        fun fromPackCode(packCode: String, isPack: Boolean = true): IconGlyph = when(packCode) {
            "nat" -> Nate
            "har" -> Harvey
            "win" -> Winifred
            "jac" -> Jacqueline
            "ste" -> Stella
            "zbh" -> BarkhamHorror
            "boa" -> Core2026
            "tom" -> Tommy
            "car" -> Carolyn
            "mar" -> Marie
            "and" -> Andre
            "mig" -> Miguel
            "ghouls_of_umôrdhoth" -> GhoulsOfUmrdhoth
            "pentagram" -> DarkCult
            "cultists" -> CultOfUmordoth
            "torch" -> TheGathering
            "arkham" -> MidnightMasks
            "tentacles" -> TheDevourerBelow
            "dwl", "dwlp", "dwlc" -> Set
            "rtdwl" -> ReturnToTheDunwichLegacy
            "tmm" -> TheMiskatonicMuseum
            "tece", "essex_county_express" -> TheEssexCountyExpress
            "bota" -> BloodOnTheAltar
            "uau" -> UndimensionedAndUnseen
            "wda" -> WhereDoomAwaits
            "litas" -> LostInTimeAndSpace
            "return_to_essex_county_express" -> ReturnToTheEssexCountyExpress
            "return_to_extracurricular_activity" -> ReturnToExtracurricularActivities
            "ptc", "ptcp", "ptcc" -> Carcosa
            "rtptc" ->  ReturnToThePathToCarcosa
            "eotp" -> EchoesOfThePast
            "tuo" ->TheUnspeakableOath
            "apot" -> APhantomOfTruth
            "tpm" -> ThePallidMask
            "bsr" -> BlackStarsRise
            "flood" -> TheFloodBelow
            "vortex" -> TheVortexAbove
            "dca" -> DimCarcosa
            "return_to_a_phantom_of_truth" -> ReturnToThePhantomOfTruth
            "decay" -> DecayAndFilth
            "stranger" -> TheStranger
            "tmg", "the_midwinter_gala" -> Gala
            "iotv", "tdor", "tdg", "tftbw", "hoth", "bob", "dre", "promo", "promotional", "books" -> Novella
            "coh", "carnevale_of_horrors" -> Carnevale
            "venice" -> Venice1
            "rod" -> ReadOrDie
            "aon" -> AllOrNothing
            "bad" -> BadBlood
            "btb" -> ByTheBook
            "ltr" -> LaidToRest
            "rtr" -> RedTideRising
            "enc" -> EnthrallingEncore
            "parallel", "otr", "on_the_road_again", "ptr", "path_of_the_righteous", "pap",
                "pistols_and_pearls", "hfa", "hunting_for_answers" -> AppIcon.Parallel
            "cotr" -> CurseOfTheRougarou
            "rougarou" -> Rougerauo2
            "bayou" -> TheBayou
            "blob", "blob_that_ate_everything" -> TheBlobThatAteEverything
            "blbe" -> BlobThatAteEverythingElse
            "migo_incursion" -> Migo
            "machinations_epic_multiplayer", "blob_epic_multiplayer" -> EpicMultiplayer
            "blob_single_group", "machinations_single_group" -> SingleGroup
            "gob" -> Guardians
            "hotel" -> Excelsior
            "the_eternal_slumber" -> EternalSlumber
            "the_nights_usurper" -> NightsUsurper
            "hote" -> HeartOfTheElders
            "tcoa", "the_city_of_archives" -> CityOfArchives
            "return_to_the_city_of_archives" -> ReturnToCityOfArchives
            "tfa", "tfap", "tfac" -> TheForgottenAge
            "sha" -> ShatteredAeons
            "tdoy", "depths_of_yoth" -> TheDepthsOfYoth
            "tof" -> ThreadsOfFate
            "tbb" -> TheBoundaryBeyond
            "wilds" -> TheUntamedWilds
            "eztli" -> TheDoomOfEztli
            "traps" -> DeadlyTraps
            "flux" -> TemporalFlux
            "ruins" -> ForgottenRuins
            "venom" -> YigsVenom
            "k\"n-yan", "heart_of_the_elders_part_2" -> Knyan
            "heart_of_the_elders_part_1", "pillars_of_judgment" -> PillarsOfJudgement
            "rttfa" -> ReturnToTheForgottenAge
            "return_to_heart_of_the_elders_part_1", "return_to_pillars_of_judgment" -> ReturnToPillarsOfJudgement
            "return_to_heart_of_the_elders_part_2" -> ReturnToKnyan
            "return_to_heart_of_the_elders" -> ReturnToTheHeartOfTheElders
            "wog" -> WarOfTheOuterGods
            "swarm_of_assimilation" -> AssimilatingSwarm
            "death_of_stars" -> DeathOfTheStars
            "tcu", "tcup", "tcuc" -> TheCircleUndone
            "tsn" -> TheSecretName
            "wos" -> TheWagesOfSin
            "fgg" -> ForTheGreaterGood
            "uad" -> UnionAndDisillusion
            "icc" -> InTheClutchesOfChaos
            "bbt" -> BeforeTheBlackThrone
            "unavoidable_demise" -> UnspeakableFate
            "hexcraft" -> Witchwork
            "unstable_realm" -> SpectralRealm
            "threatening_evils", "impending_evils" -> ThreateningEvil
            "tdeb" -> AgentsOfAtlachNacha
            "tdea" -> DreamersCurse
            "tde", "tdep", "tdec", "the_dream_eaters" -> Dream
            "sfk" -> TheSearchForKadath
            "wgd", "where_the_gods_dwell" -> WhereGodsDwell
            "woc" -> WeaverOfTheCosmos
            "dsm" -> DarkSideOfTheMoon
            "tsh" -> AThousandShapesOfHorror
            "pnr" -> PointOfNoReturn
            "itd" -> InTooDeep
            "def" -> DevilReef
            "hhg" -> HorrorInHighGear
            "itm" -> IntoTheMaelstrom
            "ticp", "ticc", "the_innsmouth_conspiracy" -> Tic
            "tdc" -> Tdcc
            "creatures_of_the_deep" -> CreaturesFromBelow
            "the_locals" -> Locals
            "the_vanishing_of_elina_harper" -> DisappearanceOfElinaHarper
            "the_pit_of_despair" -> GrottoOfDespair
            "flooded_caverns" -> FloodedCaves
            "lif" -> ALightInTheFog
            "the_lair_of_dagon", "lod" -> LairOfDagon
            "dark_matter", "zdm" -> TheTatterdemalion
            "the_symphony_of_erich_zann", "the_symphony_of_erich_zahn" -> Zez
            "zcos", "the_colour_out_of_space" -> MeteoricPhenomenon
            "crown_of_egil" -> Zce
            "the_golden_circle" -> GoldenCircle
            "chesire_cat" -> CheshireCat
            "jabberwocky" -> Jabberwock
            "zbt" -> Beta
            "zaw" -> AliceInWonderland
            "zwtws" -> WhenTheWorldScreamed
            "film_fatale" -> if(isPack) FilmFatale else FilmFataleEncounter
            "legendry" -> Legendary
            "zbs" -> TheBlackStone
            "zcc" -> ConsternationOnTheConstellation
            "eoep" -> Eoe
            "eoe", "eoec", "edge_of_the_earth" -> EoeCampaign
            "fatal_mirage_2", "fatal_mirage_3" -> FatalMirage
            "the_heart_of_madness_part_1", "the_heart_of_madness_part_2" -> TheHeartOfMadness
            "tekelili" -> TekeliLi
            "ice_and_death_part_1", "ice_and_death_part_2", "ice_and_death_part_3" -> IceAndDeath
            "seeping_nightmares" -> SleepingNightmares
            "tskp" -> Tsk
            "shades_of_suffering" -> ShadesOfSorrow
            "arkham_ma" -> Arkham
            "istanbul" -> Constantinople
            "bermuda" -> Bermuda2
            "bermuda_triangle" -> Bermuda
            "venice_it" -> Venice
            "agents_of_cthugha" -> AgentsOfCthugua
            "spawn_of_rlyeh" -> SpawnOfRyleh
            "zhu" -> TheFallOfTheHouseOfUsher
            "zatw", "against_the_wendigo" -> Wendigo
            "hanninah_valley" -> HannihahValley
            "zjc", "jennys_choice" -> LostCathedral
            "fof", "fortune_and_folly_part_1", "fortune_and_folly_part_2" -> Roulette
            "mourning_chorus" -> MourningStroll
            "zcp" -> CallOfThePlaguebearer
            "zcu" -> AppIcon.CardsLogo
            "zsti" -> AppIcon.Investigator
            "zhod" -> HeartOfDarkness
            "zhod_africa_is_watching" -> AfricaIsWatching
            "zhod_to_the_heart_of_the_congo" -> ToTheHeartOfTheCongo
            "zhod_the_avatar_of_darkness" -> TheAvatarOfDarkness
            "zhod_the_darkness" -> TheDarkness
            "zhod_african_wildlife" -> AfricanWildlife
            "zhod_lands_of_the_congo" -> LandsOfTheCongo
            "zhod_cult_of_darkness" -> CultOfDarkness
            "zrttic" -> Rttic
            "zreturn_to_the_pit_of_despair" -> ReturnToThePitOfDespair
            "zreturn_to_the_vanishing_of_elina_harper" -> ReturnToTheVanishingOfElinaHarper
            "zreturn_to_in_too_deep" -> ReturnToInTooDeep
            "zreturn_to_devil_reef" -> ReturnToDevilReef
            "zreturn_to_horror_in_high_gear" -> ReturnToHorrorInHighGear
            "zreturn_to_a_light_in_the_fog" -> ReturnToALightInTheFog
            "zreturn_to_the_lair_of_dagon" -> ReturnToTheLairOfDagon
            "zreturn_to_into_the_maelstrom" -> ReturnToIntoTheMaelstrom
            "zstalkers_of_cthulhu" -> StalkersOfCthulhu
            "zrolling_tide" -> RollingTide
            "zinnsmouth_haze" -> InnsmouthHaze
            "zbarricaded_doors" -> BarricadedDoors
            "zoccultation" -> Occultation
            "zreturn_to_flooded_caverns" -> ReturnToFloodedCaverns
            "relics_of_the_past" -> Rop
            "zai" -> ArkhamIncidents
            "fhv" -> Fhvc
            "zlf" -> LegionsOfFire

            "zoz_the_road_to_oz" -> TheRoadToOz
            "zoz_ferocious_beasts" -> FerociousBeasts
            "zoz_wicked_witches" -> WickedWitches
            "zoz_prismatic_evils" -> PrismaticEvils
            "zoz_terror_out_of_space" -> TerrorOutOfSpace
            "zoz_princess_of_oz" -> PrincessOfOz
            "zoz_chromatic_infection" -> ChromaticInfection
            "zoz_deep_impact" -> DeepImpact
            "zoz_emerald_city" -> EmeraldCity
            "zoz_alien_vibrance" -> AlienVibrance
            "zoz_horrid_infection" -> HorridInfection
            "zoz_companions_of_oz" -> CompanionsOfOz
            "zoz_spiraling_decay" -> SpiralingDecay
            "zoz_double_whammy" -> DoubleWhammy
            "zoz_chasing_rainbows" -> ChasingRainbows
            "zoz_blighted_land" -> BlightedLand
            "zoz_misery_loves_company" -> MiseryLovesCompany
            "zoz_nomes" -> Nomes
            "zoz_violent_invasion" -> ViolentInvasion
            "zoz_hall_of_the_mountain_king" -> HallOfTheMountainKing
            "zoz_defense_of_the_realm" -> DefenseOfTheRealm
            "zoz_true_colours" -> TrueColours
            "zoz_the_colour_itself" -> TheColourItself
            "zoz_munchkin" -> MunchkinCountry
            "zoz_winkie" -> WinkieCountry
            "zoz_quadling" -> QuadlingCountry
            "zoz_gillikin" -> GillikinCountry
            "the_colour_out_of_oz" -> Zoz
            "zau_night_of_fire" -> ANightOfFire
            "zau_myriad_gentleman" -> TheMyriadGentleman
            "zau_world_torn_down" -> AWorldTornDown
            "zau_unstuck" -> Unstuck
            "zau_year_to_plan" -> AYearToPlan
            "zau_world_torn_down_again" -> AWorldTornDownAgain
            "zau_time_runs_out", "time_runs_out" -> TimeRunsOut
            "zau_agents_of_aforgomon" -> AgentsOfAforgomon
            "zau_missions" -> Missions
            "zau_myriad" -> Myriad
            "zau_night_of_the_ritual" -> NightOfTheRitual
            "zau_nyctophobia" -> Nyctophobia
            "zau_paradox" -> Paradox
            "zau_shifting_reality" -> ShiftingReality
            "zau_thugs" -> Thugs
            "zau_unleashed_chaos" -> UnleashedChaos
            "zau_unravelling_years" -> UnravellingYears
            "zau", "zau_ages_unwound" -> AgesUnwound
            "tablet" -> AppIcon.Tablet
            "zcx", "zcxc", "zcem" -> ZcxCircusExMortis
            "zgoo" -> Onigawa
            "zdh" -> Darkham
            else -> byPackCode[packCode]
        } ?: Core
    }
}