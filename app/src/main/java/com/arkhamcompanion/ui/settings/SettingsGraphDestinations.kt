package com.arkhamcompanion.ui.settings

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object SettingsCollection : NavKey

@Serializable
object SettingsRewards : NavKey

@Serializable
object SettingsRules : NavKey

@Serializable
data class RulesSection(
    val id: String
) : NavKey

@Serializable
object SettingsAbout : NavKey

@Serializable
object SettingsBackup : NavKey

@Serializable
object SettingsDiagnostics : NavKey