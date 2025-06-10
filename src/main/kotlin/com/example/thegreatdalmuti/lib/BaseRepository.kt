package com.example.thegreatdalmuti.lib

import org.springframework.data.jpa.repository.JpaRepository

interface BaseRepository<E : BaseEntity?> : JpaRepository<E, Long?> {

}
