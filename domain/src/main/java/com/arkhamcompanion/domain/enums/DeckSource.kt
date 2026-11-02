package com.arkhamcompanion.domain.enums

enum class DeckSource {
    Account, Arkhamdb, Device;

    companion object {
        fun valueOf(source: String? = null): DeckSource = when (source) {
            "account" -> Account
            "arkhamdb" -> Arkhamdb
            else -> Device
        }
    }
}