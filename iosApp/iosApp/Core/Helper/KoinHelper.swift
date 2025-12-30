import Foundation
import Shared

class KoinHelper {
    static let shared = KoinHelper()
    
    private init() {}
    
    lazy var wallpaperRepository: WallpaperRepository = {
        return koin.get(objCClass: WallpaperRepository.self) as! WallpaperRepository
    }()
    
    lazy var favoritesRepository: FavoritesRepository = {
        return koin.get(objCClass: FavoritesRepository.self) as! FavoritesRepository
    }()
    
    lazy var settingsRepository: SettingsRepository = {
        return koin.get(objCClass: SettingsRepository.self) as! SettingsRepository
    }()
    
    lazy var videoRepository: VideoRepository = {
        return koin.get(objCClass: VideoRepository.self) as! VideoRepository
    }()
}

// Global helper for Koin injection
private var koin: Koin_coreKoin {
    return KoinKt.doInitKoin().koin
}
