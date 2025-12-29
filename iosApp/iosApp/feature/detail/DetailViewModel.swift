import Foundation
import Observation
import Shared
import SwiftUI

@MainActor
@Observable
class DetailViewModel {
    var isFavorite: Bool = false
    var downloadState: DownloadState = .idle

    private let imageDownloader = ImageDownloader()
    private let repository: WallpaperRepository

    init() {
        self.repository = KoinHelper().wallpaperRepository
    }

    func loadFavoriteStatus(wallpaperId: Int64) {
        Task {
            do {
                self.isFavorite = try await repository.isFavorite(
                    wallpaperId: wallpaperId
                ).boolValue
            } catch {
                print("Failed to load favorite status: \(error)")
            }
        }
    }

    func toggleFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                let kmWallpaper = wallpaper.toDomain()
                try await repository.toggleFavorite(wallpaper: kmWallpaper)
                self.isFavorite.toggle()
            }
        }
    }

    func downloadWallpaper(url: String) {
        Task {
            downloadState = .downloading

            do {
                try await imageDownloader.downloadAndSave(url: url)

                downloadState = .success
                triggerHaptic(type: .success)

                try? await Task.sleep(nanoseconds: 2 * 1_000_000_000)
                downloadState = .idle

            } catch {
                print("Download failed: \(error.localizedDescription)")
                downloadState = .failed
                triggerHaptic(type: .error)

                try? await Task.sleep(nanoseconds: 2 * 1_000_000_000)
                downloadState = .idle
            }
        }
    }

    private func triggerHaptic(
        type: UINotificationFeedbackGenerator.FeedbackType
    ) {
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(type)
    }

}

enum DownloadState {
    case idle
    case downloading
    case success
    case failed
}
