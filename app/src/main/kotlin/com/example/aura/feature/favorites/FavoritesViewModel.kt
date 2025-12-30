package com.example.aura.feature.favorites

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.Destination
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val wallpaperRepository: WallpaperRepository, private val navigator: AppNavigator
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
            is FavoritesIntent.LoadFavorites -> {
                currentState.copy(isLoading = true).only()
            }

            is FavoritesIntent.FavoritesLoaded -> {
                currentState.copy(
                    favorites = intent.favorites, isLoading = false, error = null
                ).only()
            }

            is FavoritesIntent.FavoritesLoadError -> {
                currentState.copy(
                    isLoading = false, error = intent.message
                ).with(FavoritesEffect.ShowError(intent.message))
            }

            is FavoritesIntent.RemoveFavorite -> {
                viewModelScope.launch {
                    try {
                        wallpaperRepository.removeFavorite(intent.wallpaper.id)
                    } catch (e: Exception) {
                        sendIntent(
                            FavoritesIntent.RemoveFavoriteError(
                                e.message ?: "Failed to remove favorite"
                            )
                        )
                    }
                }
                currentState.only()
            }

            is FavoritesIntent.RemoveFavoriteError -> {
                currentState.with(FavoritesEffect.ShowError(intent.message))
            }

            is FavoritesIntent.ClearAllFavorites -> {
                viewModelScope.launch {
                    try {
                        currentState.favorites.forEach { wallpaper ->
                            wallpaperRepository.removeFavorite(wallpaper.id)
                        }
                    } catch (e: Exception) {
                        sendIntent(
                            FavoritesIntent.ClearFavoritesError(
                                e.message ?: "Failed to clear favorites"
                            )
                        )
                    }
                }
                currentState.only()
            }

            is FavoritesIntent.ClearFavoritesError -> {
                currentState.with(FavoritesEffect.ShowError(intent.message))
            }

            is FavoritesIntent.OnWallpaperClicked -> {
                navigator.navigate(
                    Destination.WallpaperDetail(wallpaper = intent.wallpaper)
                )
                currentState.only()
            }
        }
    }

    private fun observeFavorites() {
        wallpaperRepository.observeFavorites().onEach { favorites ->
            sendIntent(
                FavoritesIntent.FavoritesLoaded(
                    favorites.map { it.toUi(isFavorite = true) })
            )
        }.catch { throwable ->
                sendIntent(
                    FavoritesIntent.FavoritesLoadError(
                        throwable.message ?: "Unknown error"
                    )
                )
        }.launchIn(viewModelScope)
    }
}
