import Foundation
import Observation
import Shared

@Observable
class FavoritesViewModel {

    // MARK: - State
    var favorites: [WallpaperUi] = []
    var isLoading: Bool = true
    var errorMessage: String? = nil

    private let repository: WallpaperRepository

    private var observationTask: Task<Void, Never>? = nil

    init() {
        self.repository = iOSApp.dependencies.wallpaperRepository
    }

    // MARK: - Lifecycle

    func startObserving() {
        stopObserving()

        isLoading = true

        observationTask = Task { @MainActor in
            for await wallpapers in repository.observeFavorites() {
                let uiList = wallpapers.map {
                    $0.toUi()
                }

                self.favorites = uiList
                self.isLoading = false
            }
        }
    }

    func stopObserving() {
        observationTask?.cancel()
        observationTask = nil
    }

    // MARK: - Intents

    func removeFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                try await repository.removeFavorite(wallpaperId: wallpaper.id)
            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }
}
