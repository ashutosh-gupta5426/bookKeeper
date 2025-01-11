package com.example.bookkeeper


data class Book(
    var id: Int = 0,
    var bookName: String = "",
    var author: String = "",
    var summary: String = "",
    var publicationYear: Int = 0,
    var publishedBy: String = "",
    var genre: String = "",
    var image: ByteArray ?= null
)
