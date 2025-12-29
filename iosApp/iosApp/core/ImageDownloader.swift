import UIKit
import Photos

class ImageDownloader {

    // Singleton instance (optional, but good for reuse)
    static let shared = ImageDownloader()
    
    /// Downloads image from URL and saves to Photo Library.
    /// Returns true if successful, throws error otherwise.
    func downloadAndSave(url: String) async throws {
        guard let imageUrl = URL(string: url) else {
            throw URLError(.badURL)
        }

        // 1. Download Async using URLSession (Non-blocking)
        let (data, _) = try await URLSession.shared.data(from: imageUrl)

        guard let image = UIImage(data: data) else {
            throw URLError(.cannotDecodeContentData)
        }

        // 2. Save to Photos
        try await saveToPhotoLibrary(image)
    }

    private func saveToPhotoLibrary(_ image: UIImage) async throws {
        // Check/Request Permission
        let status = await PHPhotoLibrary.requestAuthorization(for: .addOnly)

        guard status == .authorized || status == .limited else {
            throw ImageDownloadError.permissionDenied
        }

        // Perform Save
        try await PHPhotoLibrary.shared().performChanges {
            PHAssetChangeRequest.creationRequestForAsset(from: image)
        }
    }
}

// Custom Error Type
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
