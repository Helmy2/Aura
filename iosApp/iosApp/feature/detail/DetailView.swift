import Shared
import SwiftUI

struct DetailView: View {
    let wallpaper: WallpaperUi
    let coordinator: NavigationCoordinator
    @State private var viewModel = DetailViewModel()

    var body: some View {
        ZStack {
            // 1. Background Layer (Color + Image)
            Color(hex: wallpaper.averageColor)
                .ignoresSafeArea()

            AsyncImage(url: URL(string: wallpaper.imageUrl)) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                case .empty:
                    ProgressView()
                        .scaleEffect(1.5)
                        .tint(.white)
                default:
                    Color.clear
                }
            }
            .ignoresSafeArea()

            // 2. UI Overlay Layer
            VStack {
                // --- Top Bar ---
                HStack {
                    // Back Button
                    Button(action: { coordinator.pop() }) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 20, weight: .medium))
                            .foregroundStyle(.white)
                            .padding(12)
                            .background(.ultraThinMaterial, in: Circle())
                    }

                    Spacer()

                    // Favorite Button
                    Button(action: {
                        viewModel.toggleFavorite(wallpaper: wallpaper)
                    }) {
                        Image(
                            systemName: viewModel.isFavorite
                                ? "heart.fill" : "heart"
                        )
                            .font(.system(size: 20, weight: .medium))
                            .foregroundStyle(viewModel.isFavorite ? .red : .white)
                            .padding(12)
                            .background(.ultraThinMaterial, in: Circle())
                    }

                    // Download Button
                    Button(action: {
                        viewModel.downloadWallpaper(url: wallpaper.imageUrl)
                    }) {
                        ZStack {
                            if viewModel.isDownloading {
                                ProgressView()
                                    .tint(.white)
                            } else {
                                Image(systemName: "arrow.down")
                                    .font(.system(size: 20, weight: .medium))
                                    .foregroundStyle(.white)
                            }
                        }
                        .padding(12)
                        .background(.ultraThinMaterial, in: Circle())
                    }
                    .disabled(viewModel.isDownloading)
                }
                .padding(.horizontal)

                Spacer()

                // --- Bottom Metadata Bar ---
                HStack(alignment: .center) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(wallpaper.photographerName)
                            .font(.title3)
                            .fontWeight(.bold)
                            .foregroundStyle(.white)

                        Text("Photographer")
                            .font(.caption)
                            .foregroundStyle(.white.opacity(0.8))
                    }

                    Spacer()

                    Image(systemName: "camera.fill")
                        .foregroundStyle(.white.opacity(0.6))
                }
                .padding()
                .background(.ultraThinMaterial)
                .cornerRadius(20)
                .padding(.horizontal)
                .padding(.bottom)
            }
        }
        .navigationBarHidden(true)
        .toolbar(.hidden, for: .tabBar)
        .onAppear {
            viewModel.loadFavoriteStatus(wallpaperId: wallpaper.id)
        }
    }
}
