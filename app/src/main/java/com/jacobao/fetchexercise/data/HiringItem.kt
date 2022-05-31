package com.jacobao.fetchexercise.data

data class HiringItem(
    val id: Int, // assume IDs fit in a 32 bit Int
    val listId: Int,
    val name: String?
)