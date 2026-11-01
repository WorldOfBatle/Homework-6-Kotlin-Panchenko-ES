package com.example.homework6

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class QuestionDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_QUESTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_TEXT TEXT NOT NULL,
                $COLUMN_ANSWER_1 TEXT NOT NULL,
                $COLUMN_ANSWER_2 TEXT NOT NULL,
                $COLUMN_ANSWER_3 TEXT NOT NULL,
                $COLUMN_CORRECT_ANSWER INTEGER NOT NULL
                    CHECK ($COLUMN_CORRECT_ANSWER BETWEEN 1 AND 3)
            )
            """.trimIndent()
        )

        val questions = listOf(
            Question(
                id = 1,
                text = "Кто написал роман «Мастер и Маргарита»?",
                answer1 = "Лев Толстой",
                answer2 = "Михаил Булгаков",
                answer3 = "Антон Чехов",
                correctAnswer = 2
            ),
            Question(
                id = 2,
                text = "Кто является автором «Маленького принца»?",
                answer1 = "Антуан де Сент-Экзюпери",
                answer2 = "Жюль Верн",
                answer3 = "Виктор Гюго",
                correctAnswer = 1
            ),
            Question(
                id = 3,
                text = "Как зовут главного героя романа «Преступление и наказание»?",
                answer1 = "Евгений Онегин",
                answer2 = "Пьер Безухов",
                answer3 = "Родион Раскольников",
                correctAnswer = 3
            ),
            Question(
                id = 4,
                text = "Кто написал роман «Ночной Дозор»?",
                answer1 = "Борис Акунин",
                answer2 = "Сергей Лукьяненко",
                answer3 = "Дмитрий Глуховский",
                correctAnswer = 2
            ),
            Question(
                id = 5,
                text = "Как называется школа, в которой учился Гарри Поттер?",
                answer1 = "Хогвартс",
                answer2 = "Нарния",
                answer3 = "Средиземье",
                correctAnswer = 1
            )
        )

        questions.forEach { question ->
            insertQuestion(db, question)
        }
    }

    private fun insertQuestion(
        db: SQLiteDatabase,
        question: Question
    ) {
        val values = ContentValues().apply {
            put(COLUMN_ID, question.id)
            put(COLUMN_TEXT, question.text)
            put(COLUMN_ANSWER_1, question.answer1)
            put(COLUMN_ANSWER_2, question.answer2)
            put(COLUMN_ANSWER_3, question.answer3)
            put(COLUMN_CORRECT_ANSWER, question.correctAnswer)
        }

        db.insertOrThrow(TABLE_QUESTIONS, null, values)
    }

    fun getAllQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase

        db.query(
            TABLE_QUESTIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID ASC"
        ).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val textIndex = cursor.getColumnIndexOrThrow(COLUMN_TEXT)
            val answer1Index = cursor.getColumnIndexOrThrow(COLUMN_ANSWER_1)
            val answer2Index = cursor.getColumnIndexOrThrow(COLUMN_ANSWER_2)
            val answer3Index = cursor.getColumnIndexOrThrow(COLUMN_ANSWER_3)
            val correctIndex = cursor.getColumnIndexOrThrow(COLUMN_CORRECT_ANSWER)

            while (cursor.moveToNext()) {
                questions.add(
                    Question(
                        id = cursor.getInt(idIndex),
                        text = cursor.getString(textIndex),
                        answer1 = cursor.getString(answer1Index),
                        answer2 = cursor.getString(answer2Index),
                        answer3 = cursor.getString(answer3Index),
                        correctAnswer = cursor.getInt(correctIndex)
                    )
                )
            }
        }

        return questions
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "literature_quiz.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_QUESTIONS = "questions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TEXT = "question_text"
        private const val COLUMN_ANSWER_1 = "answer_1"
        private const val COLUMN_ANSWER_2 = "answer_2"
        private const val COLUMN_ANSWER_3 = "answer_3"
        private const val COLUMN_CORRECT_ANSWER = "correct_answer"
    }
}