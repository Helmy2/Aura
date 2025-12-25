import SwiftUI
import Shared

struct HomeView: View {
    
    @State private var viewModel = HomeViewModel()
    
    let columns = [
        GridItem(.adaptive(minimum: 160), spacing: 16)
    ]
    
    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVGrid(columns: columns, spacing: 16) {
                    
                    ForEach(viewModel.wallpapers, id: \.id) { wallpaper in
                        NavigationLink(destination: DetailView(wallpaper: wallpaper)) {
                            WallpaperItemView(wallpaper: wallpaper)
                                .onAppear {
                                    if wallpaper == viewModel.wallpapers.last {
                                        viewModel.loadNextPage()
                                    }
                                }
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                    
                    if viewModel.isPaginationLoading {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .padding()
                    }
                }
                .padding()
            }
            .overlay {
                if viewModel.isLoading {
                    ProgressView("Loading...")
                }
            }
            .alert("Error", isPresented: Binding(
                get: { viewModel.errorMessage != nil },
                set: { _ in viewModel.errorMessage = nil }
            )) {
                Button("OK", role: .cancel) { }
            } message: {
                Text(viewModel.errorMessage ?? "")
            }
        }
    }
}

struct WallpaperItemView: View {
    let wallpaper: Wallpaper
    
    var body: some View {
        VStack(alignment: .leading) {
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
            
            Text("Photo by \(wallpaper.photographer)")
                .font(.caption)
                .lineLimit(1)
                .foregroundColor(.primary)
        }
    }
}
