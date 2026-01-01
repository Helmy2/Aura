import Foundation
import Shared

struct VideoUi: Identifiable, Equatable, Hashable {
    let id: Int64
    let videoUrl: String
    let thumbnailUrl: String
    let duration: Int
    let width: Int
    let height: Int
    let photographerName: String
    var isFavorite: Bool
}

extension Video {
    func toUi() -> VideoUi {
        return VideoUi(
            id: self.id,
            videoUrl: self.videoUrl,
            thumbnailUrl: self.imageUrl,
            duration: Int(self.duration),
            width: Int(self.width),
            height: Int(self.height),
            photographerName: self.user.name,
            isFavorite: self.isFavorite
        )
    }
}
