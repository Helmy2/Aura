import Foundation
import Observation
import Shared

@Observable
class HomeViewModel {

    // MARK: - State
    var wallpapers: [WallpaperUi] = []
    var searchWallpapers: [WallpaperUi] = []
    var isLoading: Bool = false
    var isPaginationLoading: Bool = false
    var errorMessage: String? = nil

    // Search State
    var isSearchMode: Bool = false
    var searchQuery: String = ""

    // Pagination State
    private var currentPage: Int = 1
    private var isEndReached: Bool = false

    // Dependencies
    private let repository: WallpaperRepository
    private var favoritesTask: Task<Void, Never>? = nil

    init() {
        self.repository = KoinHelper().wallpaperRepository
        observeFavorites()
        loadCuratedWallpapers(reset: true)
    }
    

    // MARK: - Intents
    func loadCuratedWallpapers(reset: Bool = false) {
        if reset {
            self.isLoading = true
            self.currentPage = 1
            self.wallpapers = []
            self.isEndReached = false
            self.isSearchMode = false
        } else {
            guard !isPaginationLoading && !isEndReached else { return }
            self.isPaginationLoading = true
        }

        performFetch(query: nil, page: currentPage, isSearch: false)
    }

    func onSearchTriggered() {
        guard !searchQuery.isEmpty else { return }
        self.isSearchMode = true
        self.isLoading = true
        self.currentPage = 1
        self.searchWallpapers = []
        self.isEndReached = false
        performFetch(query: searchQuery, page: 1, isSearch: true)
    }

    func onClearSearch() {
        self.isSearchMode = false
        self.searchQuery = ""
        self.isEndReached = false
    }

    func loadNextPage() {
        guard !isPaginationLoading && !isEndReached else { return }
        self.isPaginationLoading = true
        let nextPage = currentPage + 1
        if isSearchMode {
            performFetch(query: searchQuery, page: nextPage, isSearch: true)
        } else {
            performFetch(query: nil, page: nextPage, isSearch: false)
        }
    }

    deinit {
        favoritesTask?.cancel()
    }


    // MARK: - Intents (Updated toggleFavorite)

    func toggleFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                let kmWallpaper = wallpaper.toDomain()
                // ✅ SKIE lets you call suspend functions directly with try await
                try await repository.toggleFavorite(wallpaper: kmWallpaper)
            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }

    // MARK: - Private Logic
    private func observeFavorites() {
        favoritesTask?.cancel()
        favoritesTask = Task { @MainActor in
            for await favorites in repository.observeFavorites() {
                let favoriteIds = Set(favorites.map {
                    $0.id
                })
                self.updateFavoritesState(favoriteIds: favoriteIds)
            }
        }
    }

    @MainActor
    private func updateFavoritesState(favoriteIds: Set<Int64>) {
        for i in 0..<wallpapers.count {
            let isFav = favoriteIds.contains(wallpapers[i].id)
            if wallpapers[i].isFavorite != isFav {
                wallpapers[i] = wallpapers[i].copy(isFavorite: isFav)
            }
        }

        for i in 0..<searchWallpapers.count {
            let isFav = favoriteIds.contains(searchWallpapers[i].id)
            if searchWallpapers[i].isFavorite != isFav {
                searchWallpapers[i] = searchWallpapers[i].copy(
                    isFavorite: isFav
                )
            }
        }
    }

    private func performFetch(query: String?, page: Int, isSearch: Bool) {
        Task {
            do {
                let result: [Wallpaper]

                if let query = query, isSearch {
                    result = try await repository.searchWallpapers(
                        query: query,
                        page: Int32(page)
                    )
                } else {
                    result = try await repository.getCuratedWallpapers(
                        page: Int32(page)
                    )
                }

                if result.isEmpty {
                    await MainActor.run {
                        self.isEndReached = true
                        self.isLoading = false
                        self.isPaginationLoading = false
                    }
                } else {
                    let uiResults = result.map {
                        $0.toUi(isFavorite: $0.isFavorite)
                    }

                    await MainActor.run {
                        if isSearch {
                            if page == 1 {
                                self.searchWallpapers = uiResults
                            } else {
                                let existingIds = Set(
                                    self.searchWallpapers.map {
                                        $0.id
                                    }
                                )
                                let newUnique = uiResults.filter {
                                    !existingIds.contains($0.id)
                                }
                                self.searchWallpapers.append(
                                    contentsOf: newUnique
                                )
                            }
                        } else {
                            if page == 1 {
                                self.wallpapers = uiResults
                            } else {
                                let existingIds = Set(
                                    self.wallpapers.map {
                                        $0.id
                                    }
                                )
                                let newUnique = uiResults.filter {
                                    !existingIds.contains($0.id)
                                }
                                self.wallpapers.append(contentsOf: newUnique)
                            }
                        }
                        self.currentPage = page
                        self.isLoading = false
                        self.isPaginationLoading = false
                    }
                }
            } catch {
                await MainActor.run {
                    self.errorMessage = error.localizedDescription
                    self.isLoading = false
                    self.isPaginationLoading = false
                }
            }
        }
    }
}
