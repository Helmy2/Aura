import Shared
import SwiftUI

struct HomeView: View {
    @State private var viewModel = HomeViewModel()
    let coordinator: NavigationCoordinator

    private let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
    ]

    var body: some View {
        content
            .navigationBarHidden(true)
            .onAppear {
                if viewModel.wallpapers.isEmpty && !viewModel.isLoading {
                    viewModel.loadCuratedWallpapers(reset: true)
                }
            }
    }

    // MARK: - Subviews
    private var content: some View {
        VStack(spacing: 0) {
            searchBarSection

            if viewModel.isLoading && viewModel.wallpapers.isEmpty {
                loadingSpinner
            } else {
                wallpaperGridScrollView
            }
        }
    }

    private var searchBarSection: some View {
        SearchBarView(
            text: $viewModel.searchQuery,
            isSearchActive: viewModel.isSearchMode,
            onSearch: { viewModel.onSearchTriggered() },
            onClear: { viewModel.onClearSearch() }
        )
        .padding(.horizontal)
        .padding(.top, 8)
        .padding(.bottom, 8)
    }

    private var loadingSpinner: some View {
        ProgressView()
            .scaleEffect(1.5)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private var wallpaperGridScrollView: some View {
        ScrollView {
            wallpaperGrid
                .padding(.horizontal)
                .padding(.top, 8)
        }
    }

    private var wallpaperGrid: some View {
        LazyVGrid(columns: columns, spacing: 16) {
            let list =
                viewModel.isSearchMode
                    ? viewModel.searchWallpapers : viewModel.wallpapers
            ForEach(list, id: \.id) { wallpaper in
                WallpaperGridCell(
                    wallpaper: wallpaper,
                    onTap: {
                        coordinator.navigateToDetail(wallpaper: wallpaper)
                    },
                    onFavoriteToggle: {
                        viewModel.toggleFavorite(wallpaper: wallpaper)
                    }
                )
                .onAppear {
                    if wallpaper == list.last {
                        viewModel.loadNextPage()
                    }
                }
            }

            if viewModel.isPaginationLoading {
                paginationLoader
            }
        }
    }

    private var paginationLoader: some View {
        ProgressView()
            .frame(maxWidth: .infinity)
            .padding()
    }
}
