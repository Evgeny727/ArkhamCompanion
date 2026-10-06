package com.arkhamcompanion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.ViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.SaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.savedstate.compose.LocalSavedStateRegistryOwner

/**
 * Returns a [SharedViewModelStoreNavEntryDecorator] that is remembered across recompositions.
 *
 * @param [viewModelStoreOwner] The [ViewModelStoreOwner] that provides the [ViewModelStore] to
 *   NavEntries
 */
@Composable
fun <T : Any> rememberSharedViewModelStoreNavEntryDecorator(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
): SharedViewModelStoreNavEntryDecorator<T> {
    val viewModelStoreProvider = rememberViewModelStoreProvider(viewModelStoreOwner)
    return remember(viewModelStoreOwner) {
        SharedViewModelStoreNavEntryDecorator(viewModelStoreProvider)
    }
}

/**
 * Provides the content of a [NavEntry] with a new [ViewModelStoreOwner] and provides that
 * [ViewModelStoreOwner] as a [LocalViewModelStoreOwner] so that it is available within the content.
 *
 * If the [NavEntry] specifies that it has a parent in its metadata, the parent's
 * [ViewModelStoreOwner] will also be supplied along with the new one. This allows the
 * entry to access both its own [ViewModel] and its parent's [ViewModel]s.
 *
 * This requires the usage of [SaveableStateHolderNavEntryDecorator] to ensure that the [NavEntry]
 * scoped [ViewModel]s can properly provide access to [androidx.lifecycle.SavedStateHandle]s.
 *
 * @see [SharedViewModelStoreNavEntryDecorator.parent]
 *
 * @param [viewModelStoreProvider] The [ViewModelStoreProvider] scoped to
 * the parent [ViewModelStoreOwner]
 */
class SharedViewModelStoreNavEntryDecorator<T : Any>(
    viewModelStoreProvider: ViewModelStoreProvider
) : NavEntryDecorator<T>(
    onPop = { key -> viewModelStoreProvider.clearKey(key) },
    decorate = { entry ->
        val localContentKey = entry.contentKey
        val localOwner =
            rememberViewModelStoreOwner(
                localContentKey,
                viewModelStoreProvider,
                savedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
            )

        val localValues: MutableList<ProvidedValue<*>> = mutableListOf(LocalViewModelStoreOwner provides localOwner)

        // If the entry indicates it has a parent, also provide its parent's ViewModelStore
        val parentContentKey = entry.metadata[ParentKey]
        if (parentContentKey != null) {
            val parentOwner = rememberViewModelStoreOwner(
                parentContentKey,
                viewModelStoreProvider,
                savedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
            )

            localValues.add(LocalSharedViewModelStoreOwner provides parentOwner)
        }

        // If the entry indicates it has a graph, also provide its graph's ViewModelStore
        val graphContentKey = entry.metadata[GraphKey]
        if (graphContentKey != null) {
            val graphOwner = rememberViewModelStoreOwner(
                graphContentKey,
                viewModelStoreProvider,
                savedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
            )

            localValues.add(LocalGraphSharedViewModelStoreOwner provides graphOwner)
        }

        CompositionLocalProvider(
            values = localValues.toTypedArray()
        ) { entry.Content() }
    },
) {
    companion object {
        /**
         * Use this function to specify a `NavEntry`'s parent. The parent's
         * `ViewModelStoreOwner` will be supplied via `LocalSharedViewModelStoreOwner`
         */
        fun parent(key: Any) = metadata {
            put(ParentKey, key)
        }

        fun graphWithParent(graphKey: Any, parentKey: Any) = metadata {
            put(ParentKey, parentKey)
            put(GraphKey, graphKey)
        }

        object ParentKey : NavMetadataKey<Any>
        object GraphKey : NavMetadataKey<Any>
    }
}

fun NavKey.toContentKey() = this.toString()

val LocalSharedViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("No LocalSharedViewModelStoreOwner provided!")
}

val LocalGraphSharedViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("No LocalGraphSharedViewModelStoreOwner provided")
}