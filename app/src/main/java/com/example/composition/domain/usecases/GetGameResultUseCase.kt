package com.example.composition.domain.usecases

import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.repository.GameRepository

data class GetGameResultUseCase(val repository: GameRepository) {

    operator fun invoke(
        countOfRightAnswers: Int,
        countOfQuestions: Int,
        gameSettings: GameSettings
    ): GameResult {
        return repository.getGameResult(countOfRightAnswers, countOfQuestions, gameSettings)
    }
}