//
//  DetailView.swift
//  iosApp
//
//  Created by platinum on 25/12/2025.
//

import SwiftUI
import Shared

struct DetailView: View {
    let wallpaper: Wallpaper
    
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
            
            // Scrim & Metadata Overlay
            VStack {
                Spacer()
                ZStack(alignment: .bottom) {
                    // Gradient Scrim
                    LinearGradient(
                        colors: [.black.opacity(0.8), .clear],
                        startPoint: .bottom,
                        endPoint: .top
                    )
                    .frame(height: 160)
                    
                    // Text
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Photo by")
                            .font(.caption)
                            .foregroundColor(.white.opacity(0.8))
                        
                        Text(wallpaper.photographer)
                            .font(.title2)
                            .bold()
                            .foregroundColor(.white)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .padding(.bottom, 20)
                }
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .ignoresSafeArea()
    }
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)

        let r = Double((int >> 16) & 0xFF) / 255
        let g = Double((int >> 8) & 0xFF) / 255
        let b = Double(int & 0xFF) / 255

        self.init(red: r, green: g, blue: b)
    }
}
