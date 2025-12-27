//
//  DetailViewModel.swift
//  iosApp
//
//  Created by platinum on 25/12/2025.
//

import Foundation
import SwiftUI
import Observation
import Shared

@Observable
class DetailViewModel {
    var isDownloading: Bool = false
    var showToast: Bool = false
    var toastMessage: String = ""
    var isFavorite: Bool = false
    
    private let downloader = ImageDownloader()
    private let favoritesRepository: FavoritesRepository

    init() {
        self.favoritesRepository = KoinHelper().favoritesRepository
    }

    func loadFavoriteStatus(wallpaperId: Int64) {
        Task {
            do {
                self.isFavorite = try await favoritesRepository.isFavorite(wallpaperId: wallpaperId).boolValue
            } catch {
                // Silent fail
            }
        }
    }

    func toggleFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                let kmWallpaper = wallpaper.toDomain()
                try await favoritesRepository.toggleFavorite(wallpaper: kmWallpaper)
                self.isFavorite.toggle()
                self.toastMessage = isFavorite ? "Added to favorites" : "Removed from favorites"
                self.showToast = true
            } catch {
                self.toastMessage = "Failed to update favorite"
                self.showToast = true
            }
        }
    }
    
    func downloadWallpaper(url: String) {
        guard !isDownloading else { return }
        self.isDownloading = true
        Task {
            let success = await downloader.downloadAndSave(url: url)
            self.isDownloading = false
            self.toastMessage = success ? "Saved to Photos" : "Save Failed"
            self.showToast = true
        }
    }
}
