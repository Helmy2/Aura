import Observation
import Shared
import SwiftUI

@MainActor
class NavigationCoordinator: ObservableObject {
    @Published var path = NavigationPath()
    @Published var selectedTab: Tab = .home

    enum Tab {
        case home
        case favorites
        case settings
    }

    func navigateToWallpaperList() {
        path.append(NavigationRoute.wallpaperList)
    }

    func navigateToVideoList() {
        path.append(NavigationRoute.videoList)
    }

    func navigateToWallpaperDetail(wallpaper: WallpaperUi, onToggle: @escaping (WallpaperUi) -> Void) {
        path.append(NavigationRoute.wallpaperDetail(wallpaper, onToggle))
    }

    func navigateToVideoDetail(video: VideoUi, onToggle: @escaping (VideoUi) -> Void) {
        path.append(NavigationRoute.videoDetail(video, onToggle))
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
