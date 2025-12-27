import Shared
import SwiftUI

struct DetailView: View {
    let wallpaper: WallpaperUi
    let coordinator: NavigationCoordinator
    @State private var viewModel = DetailViewModel()

    var body: some View {
        ZStack {
            Color(hex: wallpaper.averageColor)

            AsyncImage(url: URL(string: wallpaper.imageUrl)) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                case .empty:
                    ProgressView()
                default:
                    Color.black
                }
            }

            // Top Bar with Back, Favorite and Download
            VStack {
                HStack {
                    // Back Button
                    Button(action: {
                        coordinator.pop()
                    }) {
                        Image(systemName: "chevron.left")
                            .font(.title3)
                            .foregroundStyle(.white)
                            .padding(8)
                            .background(.ultraThinMaterial, in: Circle())
                    }

                    Spacer()

                    // Favorite Button
                    FavoriteButton(
                        isFavorite: viewModel.isFavorite,
                        action: {
                            viewModel.toggleFavorite(wallpaper: wallpaper)
                        }
                    )

                    // Download Button
                    Button(action: {
                        viewModel.downloadWallpaper(url: wallpaper.imageUrl)
                    }) {
                        ZStack {
                            if viewModel.isDownloading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    .frame(width: 20, height: 20)
                            } else {
                                Image(systemName: "arrow.down.circle.fill")
                                    .font(.system(size: 20))
                                    .foregroundStyle(.white)
                            }
                        }
                        .padding(8)
                        .background(.ultraThinMaterial, in: Circle())
                    }
                    .disabled(viewModel.isDownloading)
                }
                .padding(.horizontal)
                .padding(.top, getSafeAreaTop() + 8)

                Spacer()
            }
            
            // Scrim & Metadata Overlay
            VStack {
                Spacer()
                ZStack(alignment: .bottom) {
                    LinearGradient(
                        colors: [.black.opacity(0.8), .clear],
                        startPoint: .bottom,
                        endPoint: .top
                    )
                    .frame(height: 160)

                    Text(wallpaper.photographerName)
                        .font(.title2)
                        .bold()
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding()
                        .padding(.bottom, getSafeAreaBottom() + 20)
                }
            }
        }
        .navigationBarHidden(true)
        .toolbar(.hidden, for: .tabBar)
        .ignoresSafeArea()
        .onAppear {
            viewModel.loadFavoriteStatus(wallpaperId: wallpaper.id)
        }
    }

    // MARK: - Safe Area Helpers
    private func getSafeAreaTop() -> CGFloat {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first(where: { $0.isKeyWindow })
        else {
            return 0
        }
        return window.safeAreaInsets.top
    }

    private func getSafeAreaBottom() -> CGFloat {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first(where: { $0.isKeyWindow })
        else {
            return 0
        }
        return window.safeAreaInsets.bottom
    }
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(
            in: CharacterSet.alphanumerics.inverted
        )

        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let r = Double((int >> 16) & 0xFF) / 255
        let g = Double((int >> 8) & 0xFF) / 255
        let b = Double(int & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}
