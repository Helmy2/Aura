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

    // Equatable conformance
    static func ==(lhs: WallpaperUi, rhs: WallpaperUi) -> Bool {
        return lhs.id == rhs.id
    }

    // Hashable conformance
    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}

extension WallpaperUi {
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
            )
    }
}

extension Wallpaper {
    func toUi(isFavorite: Bool) -> WallpaperUi {
        return WallpaperUi(
            id: self.id,
            imageUrl: self.imageUrl,
            smallImageUrl: self.smallImageUrl,
            photographerName: self.photographer,
            photographerUrl: self.photographerUrl,
            averageColor: self.averageColor,
            height: Int(self.height),
            width: Int(self.width),
            isFavorite: isFavorite
        )
    }
}
