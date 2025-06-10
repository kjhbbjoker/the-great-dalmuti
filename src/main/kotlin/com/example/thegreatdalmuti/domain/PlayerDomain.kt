package com.example.thegreatdalmuti.domain

data class PlayerDomain(

    val id: Long,

    var username: String,

    var profileImageUrl: String? = null,

    var cards: MutableList<Card> = mutableListOf()
) {




}
