package com.example.thegreatdalmuti.repository

import com.example.thegreatdalmuti.entity.Player
import com.example.thegreatdalmuti.lib.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : BaseRepository<Player> {
}