package com.example.composition.presentation

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level


@Suppress("DEPRECATION")
class GameFragment : Fragment() {

    private lateinit var level: Level
    private lateinit var model: GameViewModel
    private lateinit var settings: GameSettings

    private var _binding: FragmentGameBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("_binding = null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(this)[GameViewModel::class.java]
        observesViewModel()
        setUpListeners()
        launchPlay()

    }

    private fun launchTimer() {
        val timeInMillisec: Long = (settings.gameTimeInSeconds * MILLISEC_IN_SEC).toLong()
        object : CountDownTimer(timeInMillisec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val showNumber = "${(millisUntilFinished / MILLISEC_IN_SEC).toInt()}"
                binding.tvTimer.text = showNumber
            }

            override fun onFinish() {
                val result = model.getGameResult(settings)
                launchFinishedFragment(result)
            }
        }.start()
    }

    private fun launchPlay() {
        settings = model.getGameSettingUseCase(level)
        model.generateQuestionUseCase(settings.maxSumValue)
        launchTimer()
    }

    private fun launchFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    private fun setUpListeners() {
        val options = listOf(
            binding.tvOption1, binding.tvOption2, binding.tvOption3,
            binding.tvOption4, binding.tvOption5, binding.tvOption6
        )
        for ((i, option) in options.withIndex()) {
            option.setOnClickListener {
                model.checkResult(i)
                model.generateQuestionUseCase(settings.maxSumValue)
            }
        }

    }

    private fun observesViewModel() {
        model.question.observe(viewLifecycleOwner) {
            binding.tvSum.text = it.sum.toString()
            binding.tvLeftNumber.text = it.visibleNumber.toString()
            val options = listOf(
                binding.tvOption1, binding.tvOption2, binding.tvOption3,
                binding.tvOption4, binding.tvOption5, binding.tvOption6
            )
            for ((i, option) in options.withIndex()) {
                option.text = it.options[i].toString()
                Log.d("GameFragment", "Видно: $i = ${option.text}")
            }
        }
        model.countOfRightAnswersLD.observe(viewLifecycleOwner) {
            binding.tvAnswersProgress.text = getString(
                R.string.progress_answers,
                it.toString(),
                settings.minCountOfRightAnswers.toString()
            )
        }
        model.percentOfRightAnswers.observe(viewLifecycleOwner) {
            binding.progressBar.setProgress(it, true)
        }
    }


    companion object {
        private const val KEY_LEVEL = "level"
        const val NAME = "game"
        private const val MILLISEC_IN_SEC = 1000

        @JvmStatic
        fun newInstance(level: Level) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
    }
}