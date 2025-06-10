package com.example.thegreatdalmuti.domain

import com.example.thegreatdalmuti.entity.Player

data class PlayerDomain(

    val id: Long?,

    var username: String,

    var profileImageUrl: String? = null,

    var cards: MutableList<Card> = mutableListOf()
) {


    constructor(player: Player) : this(
        id = player.id,
        username =player.username,
        profileImageUrl = player.profileImageUrl,
        cards= mutableListOf()
    )

}
