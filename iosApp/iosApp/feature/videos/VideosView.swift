import SwiftUI
import Shared

struct VideosView: View {
    @StateObject private var viewModel = VideosViewModel()
    let coordinator: NavigationCoordinator

    private let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]

    var body: some View {
        VStack(spacing: 0) {
            // Search Bar
            SearchBarView(
                text: $viewModel.searchQuery,
                isSearchActive: viewModel.isSearchMode,
                onSearch: { viewModel.onSearchTriggered() },
                onClear: { viewModel.onClearSearch() }
            )
                .padding(.horizontal)
                .padding(.vertical, 8)

            // Content
            if viewModel.isLoading && (viewModel.isSearchMode ? viewModel.searchVideos.isEmpty : viewModel.popularVideos.isEmpty) {
                ProgressView()
                    .scaleEffect(1.5)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ScrollView {
                    LazyVGrid(columns: columns, spacing: 16) {
                        let list = viewModel.isSearchMode ? viewModel.searchVideos : viewModel.popularVideos

                        ForEach(list) { video in
                            Button(action: {
                                coordinator.navigateToVideoDetail(video: video)
                            }) {
                                VideoGridCell(video: video)
                                    .onAppear {
                                        if video == list.last {
                                            viewModel.loadNextPage()
                                        }
                                    }
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 8)

                    if viewModel.isPaginationLoading {
                        ProgressView()
                            .padding()
                    }
                }
            }
        }
        .navigationBarHidden(true)
    }
}

struct VideoGridCell: View {
    let video: VideoUi

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            AsyncImage(url: URL(string: video.thumbnailUrl)) { phase in
                switch phase {
                case .empty:
                    Color.gray.opacity(0.2)
                case .success(let image):
                    image
                        .resizable()
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

            // Duration Badge
            Text(formatDuration(video.duration))
                .font(.caption)
                .bold()
                .padding(6)
                .background(.black.opacity(0.6))
                .foregroundColor(.white)
                .cornerRadius(4)
                .padding(8)
        }
    }

    func formatDuration(_ seconds: Int) -> String {
        let min = seconds / 60
        let sec = seconds % 60
        return String(format: "%d:%02d", min, sec)
    }
}
