//
//  WallpaperUi.swift
//  iosApp
//
//  Created by platinum on 26/12/2025.
//

import Foundation
import Shared
import SwiftUI

struct WallpaperUi: Identifiable, Equatable, Hashable {
    let id: Int64
    let imageUrl: String
    let smallImageUrl: String
    let photographerName: String
    let photographerUrl: String
    let averageColor: String
    let height: Int
    let width: Int
    var isFavorite: Bool
}

extension WallpaperUi {
    func copy(isFavorite: Bool? = nil) -> WallpaperUi {
        return WallpaperUi(
            id: self.id,
            imageUrl: self.imageUrl,
            smallImageUrl: self.smallImageUrl,
            photographerName: self.photographerName,
            photographerUrl: self.photographerUrl,
            averageColor: self.averageColor,
            height: self.height,
            width: self.width,
            isFavorite: isFavorite ?? self.isFavorite
        )
    }

    func toDomain() -> Wallpaper {
        return Wallpaper(
            id: self.id,
            imageUrl: self.imageUrl,
            smallImageUrl: self.smallImageUrl,
            photographer: self.photographerName,
            photographerUrl: self.photographerUrl,
            averageColor: self.averageColor,
            height: Int32(self.height),
            width: Int32(self.width),
            isFavorite: isFavorite
        )
    }
}

extension Wallpaper {
    func toUi() -> WallpaperUi {
        return WallpaperUi(
            id: self.id,
            imageUrl: self.imageUrl,
            smallImageUrl: self.smallImageUrl,
            photographerName: self.photographer,
            photographerUrl: self.photographerUrl,
            averageColor: self.averageColor,
            height: Int(self.height),
            width: Int(self.width),
            isFavorite: self.isFavorite
        )
    }
}
