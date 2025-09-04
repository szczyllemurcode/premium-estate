package com.kkostrubiec.premiumestate.presentation.propertydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkostrubiec.premiumestate.data.model.Property
import com.kkostrubiec.premiumestate.data.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyDetailsViewModel @Inject constructor(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyDetailsUiState())
    val uiState: StateFlow<PropertyDetailsUiState> = _uiState.asStateFlow()

    fun loadPropertyDetails(listingId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getPropertyDetails(listingId)
                .onSuccess { property ->
                    _uiState.value = _uiState.value.copy(
                        property = property,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun retry(listingId: Int) {
        loadPropertyDetails(listingId)
    }
}

data class PropertyDetailsUiState(
    val property: Property? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
