package me.mateuspires.tictactoe.ui.main

enum class BoardCell {
    EMPTY, X, O;

    companion object {
        private val ordinals = values()

        fun fromOrdinal(ordinal: Int): BoardCell? {
            return ordinals.getOrNull(ordinal)
        }
    }
}