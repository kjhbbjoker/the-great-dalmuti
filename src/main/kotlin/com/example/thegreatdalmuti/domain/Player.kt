package com.example.thegreatdalmuti.domain



data class Player(

    val id: Long?,

    var gameRoom: GameRoom? = null,

    var username: String,

    var cards: MutableList<Card> = mutableListOf()
) {

    fun joinGameRoom(gameRoom: GameRoom) {
        this.gameRoom = gameRoom
        gameRoom.addPlayer(this)
    }

    fun leaveGameRoom() {
        this.gameRoom?.removePlayer(this)
        this.gameRoom=null
    }


}
