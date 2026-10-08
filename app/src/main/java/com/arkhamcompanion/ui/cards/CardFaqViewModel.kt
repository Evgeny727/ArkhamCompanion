package com.arkhamcompanion.ui.cards

import androidx.lifecycle.ViewModel
import com.arkhamcompanion.UiErrorState
import com.arkhamcompanion.domain.repository.CardsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CardFaqViewModel @Inject constructor(
    private val cardsRepository: CardsRepository
) : ViewModel() {
    private val _errors = MutableSharedFlow<UiErrorState>(extraBufferCapacity = 1)
    val errors: SharedFlow<UiErrorState> = _errors

    private fun emitError(throwable: Throwable) {
        _errors.tryEmit(UiErrorState(throwable))
    }

    fun getCardFaq(cardCode: String) = cardsRepository.getCardFaqByCodeFlow(cardCode).map {
        if (it.isSuccess) {
            it.getOrNull()
        } else {
            emitError(it.exceptionOrNull()!!)
            null
        }
    }
}