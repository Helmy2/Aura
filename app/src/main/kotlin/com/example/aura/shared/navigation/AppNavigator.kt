package com.example.aura.shared.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

/**
 * Manages the app's back stack and top-level navigation state using an observable list.
 */
class AppNavigator(
    startDestination: NavKey = HomeRoute,
){
    /**
     * Observable history of visited destinations. The last item is the active screen.
     * Exposed as [mutableStateListOf] for Compose reactivity.
     */
    val backStack = mutableStateListOf(startDestination)

    private val backStackStateKey = "key_nav_back_stack"

    /**
     * Pushes [destination] to [backStack].
     *
     * @param destination The destination to navigate to.
     * @param singleTop If true (default), replaces the current screen if it's the same class
     *                  to prevent duplicate stacking (e.g., double-tap protection).
     */
    fun navigate(
        destination: NavKey,
        singleTop: Boolean = true,
    ) {
        val current = backStack.lastOrNull()

        if (singleTop && current != null && current::class == destination::class) {
            backStack[backStack.lastIndex] = destination
        } else {
            backStack.add(destination)
        }
    }

    /**
     * Clears the entire back stack and sets [destination] as the new root.
     *
     * **Use Case:** Logging out, completing a checkout flow, or resetting the app state.
     */
    fun navigateAsStart(destination: NavKey) {
        backStack.clear()
        backStack.add(destination)
    }

    /** Safe pop of the last destination. Does nothing if only one screen remains. */
    fun back() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }


    /**
     * Switches to a top-level root (Bottom Nav Tab).
     * - If re-selecting the current tab: Pops to root.
     * - If switching tabs: Resets stack to the new root.
     */
    fun navigateToTopLevel(destination: NavKey) {
        if (backStack.firstOrNull() == destination) {
            // Re-selected current tab: Pop everything above the root
            if (backStack.size > 1) {
                backStack.removeRange(1, backStack.size)
            }
        } else {
            // Switch tab: Reset stack completely
            backStack.clear()
            backStack.add(HomeRoute)
            backStack.add(destination)
        }
    }
}
