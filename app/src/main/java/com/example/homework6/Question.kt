package com.example.homework6

data class Question(
    val id: Int,
    val text: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val correctAnswer: Int
)