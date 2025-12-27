//
//  WallpaperGridCell.swift
//  iosApp
//
//  Created by platinum on 27/12/2025.
//

import SwiftUI

struct WallpaperGridCell: View {
    let wallpaper: WallpaperUi
    let onTap: () -> Void
    var onFavoriteToggle: (() -> Void)? = nil

    var body: some View {
        ZStack(alignment: .topTrailing) {

            Button(action: onTap) {
                AsyncImage(url: URL(string: wallpaper.smallImageUrl)) { phase in
                    switch phase {
                    case .empty:
                        Color.gray.opacity(0.2)
                    case .success(let image):
                        image.resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(minWidth: 0, maxWidth: .infinity)
                    case .failure:
                        Color.red.opacity(0.2)
                    @unknown default:
                        EmptyView()
                    }
                }
                .frame(height: 220)
                .cornerRadius(12)
                .clipped()
            }

            // Favorite Button
            FavoriteButton(
                isFavorite: wallpaper.isFavorite,
                action: {
                    onFavoriteToggle?()
                }
            )
            .padding(8)

        }
    }
}
