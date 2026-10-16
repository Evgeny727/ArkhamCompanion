package com.arkhamcompanion.ui.cards

import androidx.lifecycle.ViewModel
import com.arkhamcompanion.domain.repository.CardsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardFaqViewModel @Inject constructor(
    private val cardsRepository: CardsRepository
) : ViewModel() {
    fun getCardFaq(cardCode: String) = cardsRepository.getCardFaqByCodeFlow(cardCode)
}