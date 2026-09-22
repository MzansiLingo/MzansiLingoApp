package com.example.mzantsilingo.outstanding

data class CulturalCard(
    val word: String,
    val meaning: String,
    val example: String,
    val translation: String
)

object CulturalCards {

    val cards = listOf(
        CulturalCard(
            word = "Molo",
            meaning = "Hello",
            example = "Molo, unjani?",
            translation = "Hello, how are you?"
        ),
        CulturalCard(
            word = "Enkosi",
            meaning = "Thank you",
            example = "Enkosi kakhulu.",
            translation = "Thank you very much."
        ),
        CulturalCard(
            word = "Umama",
            meaning = "Mother",
            example = "Umama uyapheka.",
            translation = "Mother is cooking."
        ),
        CulturalCard(
            word = "Ikhaya",
            meaning = "Home",
            example = "Ndiyaya ekhaya.",
            translation = "I am going home."
        )
    )
}
