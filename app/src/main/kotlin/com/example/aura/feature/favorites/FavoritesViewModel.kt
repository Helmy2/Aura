package com.example.aura.feature.favorites

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.model.MediaContent
import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.Destination
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val navigator: AppNavigator
) : MviViewModel<FavoritesState, FavoritesIntent, FavoritesEffect>(
    initialState = FavoritesState()
) {

    init {
        observeFavorites()
    }

    override fun reduce(
        currentState: FavoritesState, intent: FavoritesIntent
    ): Pair<FavoritesState, FavoritesEffect?> {
        return when (intent) {
            is FavoritesIntent.LoadFavorites -> currentState.copy(isLoading = true).only()

            is FavoritesIntent.FavoritesLoaded -> currentState.copy(
                items = intent.items, isLoading = false, error = null
            ).only()

            is FavoritesIntent.FavoritesLoadError -> currentState.copy(
                isLoading = false, error = intent.message
            ).with(FavoritesEffect.ShowError(intent.message))

            is FavoritesIntent.RemoveFormFavorite -> {
                removeItemFromFavorite(intent.item)
                currentState.only()
            }

            is FavoritesIntent.RemoveError -> currentState.with(
                FavoritesEffect.ShowError(
                    intent.message
                )
            )

            is FavoritesIntent.OnItemClicked -> {
                when (intent.item) {
                    is MediaContent.VideoContent -> navigator.navigate(
                        Destination.VideoDetail(intent.item.video)
                    )

                    is MediaContent.WallpaperContent -> navigator.navigate(
                        Destination.WallpaperDetail(intent.item.wallpaper)
                    )
                }
                currentState.only()
            }

            else -> currentState.only()
        }
    }

    private fun removeItemFromFavorite(item: MediaContent) {
        viewModelScope.launch {
            try {
                favoritesRepository.removeFromFavorite(item)
            } catch (e: Exception) {
                sendIntent(FavoritesIntent.RemoveError(e.message ?: "Failed to remove"))
            }
        }
    }

    private fun observeFavorites() {
        favoritesRepository.observeFavorites().onEach {
            sendIntent(FavoritesIntent.FavoritesLoaded(it))
        }.catch { e ->
            sendIntent(FavoritesIntent.FavoritesLoadError(e.message ?: "Unknown error"))
        }.launchIn(viewModelScope)
    }
}
