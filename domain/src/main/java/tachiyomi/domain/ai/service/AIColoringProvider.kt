package tachiyomi.domain.ai.service

import java.io.File

interface AIColoringProvider {
    val id: String
    val name: String
    suspend fun colorize(image: File): Result<File>
}
