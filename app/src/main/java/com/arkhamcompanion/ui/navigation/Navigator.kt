package com.arkhamcompanion.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey, launchSingleTop: Boolean = false) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
            return
        }

        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")

        if (launchSingleTop && currentStack.lastOrNull() == route) {
            return
        }

        currentStack.add(route)
    }

    fun goBack(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, don't navigate.
        return if (currentRoute == state.topLevelRoute) {
            false
        } else {
            currentStack.removeLastOrNull() != null
        }
    }

    fun goBackToTopLevelRoute(topLevelRoute: NavKey) {
        val stack = state.backStacks[topLevelRoute]
            ?: error("Stack for $topLevelRoute not found")

        if (stack.size > 1) {
            stack.subList(1, stack.size).clear()
        }
    }
}

internal fun Navigator.navigateSingleTop(route: NavKey) = navigate(route, launchSingleTop = true)
