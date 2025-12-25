import Shared
import SwiftUI

@MainActor
@Observable
class HomeViewModel {

    // MARK: - State
    var wallpapers: [Wallpaper] = []
    var isLoading: Bool = false
    var isPaginationLoading: Bool = false
    var errorMessage: String? = nil
    
    // Pagination State
    private var currentPage: Int = 1
    private var isEndReached: Bool = false
    
    // MARK: - Dependencies
    private let repository: WallpaperRepository

    init() {
        self.repository = KoinHelper().wallpaperRepository
        loadCuratedWallpapers(reset: true)
    }

    // MARK: - Intent: Load Data
    func loadCuratedWallpapers(reset: Bool = false) {
        if reset {
            self.isLoading = true
            self.currentPage = 1
            self.wallpapers = []
            self.isEndReached = false
        } else {
            // Guard: Don't load if already loading or finished
            guard !isPaginationLoading && !isEndReached else { return }
            self.isPaginationLoading = true
        }
        
        self.errorMessage = nil
        
        Task {
            do {
                // Fetch from Shared Repository
                // Note: Int32 conversion is needed for Kotlin Int
                let result = try await repository.getCuratedWallpapers(page: Int32(currentPage))
                
                if result.isEmpty {
                    self.isEndReached = true
                } else {
                    if reset {
                        self.wallpapers = result
                    } else {
                        self.wallpapers.append(contentsOf: result)
                    }
                    // Increment page for next time
                    self.currentPage += 1
                }
                
                self.isLoading = false
                self.isPaginationLoading = false
                
            } catch {
                self.errorMessage = error.localizedDescription
                self.isLoading = false
                self.isPaginationLoading = false
            }
        }
    }
    
    // MARK: - Intent: Load More
    func loadNextPage() {
        loadCuratedWallpapers(reset: false)
    }
}
