package com.arkhamcompanion.ui.utils

import com.arkhamcompanion.R

fun getLocalizedAction(code: String) =
    when (code) {
        "Fight" -> R.string.fight
        "Engage" -> R.string.engage
        "Investigate" -> R.string.investigate
        "Draw" -> R.string.draw_action
        "Resource" -> R.string.resource_action
        "Move" -> R.string.move
        "Evade" -> R.string.evade
        "Parley" -> R.string.parley
        "Resign" -> R.string.resign
        else -> R.string.unknown
    }

fun getLocalizedTrait(code: String) =
    when (code) {
        "???" -> R.string.trait_question_marks
        "Abandoned" -> R.string.trait_abandoned
        "Abomination" -> R.string.trait_abomination
        "Abyss" -> R.string.trait_abyss
        "Access" -> R.string.trait_access
        "Accursed" -> R.string.trait_accursed
        "Act 1" -> R.string.trait_act_1
        "Act 2" -> R.string.trait_act_2
        "Adrift" -> R.string.trait_adrift
        "Agency" -> R.string.trait_agency
        "AI" -> R.string.trait_ai
        "Alchemy" -> R.string.trait_alchemy
        "Alexandria" -> R.string.trait_alexandria
        "Allied" -> R.string.trait_allied
        "Ally" -> R.string.trait_ally
        "Ally? Creature" -> R.string.trait_ally_question
        "Altered" -> R.string.trait_altered
        "Ancient" -> R.string.trait_ancient
        "Ancient One" -> R.string.trait_ancient_one
        "Ancient One?" -> R.string.trait_ancient_one_question
        "Apiary" -> R.string.trait_apiary
        "Apparel" -> R.string.trait_apparel
        "Arkham" -> R.string.trait_arkham
        "Arkham Asylum" -> R.string.trait_arkham_asylum
        "Arkham?" -> R.string.trait_arkham_question
        "Armor" -> R.string.trait_armor
        "Artifact" -> R.string.trait_artifact
        "Artist" -> R.string.trait_artist
        "Assistant" -> R.string.trait_assistant
        "Attack" -> R.string.trait_attack
        "Augury" -> R.string.trait_augury
        "Avatar" -> R.string.trait_avatar
        "Basement" -> R.string.trait_basement
        "Bane" -> R.string.trait_bane
        "Bayou" -> R.string.trait_bayou
        "Bazaar" -> R.string.trait_bazaar
        "Believer" -> R.string.trait_believer
        "Blessed" -> R.string.trait_blessed
        "Blessed?" -> R.string.trait_blessed_question
        "Blight" -> R.string.trait_blight
        "Blunder" -> R.string.trait_blunder
        "Boat" -> R.string.trait_boat
        "Bog" -> R.string.trait_bog
        "Bokor" -> R.string.trait_bokor
        "Bold" -> R.string.trait_bold
        "Book" -> R.string.trait_book
        "Boon" -> R.string.trait_boon
        "Brand" -> R.string.trait_brand
        "Bridge" -> R.string.trait_bridge
        "Broken" -> R.string.trait_broken
        "Brotherhood" -> R.string.trait_brotherhood
        "Buenos Aires" -> R.string.trait_buenos_aires
        "Byakhee" -> R.string.trait_byakhee
        "Bystander" -> R.string.trait_bystander
        "Cairo" -> R.string.trait_cairo
        "Camp" -> R.string.trait_camp
        "Campsite" -> R.string.trait_campsite
        "Card" -> R.string.trait_card
        "Carnevale" -> R.string.trait_carnevale
        "Cart" -> R.string.trait_cart
        "Case" -> R.string.trait_case
        "Casino" -> R.string.trait_casino
        "Castle" -> R.string.trait_castle
        "Cave" -> R.string.trait_cave
        "Central" -> R.string.trait_central
        "Chair" -> R.string.trait_chair
        "Charm" -> R.string.trait_charm
        "Chess" -> R.string.trait_chess
        "Chosen" -> R.string.trait_chosen
        "Circle" -> R.string.trait_circle
        "Circus Train" -> R.string.trait_circus_train
        "City" -> R.string.trait_city
        "Civic" -> R.string.trait_civic
        "Civilian" -> R.string.trait_civilian
        "Clairvoyant" -> R.string.trait_clairvoyant
        "Classy" -> R.string.trait_classy
        "Clearing" -> R.string.trait_clearing
        "Clothing" -> R.string.trait_clothing
        "Clover Club" -> R.string.trait_clover_club
        "Coastal" -> R.string.trait_coastal
        "Collector" -> R.string.trait_collector
        "Colour" -> R.string.trait_colour
        "Completed" -> R.string.trait_completed
        "Composure" -> R.string.trait_composure
        "Condition" -> R.string.trait_condition
        "Connection" -> R.string.trait_connection
        "Consort" -> R.string.trait_consort
        "Conspirator" -> R.string.trait_conspirator
        "Construct" -> R.string.trait_construct
        "Corruption" -> R.string.trait_corruption
        "Corpse" -> R.string.trait_corpse
        "Cosmos" -> R.string.trait_cosmos
        "Coterie" -> R.string.trait_coterie
        "Covenant" -> R.string.trait_covenant
        "Creature" -> R.string.trait_creature
        "Cretaceous" -> R.string.trait_cretaceous
        "Crew" -> R.string.trait_crew
        "Crime Scene" -> R.string.trait_crime_scene
        "Criminal" -> R.string.trait_criminal
        "Cthulhu" -> R.string.trait_cthulhu
        "Cultist" -> R.string.trait_cultist
        "Curse" -> R.string.trait_curse
        "Cursed" -> R.string.trait_cursed
        "Dark" -> R.string.trait_dark
        "Dark Young" -> R.string.trait_dark_young
        "Darkness" -> R.string.trait_darkness
        "Deep One" -> R.string.trait_deep_one
        "Delta" -> R.string.trait_delta
        "Desert" -> R.string.trait_desert
        "Depths" -> R.string.trait_depths
        "Desperate" -> R.string.trait_desperate
        "Detective" -> R.string.trait_detective
        "Developed" -> R.string.trait_developed
        "Device" -> R.string.trait_device
        "Dhole" -> R.string.trait_dhole
        "Dilemma" -> R.string.trait_dilemma
        "Dinosaur" -> R.string.trait_dinosaur
        "Distortion" -> R.string.trait_distortion
        "Dormant" -> R.string.trait_dormant
        "Double" -> R.string.trait_double
        "Downtown" -> R.string.trait_downtown
        "Dreamer" -> R.string.trait_dreamer
        "Dreamlands" -> R.string.trait_dreamlands
        "Drifter" -> R.string.trait_drifter
        "Dunwich" -> R.string.trait_dunwich
        "Eidolon" -> R.string.trait_eidolon
        "Elder Thing" -> R.string.trait_elder_thing
        "Eldritch" -> R.string.trait_eldritch
        "Elite" -> R.string.trait_elite
        "Emissary" -> R.string.trait_emissary
        "Endtimes" -> R.string.trait_endtimes
        "Enclave" -> R.string.trait_enclave
        "Enigma" -> R.string.trait_enigma
        "Enraged" -> R.string.trait_enraged
        "Entrance" -> R.string.trait_entrance
        "Entrepreneur" -> R.string.trait_entrepreneur
        "Evidence" -> R.string.trait_evidence
        "Egypt" -> R.string.trait_egypt
        "Exhibit" -> R.string.trait_exhibit
        "Expedition" -> R.string.trait_expedition
        "Expert" -> R.string.trait_expert
        "Extradimensional" -> R.string.trait_extradimensional
        "Extraterrestrial" -> R.string.trait_extraterrestrial
        "Eztli" -> R.string.trait_eztli
        "Fairy" -> R.string.trait_fairy
        "Falcon Point" -> R.string.trait_falcon_point
        "Familiar" -> R.string.trait_familiar
        "Farm" -> R.string.trait_farm
        "Fated" -> R.string.trait_fated
        "Favor" -> R.string.trait_favor
        "Fellow Dogs" -> R.string.trait_fellow_dogs
        "Field" -> R.string.trait_field
        "Firearm" -> R.string.trait_firearm
        "Flaw" -> R.string.trait_flaw
        "Flora" -> R.string.trait_flora
        "Footwear" -> R.string.trait_footwear
        "Footwear, but not really" -> R.string.trait_footwear_but_not_really
        "Forbidden" -> R.string.trait_forbidden
        "Forest" -> R.string.trait_forest
        "Forgotten" -> R.string.trait_forgotten
        "Fortune" -> R.string.trait_fortune
        "France" -> R.string.trait_france
        "Freight Car" -> R.string.trait_freight_car
        "Front" -> R.string.trait_front
        "Future" -> R.string.trait_future
        "Gambit" -> R.string.trait_gambit
        "Game" -> R.string.trait_game
        "Geist" -> R.string.trait_geist
        "Ghast" -> R.string.trait_ghast
        "Ghoul" -> R.string.trait_ghoul
        "Glacier" -> R.string.trait_glacier
        "Glyph" -> R.string.trait_glyph
        "Government" -> R.string.trait_government
        "Grant" -> R.string.trait_grant
        "Graveyard" -> R.string.trait_graveyard
        "Ground Floor" -> R.string.trait_ground_floor
        "Gug" -> R.string.trait_gug
        "Guest" -> R.string.trait_guest
        "Hall" -> R.string.trait_hall
        "Hardship" -> R.string.trait_hardship
        "Haunted" -> R.string.trait_haunted
        "Havana" -> R.string.trait_havana
        "Hazard" -> R.string.trait_hazard
        "Headgear" -> R.string.trait_headgear
        "Headwear" -> R.string.trait_headwear
        "Hemlock Vale" -> R.string.trait_hemlock_vale
        "Hex" -> R.string.trait_hex
        "Historical Society" -> R.string.trait_historical_society
        "Hideout" -> R.string.trait_hideout
        "Human" -> R.string.trait_human
        "Humanoid" -> R.string.trait_humanoid
        "Hunter" -> R.string.trait_hunter
        "Hybrid" -> R.string.trait_hybrid
        "Illicit" -> R.string.trait_illicit
        "Improvised" -> R.string.trait_improvised
        "Incomplete" -> R.string.trait_incomplete
        "Inconspicuous" -> R.string.trait_inconspicuous
        "Infection" -> R.string.trait_infection
        "Injury" -> R.string.trait_injury
        "Innate" -> R.string.trait_innate
        "Innocent" -> R.string.trait_innocent
        "Innsmouth" -> R.string.trait_innsmouth
        "Insect" -> R.string.trait_insect
        "Insight" -> R.string.trait_insight
        "Instrument" -> R.string.trait_instrument
        "Item" -> R.string.trait_item
        "Island" -> R.string.trait_island
        "Istanbul" -> R.string.trait_istanbul
        "Job" -> R.string.trait_job
        "Jungle" -> R.string.trait_jungle
        "Kadath" -> R.string.trait_kadath
        "Keeper" -> R.string.trait_keeper
        "Key" -> R.string.trait_key
        "Kingsport" -> R.string.trait_kingsport
        "Kuala Lumpur" -> R.string.trait_kuala_lumpur
        "Lair" -> R.string.trait_lair
        "Lantern Club" -> R.string.trait_lantern_club
        "Leader" -> R.string.trait_leader
        "Lead" -> R.string.trait_lead
        "Leng" -> R.string.trait_leng
        "Liber Pater" -> R.string.trait_liber_pater
        "Lift" -> R.string.trait_lift
        "Lit" -> R.string.trait_lit
        "Local" -> R.string.trait_local
        "Locus Site" -> R.string.trait_locus_site
        "Lodge" -> R.string.trait_lodge
        "London" -> R.string.trait_london
        "Loyal" -> R.string.trait_loyal
        "Lunatic" -> R.string.trait_lunatic
        "Machination" -> R.string.trait_machination
        "Machine" -> R.string.trait_machine
        "Madness" -> R.string.trait_madness
        "Madness?" -> R.string.trait_madness_question
        "Mainland" -> R.string.trait_mainland
        "Manor" -> R.string.trait_manor
        "Manifold" -> R.string.trait_manifold
        "Marrakesh" -> R.string.trait_marrakesh
        "Mask" -> R.string.trait_mask
        "Medical" -> R.string.trait_medical
        "Medic" -> R.string.trait_medic
        "Melee" -> R.string.trait_melee
        "Mexico City" -> R.string.trait_mexico_city
        "Miasma" -> R.string.trait_miasma
        "Midtown" -> R.string.trait_midtown
        "Mi-Go" -> R.string.trait_mi_go
        "Mirage" -> R.string.trait_mirage
        "Miskatonic" -> R.string.trait_miskatonic
        "Misfortune" -> R.string.trait_misfortune
        "Mnar" -> R.string.trait_mnar
        "Monster" -> R.string.trait_monster
        "Monsters" -> R.string.trait_monsters
        "Montréal" -> R.string.trait_montreal
        "Mountain" -> R.string.trait_mountain
        "Mountains" -> R.string.trait_mountains
        "Music" -> R.string.trait_music
        "Musician" -> R.string.trait_musician
        "Mutated" -> R.string.trait_mutated
        "Mutation" -> R.string.trait_mutation
        "Mystery" -> R.string.trait_mystery
        "Nest" -> R.string.trait_nest
        "New Moon Circus" -> R.string.trait_new_moon_circus
        "New Orleans" -> R.string.trait_new_orleans
        "New York City" -> R.string.trait_new_york_city
        "Nightgaunt" -> R.string.trait_nightgaunt
        "Northside" -> R.string.trait_northside
        "Nostalgia II" -> R.string.trait_nostalgia_ii
        "Obstacle" -> R.string.trait_obstacle
        "Occult" -> R.string.trait_occult
        "Ocean" -> R.string.trait_ocean
        "Omen" -> R.string.trait_omen
        "Ooze" -> R.string.trait_ooze
        "Oozified" -> R.string.trait_oozified
        "Ooth-Nargai" -> R.string.trait_ooth_nargai
        "Oriab" -> R.string.trait_oriab
        "Otherworld" -> R.string.trait_otherworld
        "Outsider" -> R.string.trait_outsider
        "Oz" -> R.string.trait_oz
        "Pact" -> R.string.trait_pact
        "Paradox" -> R.string.trait_paradox
        "Parasitic" -> R.string.trait_parasitic
        "Paris" -> R.string.trait_paris
        "Part 1" -> R.string.trait_part_1
        "Part 2" -> R.string.trait_part_2
        "Passageway" -> R.string.trait_passageway
        "Past" -> R.string.trait_past
        "Path" -> R.string.trait_path
        "Patron" -> R.string.trait_patron
        "Performer" -> R.string.trait_performer
        "Pnakotus" -> R.string.trait_pnakotus
        "Poison" -> R.string.trait_poison
        "Police" -> R.string.trait_police
        "Port" -> R.string.trait_port
        "Portal" -> R.string.trait_portal
        "Possessed" -> R.string.trait_possessed
        "Posture" -> R.string.trait_posture
        "Power" -> R.string.trait_power
        "Plains" -> R.string.trait_plains
        "Plot" -> R.string.trait_plot
        "Practiced" -> R.string.trait_practiced
        "Present" -> R.string.trait_present
        "Present-Day" -> R.string.trait_present_day
        "Prop" -> R.string.trait_prop
        "Priestess" -> R.string.trait_priestess
        "Prison" -> R.string.trait_prison
        "Private" -> R.string.trait_private
        "Profession" -> R.string.trait_profession
        "Prosthesis" -> R.string.trait_prosthesis
        "Providence" -> R.string.trait_providence
        "Public" -> R.string.trait_public
        "Pup" -> R.string.trait_pup
        "R'lyeh" -> R.string.trait_r_lyeh
        "Rail" -> R.string.trait_rail
        "Ranged" -> R.string.trait_ranged
        "Relic" -> R.string.trait_relic
        "Reporter" -> R.string.trait_reporter
        "Research" -> R.string.trait_research
        "Resident" -> R.string.trait_resident
        "Resolute" -> R.string.trait_resolute
        "Resonant" -> R.string.trait_resonant
        "Restricted" -> R.string.trait_restricted
        "Retired" -> R.string.trait_retired
        "Return" -> R.string.trait_return
        "Rise" -> R.string.trait_rise
        "Risen" -> R.string.trait_risen
        "Ritual" -> R.string.trait_ritual
        "Ritual Site" -> R.string.trait_ritual_site
        "Rival" -> R.string.trait_rival
        "River" -> R.string.trait_river
        "Riverside" -> R.string.trait_riverside
        "Road" -> R.string.trait_road
        "Role" -> R.string.trait_role
        "Rome" -> R.string.trait_rome
        "Room" -> R.string.trait_room
        "Rooftop" -> R.string.trait_rooftop
        "Rot" -> R.string.trait_rot
        "Row" -> R.string.trait_row
        "Ruin" -> R.string.trait_ruin
        "Ruined" -> R.string.trait_ruined
        "Ruins" -> R.string.trait_ruins
        "Royalty" -> R.string.trait_royalty
        "Satellite" -> R.string.trait_satellite
        "Saturnite" -> R.string.trait_saturnite
        "Saga" -> R.string.trait_saga
        "Sailor" -> R.string.trait_sailor
        "Salem" -> R.string.trait_salem
        "Sanctum" -> R.string.trait_sanctum
        "Scheme" -> R.string.trait_scheme
        "Scholar" -> R.string.trait_scholar
        "Science" -> R.string.trait_science
        "Scientist" -> R.string.trait_scientist
        "Scion" -> R.string.trait_scion
        "Script" -> R.string.trait_script
        "Seafloor" -> R.string.trait_seafloor
        "Second Floor" -> R.string.trait_second_floor
        "Sentinel Hill" -> R.string.trait_sentinel_hill
        "Serpent" -> R.string.trait_serpent
        "Service" -> R.string.trait_service
        "Servitor" -> R.string.trait_servitor
        "Set" -> R.string.trait_set
        "Sewer" -> R.string.trait_sewer
        "Shantak" -> R.string.trait_shantak
        "Shapeshifter" -> R.string.trait_shapeshifter
        "Shattered" -> R.string.trait_shattered
        "Shadow" -> R.string.trait_shadow
        "Sheldon Gang" -> R.string.trait_sheldon_gang
        "Ship" -> R.string.trait_ship
        "Shoggoth" -> R.string.trait_shoggoth
        "Silver Twilight" -> R.string.trait_silver_twilight
        "Sister" -> R.string.trait_sister
        "Skai" -> R.string.trait_skai
        "Slope" -> R.string.trait_slope
        "Socialite" -> R.string.trait_socialite
        "Soldier" -> R.string.trait_soldier
        "Song" -> R.string.trait_song
        "Sorcerer" -> R.string.trait_sorcerer
        "Special Car" -> R.string.trait_special_car
        "Spectral" -> R.string.trait_spectral
        "Spell" -> R.string.trait_spell
        "Spider" -> R.string.trait_spider
        "Spirit" -> R.string.trait_spirit
        "Staff" -> R.string.trait_staff
        "Stable" -> R.string.trait_stable
        "Star Spawn" -> R.string.trait_star_spawn
        "Station" -> R.string.trait_station
        "Steps" -> R.string.trait_steps
        "Stowaway" -> R.string.trait_stowaway
        "St Mary's" -> R.string.trait_st_marys
        "Student" -> R.string.trait_student
        "Sub-Level" -> R.string.trait_sub_level
        "Summon" -> R.string.trait_summon
        "Summit" -> R.string.trait_summit
        "Sunken" -> R.string.trait_sunken
        "Supply" -> R.string.trait_supply
        "Surface" -> R.string.trait_surface
        "Suspect" -> R.string.trait_suspect
        "Symbiote" -> R.string.trait_symbiote
        "Synergy" -> R.string.trait_synergy
        "Syndicate" -> R.string.trait_syndicate
        "Table" -> R.string.trait_table
        "Tactic" -> R.string.trait_tactic
        "Talent" -> R.string.trait_talent
        "Tandem" -> R.string.trait_tandem
        "Tarot" -> R.string.trait_tarot
        "Task" -> R.string.trait_task
        "Tatterdemalion" -> R.string.trait_tatterdemalion
        "Tainted" -> R.string.trait_tainted
        "Temple" -> R.string.trait_temple
        "Tenochtitlán" -> R.string.trait_tenochtitlan
        "Tentacle" -> R.string.trait_tentacle
        "Terror" -> R.string.trait_terror
        "Third Floor" -> R.string.trait_third_floor
        "Tindalos" -> R.string.trait_tindalos
        "Tome" -> R.string.trait_tome
        "Tool" -> R.string.trait_tool
        "Town" -> R.string.trait_town
        "Tower" -> R.string.trait_tower
        "Train" -> R.string.trait_train
        "Traitor" -> R.string.trait_traitor
        "Trap" -> R.string.trait_trap
        "Trick" -> R.string.trait_trick
        "Tulgey Wood" -> R.string.trait_tulgey_wood
        "Unbroken" -> R.string.trait_unbroken
        "Uncharted" -> R.string.trait_uncharted
        "Underground" -> R.string.trait_underground
        "Unhallowed" -> R.string.trait_unhallowed
        "Unpracticed" -> R.string.trait_unpracticed
        "Unstable" -> R.string.trait_unstable
        "Upgrade" -> R.string.trait_upgrade
        "Upper Floor" -> R.string.trait_upper_floor
        "Unlit" -> R.string.trait_unlit
        "Vale" -> R.string.trait_vale
        "Vault" -> R.string.trait_vault
        "Vehicle" -> R.string.trait_vehicle
        "Venice" -> R.string.trait_venice
        "Veteran" -> R.string.trait_veteran
        "Virtual" -> R.string.trait_virtual
        "Void" -> R.string.trait_void
        "Walkway" -> R.string.trait_walkway
        "Warden" -> R.string.trait_warden
        "Wastes" -> R.string.trait_wastes
        "Wayfarer" -> R.string.trait_wayfarer
        "Weapon" -> R.string.trait_weapon
        "Weather" -> R.string.trait_weather

        "What? No" -> R.string.trait_what_no
        "Stop" -> R.string.trait_stop
        "That's bad" -> R.string.trait_thats_bad
        "Bad dog" -> R.string.trait_bad_dog

        "Wilderness" -> R.string.trait_wilderness
        "Witch" -> R.string.trait_witch
        "Witch House" -> R.string.trait_witch_house
        "Wonderland" -> R.string.trait_wonderland
        "Woods" -> R.string.trait_woods
        "Worker" -> R.string.trait_worker
        "Y'ha-nthlei" -> R.string.trait_y_ha_nthlei
        "Yithian" -> R.string.trait_yithian
        "Yoth" -> R.string.trait_yoth
        "Yuggoth" -> R.string.trait_yuggoth
        "Zoog" -> R.string.trait_zoog

        else -> R.string.unknown
    }

