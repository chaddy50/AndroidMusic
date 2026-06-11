package com.chaddy50.musicapp.ui.screens.settingsScreen.listenBrainzLogin

import androidx.lifecycle.ViewModel
import com.chaddy50.musicapp.data.api.listenBrainz.ListenBrainzRepository
import com.chaddy50.musicapp.data.api.listenBrainz.TokenValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ListenBrainzLoginViewModel @Inject constructor(
    private val repository: ListenBrainzRepository,
) : ViewModel() {

    val tokenValidationState: StateFlow<TokenValidationState> = repository.tokenValidationState

    fun saveToken(token: String) = repository.saveToken(token)

    fun logout() = repository.logout()
}