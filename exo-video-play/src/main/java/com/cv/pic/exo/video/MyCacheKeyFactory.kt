package com.cv.pic.exo.video

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheKeyFactory

@UnstableApi
class MyCacheKeyFactory:CacheKeyFactory {
    override fun buildCacheKey(dataSpec: DataSpec): String {
        return dataSpec.key?.ifEmpty { generateCacheKey(dataSpec.uri) } ?: dataSpec.key!!
    }

    companion object{
        /**
         * 生成缓存键（与Media3内部逻辑一致）
         */
        fun generateCacheKey(uri: Uri): String {
            return uri.toString().hashCode().toString()
        }
    }
}