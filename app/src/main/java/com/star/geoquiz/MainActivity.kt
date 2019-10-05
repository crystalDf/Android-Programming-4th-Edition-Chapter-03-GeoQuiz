package com.star.geoquiz

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val keyCurrentIndex = "currentIndex"
    private val keyAnsweredStatus = "answeredStatus"
    private val keyCorrectStatus = "correctStatus"
    private val keyFinishedStatus = "finishedStatus"

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var currentIndex = 0

    private var answeredStatus = BooleanArray(questionBank.size)
    private var correctStatus = BooleanArray(questionBank.size)
    private var finishedStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(keyCurrentIndex, 0)
            answeredStatus = savedInstanceState.getBooleanArray(keyAnsweredStatus)!!
            correctStatus = savedInstanceState.getBooleanArray(keyCorrectStatus)!!
            finishedStatus = savedInstanceState.getBoolean(keyFinishedStatus)
        }

        question_text_view.setOnClickListener {
            next_button.performClick()
        }

        true_button.setOnClickListener {
            getAnswer(true)
        }

        false_button.setOnClickListener {
            getAnswer(false)
        }

        prev_button.setOnClickListener {
            getQuestion(Order.PREV)
        }

        next_button.setOnClickListener {
            getQuestion(Order.NEXT)
        }

        prev_image_button.setOnClickListener {
            prev_button.performClick()
        }

        next_image_button.setOnClickListener {
            next_button.performClick()
        }

        getQuestion(Order.CURRENT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(keyCurrentIndex, currentIndex)
        outState.putBooleanArray(keyAnsweredStatus, answeredStatus)
        outState.putBooleanArray(keyCorrectStatus, correctStatus)
        outState.putBoolean(keyFinishedStatus, finishedStatus)
    }

    private fun getQuestion(order: Order) {

        updateIndex(order)
        updateQuestion()
        updateButton()
    }

    private fun getAnswer(userAnswer: Boolean) {

        checkAnswer(userAnswer)
        updateButton()
    }

    private fun updateIndex(order: Order) {

        while (true) {
            currentIndex = when (order) {
                Order.PREV -> (currentIndex + questionBank.size - 1) % questionBank.size
                Order.CURRENT -> currentIndex
                Order.NEXT -> (currentIndex + 1) % questionBank.size
            }

            if ((order == Order.CURRENT) || (!answeredStatus[currentIndex])) {
                break
            }
        }
    }

    private fun updateQuestion() {

        val questionResId = questionBank[currentIndex].textResId
        question_text_view.setText(questionResId)
    }

    private fun updateButton() {

        true_button.isEnabled = !answeredStatus[currentIndex]
        false_button.isEnabled = !answeredStatus[currentIndex]

        prev_button.isEnabled = !finishedStatus
        next_button.isEnabled = !finishedStatus
        prev_image_button.isEnabled = !finishedStatus
        next_image_button.isEnabled = !finishedStatus
    }

    private fun checkAnswer(userAnswer: Boolean) {

        val correctAnswer = questionBank[currentIndex].answer

        correctStatus[currentIndex] = (userAnswer == correctAnswer)
        answeredStatus[currentIndex] = true
        finishedStatus = answeredStatus.all { element -> element }

        val messageResId = when (correctStatus[currentIndex]) {
            true -> R.string.correct_toast
            false -> R.string.incorrect_toast
        }

        var result = getString(messageResId)

        if (finishedStatus) {
            result += "\n" + "Score: " + String.format("%.2f", getScore())
        }

        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }

    private fun getScore(): Double {

        val correctAnswers = correctStatus.count { element -> element }

        return correctAnswers.toDouble() / questionBank.size * 100
    }

    enum class Order {
        PREV, CURRENT, NEXT
    }
}
