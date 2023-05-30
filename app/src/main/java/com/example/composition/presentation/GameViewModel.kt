package com.example.composition.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameResultUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel : ViewModel() {

    private val repository = GameRepositoryImpl
    private var countOfRightAnswers: Int = 0
    private var countOfQuestion: Int = 0

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers


    private val _countOfRightAnswersLD = MutableLiveData<Int>()
    val countOfRightAnswersLD: LiveData<Int>
        get() = _countOfRightAnswersLD

    init {
        _countOfRightAnswersLD.value = countOfRightAnswers
    }

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private val getGameResult = GetGameResultUseCase(repository)


    fun generateQuestionUseCase(maxSumValue: Int) {
        _question.value = generateQuestionUseCase.invoke(maxSumValue)
        countOfQuestion++
        _percentOfRightAnswers.value =
            ((countOfRightAnswers / countOfQuestion.toDouble()) * 100).toInt()
    }

    fun getGameSettingUseCase(level: Level): GameSettings {
        return getGameSettingsUseCase.invoke(level)
    }

    fun getGameResult(
        gameSettings: GameSettings
    ): GameResult {
        return getGameResult.invoke(
            countOfRightAnswers,
            countOfQuestion,
            gameSettings
        )
    }

    fun checkResult(indexInOptions: Int) {
        question.value?.let {
            val rightAnswer = it.sum - it.visibleNumber
            if (it.options[indexInOptions] == rightAnswer) {
                countOfRightAnswers++
                _countOfRightAnswersLD.value = countOfRightAnswers
            }
        } ?: throw IllegalStateException("Question = null")
    }

}