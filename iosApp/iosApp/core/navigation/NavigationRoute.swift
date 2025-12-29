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
    case detail(wallpaper: WallpaperUi)

    // Hashable conformance
    func hash(into hasher: inout Hasher) {
        switch self {
        case .home:
            hasher.combine("home")
        case .settings:
            hasher.combine("settings")
        case .favorites:
            hasher.combine("favorites")
        case .detail(let wallpaper):
            hasher.combine("detail")
            hasher.combine(wallpaper.id)
        }
    }

    static func ==(lhs: NavigationRoute, rhs: NavigationRoute) -> Bool {
        switch (lhs, rhs) {
        case (.home, .home):
            return true
        case (.favorites, .favorites):
            return true
        case (.settings, .settings):
            return true
        case (.detail(let lhs), .detail(let rhs)):
            return lhs.id == rhs.id
        default:
            return false
        }
    }
}
