import Foundation
import Observation
import Shared

@Observable
class FavoritesViewModel {

    // MARK: - State
    var favorites: [MediaContentUi] = []
    var isLoading: Bool = true
    var errorMessage: String? = nil

    private let wallpaperRepository: WallpaperRepository
    private let favoritesRepository: FavoritesRepository
    private let videoRepository: VideoRepository

    private var observationTask: Task<Void, Never>? = nil

    init() {
        self.wallpaperRepository = iOSApp.dependencies.wallpaperRepository
        self.favoritesRepository = iOSApp.dependencies.favoritesRepository
        self.videoRepository = iOSApp.dependencies.videoRepository
    }

    // MARK: - Lifecycle

    func startObserving() {
        stopObserving()

        isLoading = true

        observationTask = Task { @MainActor in
            for await items in favoritesRepository.observeFavorites() {
                self.favorites = items.map {
                    $0.toUi()
                }
                self.isLoading = false
            }
        }
    }

    func stopObserving() {
        observationTask?.cancel()
        observationTask = nil
    }

    // MARK: - Intents

    func toggleFavorite(video: VideoUi) {
        Task {
            do {
                let domainVideo = try await videoRepository.getVideoById(id: video.id)
                try await favoritesRepository.toggleFavorite(video: domainVideo)
            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }

    func toggleFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                let domainWallpaper = try await wallpaperRepository.getWallpaperById(id: wallpaper.id)
                try await favoritesRepository.toggleFavorite(wallpaper: domainWallpaper)
            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }
}
