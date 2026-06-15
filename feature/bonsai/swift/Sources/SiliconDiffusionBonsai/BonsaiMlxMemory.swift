import Foundation
import MLX

enum BonsaiMlxMemory {
    private static let lowCacheLimit = 16 * 1024 * 1024

    static func configureForInference() {
        #if os(iOS)
        if let workingSet = GPU.maxRecommendedWorkingSetBytes(), workingSet > 0 {
            Memory.memoryLimit = workingSet
            print("[Bonsai] mlx memory recommendedWorkingSet=\(workingSet)")
        } else {
            print("[Bonsai] mlx memory recommendedWorkingSet=unavailable")
        }
        Memory.cacheLimit = lowCacheLimit
        print("[Bonsai] mlx memory cacheLimit=\(lowCacheLimit)")
        #else
        print("[Bonsai] mlx memory host cache clear")
        #endif
        reclaimCache()
    }

    static func reclaimCache() {
        Memory.clearCache()
    }
}
