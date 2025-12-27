import Foundation
import Shared
import Observation

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

    // Favorite State
    private var favoriteIds: Set<Int64> = [] // ✅ Cache favorite IDs
    
    // Dependencies
    private let repository: WallpaperRepository
    private let favoritesRepository: FavoritesRepository
    
    init() {
        self.repository = KoinHelper().wallpaperRepository
        self.favoritesRepository = KoinHelper().favoritesRepository

        // Start observing favorites first
        observeFavorites()

        // Then load wallpapers
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

    func toggleFavorite(wallpaper: WallpaperUi) {
        Task {
            do {
                let kmWallpaper = wallpaper.toDomain()
                try await favoritesRepository.toggleFavorite(wallpaper: kmWallpaper)
            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }
    
    // MARK: - Private Logic
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
                    self.isEndReached = true
                    self.isLoading = false
                    self.isPaginationLoading = false
                } else {
                    // ✅ Use cached favorite IDs when creating UI models
                    let uiResults = result.map {
                        $0.toUi(isFavorite: favoriteIds.contains($0.id))
                    }
                    
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
                            self.searchWallpapers.append(contentsOf: newUnique)
                        }
                    } else {
                        if page == 1 {
                            self.wallpapers = uiResults
                        } else {
                            let existingIds = Set(self.wallpapers.map { $0.id })
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
            } catch {
                self.errorMessage = error.localizedDescription
                self.isLoading = false
                self.isPaginationLoading = false
            }
        }
    }

    private func observeFavorites() {
        favoritesRepository.getAllFavorites().collect(
            collector: Collector<[Wallpaper]> { [weak self] favorites in
                guard let self = self else {
                    return
                }

                Task { @MainActor in
                    // ✅ Update cached favorite IDs
                    self.favoriteIds = Set(favorites.map {
                        $0.id
                    })

                    // Update existing wallpapers
                    self.wallpapers = self.wallpapers.map { wallpaper in
                        var updated = wallpaper
                        updated.isFavorite = self.favoriteIds.contains(wallpaper.id)
                        return updated
                    }

                    // Update search wallpapers
                    self.searchWallpapers = self.searchWallpapers.map { wallpaper in
                        var updated = wallpaper
                        updated.isFavorite = self.favoriteIds.contains(wallpaper.id)
                        return updated
                    }
                }
            },
            completionHandler: { [weak self] error in
                guard let self = self else {
                    return
                }
                Task { @MainActor in
                    if let error = error {
                        self.errorMessage = error.localizedDescription
                    }
                }
            }
        )
    }
}
