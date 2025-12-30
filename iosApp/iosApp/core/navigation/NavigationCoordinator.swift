import SwiftUI
import Shared
import Observation

@MainActor
class NavigationCoordinator: ObservableObject {
    @Published var path = NavigationPath()
    @Published var selectedTab: Tab = .home

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
