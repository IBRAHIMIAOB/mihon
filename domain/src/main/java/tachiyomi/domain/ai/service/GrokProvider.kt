package tachiyomi.domain.ai.service

import java.io.File
import java.io.IOException

class GrokProvider : AIColoringProvider {

    override val id = "grok"
    override val name = "Grok"

    override suspend fun colorize(image: File): Result<File> {
        return Result.failure(IOException("Grok provider is not yet implemented"))
    }
}
