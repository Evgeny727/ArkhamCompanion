package com.arkhamcompanion.ui.decks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkhamcompanion.R
import com.arkhamcompanion.ui.components.ArkhamCheckboxButton
import com.arkhamcompanion.ui.components.ArkhamSearchBox
import com.arkhamcompanion.ui.theme.CustomTheme
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun DecksFilersTagsScreen(
    selectedTags: ImmutableSet<String>,
    allTags: ImmutableSet<String>?,
    onTagToggle: (String) -> Unit,
    resolveFactionTag: (String) -> String,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredTags = remember(allTags, searchQuery) {
        val query = searchQuery.trim()

        allTags.orEmpty()
            .map {
                it to resolveFactionTag(it)
            }
            .filter { (code, name) ->
                query.isEmpty() || name.contains(query, ignoreCase = true)
                        || code.contains(query, ignoreCase = true)
            }
            .sortedBy { (_, name) -> name }
    }

    Column(modifier = modifier.fillMaxSize()) {
        ArkhamSearchBox(
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" },
            searchPlaceholder = stringResource(R.string.search_for_tags)
        )

        AnimatedVisibility(allTags == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = CustomTheme.colors.m
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(
                items = filteredTags,
                key = { it.first },
                contentType = { "tag" }
            ) { (tag, name) ->

                ArkhamCheckboxButton(
                    title = name,
                    isSelected = tag in selectedTags,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) { onTagToggle(tag) }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = CustomTheme.colors.divider)
            }
        }
    }
}