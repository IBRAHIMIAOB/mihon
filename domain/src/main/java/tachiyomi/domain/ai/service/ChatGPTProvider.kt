package tachiyomi.domain.ai.service

import java.io.File
import java.io.IOException

class ChatGPTProvider : AIColoringProvider {

    override val id = "chatgpt"
    override val name = "ChatGPT"

    override suspend fun colorize(image: File): Result<File> {
        return Result.failure(IOException("ChatGPT provider is not yet implemented"))
    }
}
