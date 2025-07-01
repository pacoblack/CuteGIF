package com.cv.pic.ai.deepseek.viewmodel

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

data class UserLoginRequest(val email: String, val password: String)