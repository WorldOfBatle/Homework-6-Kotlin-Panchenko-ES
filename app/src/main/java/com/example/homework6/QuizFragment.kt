package com.example.homework6

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.homework6.databinding.FragmentQuizBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var correctAnswers = 0
    private var selectedAnswer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            currentIndex = it.getInt("current_index")
            correctAnswers = it.getInt("correct_answers")
            selectedAnswer = it.getInt("selected_answer")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        binding.answersGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedAnswer = when (checkedId) {
                R.id.answer1Button -> 1
                R.id.answer2Button -> 2
                R.id.answer3Button -> 3
                else -> 0
            }
        }

        binding.nextButton.setOnClickListener {
            submitAnswer()
        }

        loadQuestions()
    }

    private fun loadQuestions() {
        val appContext = requireContext().applicationContext

        binding.questionContent.visibility = View.GONE
        binding.progressText.setText(R.string.quiz_loading)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                questions = withContext(Dispatchers.IO) {
                    QuestionDatabaseHelper(appContext).use { database ->
                        database.getAllQuestions()
                    }
                }

                if (questions.isEmpty()) {
                    binding.progressText.setText(R.string.quiz_empty)
                    return@launch
                }

                showQuestion()

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(
                    "QuizFragment",
                    "Ошибка загрузки вопросов",
                    exception
                )
                binding.progressText.setText(R.string.quiz_load_error)
            }
        }
    }

    private fun showQuestion() {
        val question = questions[currentIndex]

        binding.progressText.text = getString(
            R.string.quiz_progress,
            currentIndex + 1,
            questions.size
        )

        binding.questionText.text = question.text
        binding.answer1Button.text = question.answer1
        binding.answer2Button.text = question.answer2
        binding.answer3Button.text = question.answer3

        // Запоминаем выбор перед очисткой RadioGroup:
        // clearCheck() тоже вызывает слушатель.
        val answerToRestore = selectedAnswer

        binding.answersGroup.clearCheck()

        when (answerToRestore) {
            1 -> binding.answersGroup.check(R.id.answer1Button)
            2 -> binding.answersGroup.check(R.id.answer2Button)
            3 -> binding.answersGroup.check(R.id.answer3Button)
        }

        binding.nextButton.setText(
            if (currentIndex == questions.lastIndex) {
                R.string.quiz_finish
            } else {
                R.string.quiz_next
            }
        )

        binding.questionContent.visibility = View.VISIBLE
    }

    private fun submitAnswer() {
        if (questions.isEmpty()) return

        if (selectedAnswer == 0) {
            Toast.makeText(
                requireContext(),
                R.string.quiz_answer_required,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val question = questions[currentIndex]
        val earnedPoint = if (
            selectedAnswer == question.correctAnswer
        ) {
            1
        } else {
            0
        }

        if (currentIndex == questions.lastIndex) {
            val navController = findNavController()

            if (navController.currentDestination?.id != R.id.quizFragment) {
                return
            }

            val arguments = Bundle().apply {
                putInt("score", correctAnswers + earnedPoint)
                putInt("total", questions.size)
            }

            navController.navigate(
                R.id.action_quizFragment_to_resultFragment,
                arguments
            )
        } else {
            correctAnswers += earnedPoint
            currentIndex++
            selectedAnswer = 0
            showQuestion()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("current_index", currentIndex)
        outState.putInt("correct_answers", correctAnswers)
        outState.putInt("selected_answer", selectedAnswer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}