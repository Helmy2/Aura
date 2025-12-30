//
//  NavigationCoordinator.swift
//  iosApp
//
//  Created by platinum on 27/12/2025.
//

import SwiftUI
import Shared
import Observation

@MainActor
@Observable
class NavigationCoordinator {
    var path = NavigationPath()
    var selectedTab: Tab = .home

    enum Tab {
        case home
        case videos
        case favorites
        case settings
    }

    // Navigate to detail
    func navigateToDetail(wallpaper: WallpaperUi) {
        path.append(NavigationRoute.detail(wallpaper))
    }

    func navigateToVideoDetail(video: VideoUi) {
        path.append(NavigationRoute.videoDetail(video))
    }

    // Pop to root
    func popToRoot() {
        path.removeLast(path.count)
    }

    // Pop one level
    func pop() {
        if !path.isEmpty {
            path.removeLast()
        }
    }

    // Switch tab
    func switchToTab(_ tab: Tab) {
        selectedTab = tab
        popToRoot()
    }


}
