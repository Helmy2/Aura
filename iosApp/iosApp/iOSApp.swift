import Shared
import SwiftUI


@main
struct iOSApp: App {
    @StateObject private var coordinator = NavigationCoordinator()
    @State private var settingsViewModel: SettingsViewModel
    static let dependencies: DependenciesHelper = DependenciesHelper()
    
    init() {
        KoinHelperKt.doInitKoin()
        settingsViewModel = SettingsViewModel()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(coordinator)
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
    @EnvironmentObject var coordinator: NavigationCoordinator

    var body: some View {
        ZStack(alignment: .bottom) {

            Group {
                switch coordinator.selectedTab {
                case .home:
                    HomeNavigationStack()
                case .videos:
                    VideosNavigationStack()
                case .favorites:
                    FavoritesNavigationStack()
                case .settings:
                    SettingNavigationStack()
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
    @EnvironmentObject var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            HomeView()
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
    @EnvironmentObject var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            FavoritesView()
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
    @EnvironmentObject var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            SettingsView()
        }
    }
}

struct VideosNavigationStack: View {
    @EnvironmentObject var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            VideosView()
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
