import Foundation
import Photos
import Observation
import Shared
import SwiftUI

@Observable
class WallpaperDetailViewModel {
    var isFavorite: Bool = false
    var downloadState: DownloadState = .idle

    private let repository: WallpaperRepository

    init() {
        self.repository = iOSApp.dependencies.wallpaperRepository
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
                try await downloadAndSave(url: url)

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

    func downloadAndSave(url: String) async throws {
        guard let imageUrl = URL(string: url) else {
            throw URLError(.badURL)
        }

        let (data, _) = try await URLSession.shared.data(from: imageUrl)

        guard let image = UIImage(data: data) else {
            throw URLError(.cannotDecodeContentData)
        }

        try await saveToPhotoLibrary(image)
    }

    private func saveToPhotoLibrary(_ image: UIImage) async throws {
        let status = await PHPhotoLibrary.requestAuthorization(for: .addOnly)

        guard status == .authorized || status == .limited else {
            throw ImageDownloadError.permissionDenied
        }

        try await PHPhotoLibrary.shared().performChanges {
            PHAssetChangeRequest.creationRequestForAsset(from: image)
        }
    }
}

enum DownloadState {
    case idle
    case downloading
    case success
    case failed
}

enum ImageDownloadError: Error, LocalizedError {
    case permissionDenied
    case saveFailed

    var errorDescription: String? {
        switch self {
        case .permissionDenied: return "Photo library access denied. Please enable it in Settings."
        case .saveFailed: return "Failed to save photo."
        }
    }
}
