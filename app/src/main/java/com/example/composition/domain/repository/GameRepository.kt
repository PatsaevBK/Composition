package com.example.composition.domain.repository

import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question

interface GameRepository {

    fun generateQuestion(
        maxSumValue: Int,
        countOfOptions: Int
    ): Question

    fun getGameSettings(level: Level): GameSettings

    fun getGameResult(
        countOfRightAnswers: Int,
        countOfQuestions: Int,
        gameSettings: GameSettings
    ): GameResult


}