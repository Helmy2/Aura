import Shared
import SwiftUI

struct FavoritesView: View {
    @State private var viewModel = FavoritesViewModel()
    @EnvironmentObject var coordinator: NavigationCoordinator

    private let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
    ]

    var body: some View {
        content
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.stopObserving()
        }
    }

    // MARK: - Subviews
    private var content: some View {
        Group {
            if viewModel.isLoading {
                loadingSpinner
            } else if viewModel.favorites.isEmpty {
                emptyStateView
            } else {
                favoritesGrid
            }
        }
    }

    private var loadingSpinner: some View {
        ProgressView()
            .scaleEffect(1.5)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private var emptyStateView: some View {
        VStack(spacing: 16) {
            Image(systemName: "heart.fill")
                .font(.system(size: 80))
                .foregroundStyle(.pink.opacity(0.3))

            Text("No favorites yet")
                .font(.title2)
                .fontWeight(.semibold)
                .foregroundStyle(.secondary)

            Text(
                "Start adding wallpapers to your favorites\nby tapping the heart icon"
            )
                .font(.body)
                .foregroundStyle(.tertiary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private var favoritesGrid: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(viewModel.favorites, id: \.id) { item in
                    MediaContentGridCell(
                        content: item,
                        onRemoveFavorite: {
                            switch item {
                            case .wallpaper(let wallpaper):
                                viewModel.toggleFavorite(wallpaper: wallpaper)
                            case .video(let video):
                                viewModel.toggleFavorite(video: video)
                            }
                        },
                        onNavigate: {
                            switch item {
                            case .wallpaper(let wallpaper):
                                coordinator.navigateToWallpaperDetail(
                                    wallpaper: wallpaper
                                ) { w in
                                    viewModel.toggleFavorite(wallpaper: w)
                                }
                            case .video(let video):
                                coordinator.navigateToVideoDetail(video: video) { w in
                                    viewModel.toggleFavorite(video: w)
                                }
                            }
                        }
                    )
                }
            }
            .padding(.horizontal)
            .padding(.top, 8)
        }
    }
}

struct MediaContentGridCell: View {
    let content: MediaContentUi
    var onRemoveFavorite: () -> Void
    var onNavigate: () -> Void

    var body: some View {
        switch content {
        case .wallpaper(let wallpaper):
            WallpaperGridCell(
                wallpaper: wallpaper,
                onTap: onNavigate,
                onFavoriteToggle: onRemoveFavorite,
                )

        case .video(let video):
            VideoGridCell(
                video: video,
                onTap: onNavigate,
                onFavoriteToggle: onRemoveFavorite,
                )
        }
    }
}
