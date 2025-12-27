import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinHelperKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    @State private var coordinator = NavigationCoordinator()

    var body: some View {
        TabView(selection: Binding(
            get: { coordinator.selectedTab },
            set: { coordinator.switchToTab($0) }
        )) {
            HomeNavigationStack(coordinator: coordinator)
            .tabItem {
                Label("Home", systemImage: coordinator.selectedTab == .home ? "house.fill" : "house")
            }
            .tag(NavigationCoordinator.Tab.home)

            FavoritesNavigationStack(coordinator: coordinator)
            .tabItem {
                Label("Favorites", systemImage: coordinator.selectedTab == .favorites ? "heart.fill" : "heart")
            }
            .tag(NavigationCoordinator.Tab.favorites)
        }
        .tint(.pink)
    }
}

// MARK: - Home Navigation Stack
struct HomeNavigationStack: View {
    @Bindable var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            HomeView(coordinator: coordinator)
                .navigationDestination(for: NavigationRoute.self) { route in
                    switch route {
                    case .detail(let wallpaper):
                        DetailView(wallpaper: wallpaper, coordinator: coordinator)
                    default:
                        EmptyView()
                    }
                }
        }
    }
}

// MARK: - Favorites Navigation Stack
struct FavoritesNavigationStack: View {
    @Bindable var coordinator: NavigationCoordinator

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            FavoritesView(coordinator: coordinator)
                .navigationDestination(for: NavigationRoute.self) { route in
                    switch route {
                    case .detail(let wallpaper):
                        DetailView(wallpaper: wallpaper, coordinator: coordinator)
                    default:
                        EmptyView()
                    }
                }
        }
    }
}
