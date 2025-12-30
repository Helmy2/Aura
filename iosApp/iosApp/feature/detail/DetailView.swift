import Shared
import SwiftUI

struct DetailView: View {
    let wallpaper: WallpaperUi
    let coordinator: NavigationCoordinator
    @State private var viewModel = DetailViewModel()

    var body: some View {
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

                Button(action: {
                    viewModel.downloadWallpaper(url: wallpaper.imageUrl)
                }) {
                    ZStack {
                        switch viewModel.downloadState {

                        case .idle:
                            Image(systemName: "arrow.down")
                                .font(.system(size: 20, weight: .medium))
                                .foregroundStyle(.white)
                                .transition(.scale.combined(with: .opacity))
                                .scaledToFill()

                        case .downloading:
                            ProgressView()
                                .tint(.white)
                                .scaleEffect(0.8)
                                .transition(.scale.combined(with: .opacity))

                        case .success:
                            Image(systemName: "checkmark")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundStyle(.green)
                                .transition(.scale.combined(with: .opacity))

                        case .failed:
                            Image(systemName: "xmark")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundStyle(.red)
                                .transition(.scale.combined(with: .opacity))
                        }
                    }
                    .frame(width: 44, height: 44)
                    .background(.ultraThinMaterial, in: Circle())
                    .overlay(
                        Circle().stroke(
                            borderColor(for: viewModel.downloadState),
                            lineWidth: 2
                        )
                    )
                    .animation(
                        .spring(response: 0.3, dampingFraction: 0.6),
                        value: viewModel.downloadState
                    )
                }
                .disabled(viewModel.downloadState != .idle)

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
                .padding()

                Spacer()

                Image(systemName: "camera.fill")
                    .foregroundStyle(.white.opacity(0.6))
                    .padding()
            }
            .background(.ultraThinMaterial)
        }
        .background(
            ZStack {
                Color(hex: wallpaper.averageColor)

                AsyncImage(url: URL(string: wallpaper.imageUrl)) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                    case .empty:
                        ProgressView()
                            .scaleEffect(1.5)
                            .tint(.white)
                    default:
                        Color.clear
                    }
                }
            }
            .ignoresSafeArea()
        )
        .navigationBarHidden(true)
        .toolbar(.hidden, for: .tabBar)
        .onAppear {
            viewModel.loadFavoriteStatus(wallpaperId: wallpaper.id)
        }
    }
}

private func borderColor(for state: DownloadState) -> Color {
    switch state {
    case .success: return .green.opacity(0.8)
    case .failed: return .red.opacity(0.8)
    default: return .clear
    }
}
