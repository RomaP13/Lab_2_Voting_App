package com.example.myapplication.model

data class VoteOption(
    val id: Int,
    val title: String,
    val votes: Int = 0
)