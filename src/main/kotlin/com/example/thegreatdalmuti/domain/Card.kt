package com.example.thegreatdalmuti.domain

import com.example.thegreatdalmuti.type.RankType

data class Card(
    val name: String,
    val rank: RankType,      // 카드의 랭크
) {

}