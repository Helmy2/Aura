//
//  NavigationRoute.swift
//  iosApp
//
//  Created by platinum on 27/12/2025.
//

import Foundation
import Shared

enum NavigationRoute: Hashable {
    case home
    case favorites
    case settings
    case wallpaperDetail(WallpaperUi)
    case videoDetail(VideoUi)
    case wallpaperList
    case videoList

    // Hashable conformance
    func hash(into hasher: inout Hasher) {
        switch self {
        case .home:
            hasher.combine("home")
        case .settings:
            hasher.combine("settings")
        case .favorites:
            hasher.combine("favorites")
        case .videoList:
            hasher.combine("videoList")
        case .wallpaperList:
            hasher.combine("wallpaperList")
        case .videoDetail:
            hasher.combine("videoDetail")
        case .wallpaperDetail(let wallpaper):
            hasher.combine("wallpaperDetail")
            hasher.combine(wallpaper.id)
        }
    }
}
