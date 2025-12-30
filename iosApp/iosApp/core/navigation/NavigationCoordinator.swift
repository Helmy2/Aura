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

    func navigateToDetail(wallpaper: WallpaperUi) {
        path.append(NavigationRoute.wallpaperDetail(wallpaper))
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
