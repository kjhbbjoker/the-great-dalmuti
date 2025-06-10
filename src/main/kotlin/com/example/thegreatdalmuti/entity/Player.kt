package com.example.thegreatdalmuti.entity

import com.example.thegreatdalmuti.lib.BaseEntity
import com.example.thegreatdalmuti.type.RankType
import jakarta.persistence.*

@Entity
class Player : BaseEntity() {

    val userId: String = "";

    var username: String = "";

    var password: String = "";

    var email: String = "";

    var profileImageUrl: String? = null
}