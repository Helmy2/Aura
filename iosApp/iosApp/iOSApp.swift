import Shared
import SwiftUI

@main
struct iOSApp: App {
    @State private var settingsViewModel: SettingsViewModel

    init() {
        KoinHelperKt.doInitKoin()
        settingsViewModel = SettingsViewModel()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .preferredColorScheme(
                    getColorScheme(for: settingsViewModel.themeMode)
                )
        }
    }

    private func getColorScheme(for mode: ThemeMode) -> ColorScheme? {
        switch mode {
        case .system: return nil
        case .light: return .light
        case .dark: return .dark
        }
    }
}

struct ContentView: View {
    @State private var coordinator = NavigationCoordinator()

    var body: some View {
        ZStack(alignment: .bottom) {

            Group {
                switch coordinator.selectedTab {
                case .home:
                    HomeNavigationStack(coordinator: coordinator)
                case .videos:
                    VideosNavigationStack(coordinator: coordinator)
                case .favorites:
                    FavoritesNavigationStack(coordinator: coordinator)
                case .settings:
                    SettingNavigationStack(coordinator: coordinator)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            if coordinator.path.isEmpty {
                AuraTabBar(selectedTab: $coordinator.selectedTab)
                    .padding(.bottom, 10)
                    .transition(.move(edge: .bottom).combined(with: .opacity))

            }
        }
        .ignoresSafeArea()
    }
}

// MARK: - Navigation Stacks

struct HomeNavigationStack: View {
    @Bindable var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            HomeView(coordinator: coordinator)
                .navigationDestination(for: NavigationRoute.self) { route in
                    switch route {
                    case .detail(let wallpaper):
                        DetailView(
                            wallpaper: wallpaper,
                            coordinator: coordinator
                        )
                            .toolbar(.hidden, for: .navigationBar)
                    default: EmptyView()
                    }
                }
        }
    }
}

struct FavoritesNavigationStack: View {
    @Bindable var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            FavoritesView(coordinator: coordinator)
                .navigationDestination(for: NavigationRoute.self) { route in
                    switch route {
                    case .detail(let wallpaper):
                        DetailView(
                            wallpaper: wallpaper,
                            coordinator: coordinator
                        )
                            .toolbar(.hidden, for: .navigationBar)
                    default: EmptyView()
                    }
                }
        }
    }
}

struct SettingNavigationStack: View {
    @Bindable var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            SettingsView()
        }
    }
}

struct VideosNavigationStack: View {
    @Bindable var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            VideosView(coordinator: coordinator)
                .navigationDestination(for: NavigationRoute.self) { route in
                    switch route {
                    case .videoDetail(let video):
                        VideoDetailView(
                            video: video
                        ).toolbar(.hidden, for: .navigationBar)
                    default: EmptyView()
                    }
                }
        }
    }
}
