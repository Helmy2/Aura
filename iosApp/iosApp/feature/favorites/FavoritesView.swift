import Shared
import SwiftUI

struct FavoritesView: View {
    @State private var viewModel = FavoritesViewModel()
    let coordinator: NavigationCoordinator

    private let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
    ]

    var body: some View {
        content
            .navigationTitle("Favorites")
            .navigationBarTitleDisplayMode(.large)
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
                ForEach(viewModel.favorites, id: \.id) { wallpaper in
                    WallpaperGridCell(
                        wallpaper: wallpaper,
                        onTap: {
                            coordinator.navigateToDetail(wallpaper: wallpaper)
                        },
                        onFavoriteToggle: {
                            viewModel.removeFavorite(wallpaper: wallpaper)
                        }
                    )
                }
            }
            .padding(.horizontal)
            .padding(.top, 8)
        }
    }
}
