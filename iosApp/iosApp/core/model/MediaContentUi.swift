import Foundation
import Shared

enum MediaContentUi: Identifiable, Hashable {
    case wallpaper(WallpaperUi)
    case video(VideoUi)
    
    var id: Int64 {
        switch self {
        case .wallpaper(let w): return w.id
        case .video(let v): return v.id
        }
    }
    
    func hash(into hasher: inout Hasher) {
        switch self {
        case .wallpaper(let w):
            hasher.combine("wallpaper")
            hasher.combine(w.id)
        case .video(let v):
            hasher.combine("video")
            hasher.combine(v.id)
        }
    }
    
    static func == (lhs: MediaContentUi, rhs: MediaContentUi) -> Bool {
        switch (lhs, rhs) {
        case (.wallpaper(let w1), .wallpaper(let w2)): return w1.id == w2.id
        case (.video(let v1), .video(let v2)): return v1.id == v2.id
        default: return false
        }
    }
}

extension MediaContent {
    func toUi() -> MediaContentUi {
        if let wContent = self as? MediaContent.WallpaperContent {
            return .wallpaper(wContent.wallpaper.toUi())
        } else if let vContent = self as? MediaContent.VideoContent {
            var uiVideo = vContent.video.toUi()
            return .video(uiVideo)
        } else {
            fatalError("Unknown MediaContent type")
        }
    }
}
