package com.example.thegreatdalmuti.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreatePlayerRequest(
    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(min = 2, max = 20, message = "닉네임은 2-20자여야 합니다")
    val nickname: String,

)
