package com.example.thegreatdalmuti.entity

import com.example.thegreatdalmuti.lib.BaseEntity
import com.example.thegreatdalmuti.type.RankType
import jakarta.persistence.*

@Entity
class Player : BaseEntity() {

    var username: String = "";

    var profileImageUrl: String? = null

    var rank: RankType? = RankType.PEON

}