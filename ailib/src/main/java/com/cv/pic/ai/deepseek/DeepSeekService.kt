package com.cv.pic.ai.deepseek

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekService {
  @POST("chat/completions")
  suspend fun chatCompletion(
    @Header("Authorization") token: String,
    @Body request: ChatRequest
  ): ChatResponse

}

data class ChatRequest(
  val model: String = "deepseek-chat",
  val messages: List<Message>,
  val stream: Boolean = false
)

data class Message(
  val role: String,
  val content: String
)

data class ChatResponse(
  val choices: List<Choice>
)

data class Choice(
  val message: Message
)