fun getLocalizedSlot(code: String) =
    when (code) {
        "hand" -> R.string.hand
        "hand x2" -> R.string.hand_x2
        "accessory" -> R.string.accessory
        "ally" -> R.string.ally
        "arcane" -> R.string.arcane
        "arcane x2" -> R.string.arcane_x2
        "head" -> R.string.head
        "body" -> R.string.body
        "tarot" -> R.string.tarot
        "other" -> R.string.other
        else -> R.string.unknown
    }

fun getLocalizedUse(code: String) =
    when (code) {
        "aether" -> R.string.uses_aether
        "arrow" -> R.string.uses_arrow
        "ammo" -> R.string.uses_ammo
        "blame" -> R.string.uses_blame
        "bounties" -> R.string.uses_bounties
        "brilliance" -> R.string.uses_brilliance
        "chances" -> R.string.uses_chances
        "charges" -> R.string.uses_charges
        "doses" -> R.string.uses_doses
        "discoveries" -> R.string.uses_discoveries
        "durability" -> R.string.uses_durability
        "evidence" -> R.string.uses_evidence
        "inspiration" -> R.string.uses_inspiration
        "keys" -> R.string.uses_keys
        "leylines" -> R.string.uses_leylines
        "locks" -> R.string.uses_locks
        "obsessions" -> R.string.uses_obsessions
        "obligations" -> R.string.uses_obligations
        "offerings" -> R.string.uses_offerings
        "poems" -> R.string.uses_poems
        "portents" -> R.string.uses_portents
        "portions" -> R.string.uses_portions
        "resources" -> R.string.uses_resources
        "renown" -> R.string.uses_renown
        "rumors" -> R.string.uses_rumors
        "samples" -> R.string.uses_samples
        "secrets" -> R.string.uses_secrets
        "signs" -> R.string.uses_signs
        "supplies" -> R.string.uses_supplies
        "shards" -> R.string.uses_shards
        "shell" -> R.string.uses_shell
        "tickets" -> R.string.uses_tickets
        "time" -> R.string.uses_time
        "treats" -> R.string.uses_treats
        "tries" -> R.string.uses_tries
        "truths" -> R.string.uses_truths
        "wishes" -> R.string.uses_wishes
        "whistles" -> R.string.uses_whistles
        else -> R.string.unknown
    }

fun getLocalizedSkill(code: String) =
    when (code) {
        "agility" -> R.string.agility
        "combat" -> R.string.combat
        "intellect" -> R.string.intellect
        "willpower" -> R.string.willpower
        else -> R.string.unknown
    }