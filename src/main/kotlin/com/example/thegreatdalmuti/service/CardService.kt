package com.example.thegreatdalmuti.service

import com.example.thegreatdalmuti.domain.Card
import com.example.thegreatdalmuti.domain.Player
import com.example.thegreatdalmuti.type.RankType
import org.springframework.stereotype.Service

@Service
class CardService {

    /**
     * 달무티 게임 덱 생성
     * - 1번 카드 1장, 2번 카드 2장, ..., 12번 카드 12장
     * - 조커 2장 추가 (향후 구현)
     */
    fun createDeck(): List<Card> {
        val cards = mutableListOf<Card>()

        RankType.entries.forEach { rankType ->
            repeat(rankType.rankNumber) {
                cards.add(Card(
                    name = "${rankType.rankNumber}",
                    rank = rankType
                ))
            }
        }

        // 조커 카드 추가 (나중에 구현)
        // cards.add(Card("Joker", RankType.JOKER))
        // cards.add(Card("Joker", RankType.JOKER))

        return cards
    }

    /**
     * 덱을 섞어서 반환
     */
    fun createShuffledDeck(): MutableList<Card> {
        return createDeck().shuffled().toMutableList()
    }

    /**
     * 플레이어들에게 카드 배분
     * 모든 카드를 균등하게 배분
     */
    fun distributeCards(players: List<Player>, deck: MutableList<Card>) {

        require(players.isNotEmpty()) {
            "플레이어가 없습니다."
        }

        val cardsPerPlayer = deck.size / players.size
        val remainingCards = deck.size % players.size

        var cardIndex = 0

        players.forEachIndexed { playerIndex, player ->
            player.cards.clear()

            // 기본 카드 수 배분
            repeat(cardsPerPlayer) {
                player.cards.add(deck[cardIndex++])
            }

            // 남은 카드가 있으면 첫 번째 플레이어부터 하나씩 추가 배분
            if (playerIndex < remainingCards) {
                player.cards.add(deck[cardIndex++])
            }
        }

        // 배분된 카드들을 랭크 순으로 정렬 (낮은 숫자가 강함)
        players.forEach { player ->
            player.cards.sortBy { it.rank.rankNumber }
        }
    }

    /**
     * 카드 조합이 유효한지 검증
     * - 같은 랭크의 카드들만 함께 낼 수 있음
     * - 1장 이상이어야 함
     */
    fun isValidCardCombination(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false

        val firstRank = cards.first().rank
        return cards.all { it.rank == firstRank }
    }

    /**
     * 현재 필드의 카드보다 강한 카드인지 확인
     * - 숫자가 낮을수록 강함 (1이 가장 강함)
     * - 같은 장수여야 함
     */
    fun canPlayCards(playCards: List<Card>, fieldCards: List<Card>): Boolean {
        if (fieldCards.isEmpty()) return true // 첫 번째 플레이어는 아무 카드나 가능

        // 장수가 같아야 함
        if (playCards.size != fieldCards.size) return false

        // 유효한 조합인지 확인
        if (!isValidCardCombination(playCards)) return false

        // 더 강한 카드인지 확인 (숫자가 더 작아야 함)
        val playRank = playCards.first().rank.rankNumber
        val fieldRank = fieldCards.first().rank.rankNumber

        return playRank < fieldRank
    }

    /**
     * 플레이어가 특정 카드들을 가지고 있는지 확인
     */
    fun playerHasCards(player: Player, cards: List<Card>): Boolean {
        return cards.all { cardToCheck ->
            player.cards.count { it.rank == cardToCheck.rank } >=
                    cards.count { it.rank == cardToCheck.rank }
        }
    }

    /**
     * 플레이어 손에서 카드 제거
     */
    fun removeCardsFromPlayer(player: Player, cards: List<Card>) {
        cards.forEach { cardToRemove ->
            val cardInHand = player.cards.find { it.rank == cardToRemove.rank }
            if (cardInHand != null) {
                player.cards.remove(cardInHand)
            }
        }
    }

    /**
     * 카드 덱의 총 개수 계산 (테스트용)
     */
    fun getTotalCardCount(): Int {
        return RankType.entries.toTypedArray().sumOf { it.rankNumber }
    }

    /**
     * 특정 랭크의 카드 개수 반환
     */
    fun getCardCountByRank(rank: RankType): Int {
        return rank.rankNumber
    }
}