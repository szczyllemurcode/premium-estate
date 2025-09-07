package com.kkostrubiec.premiumestate.presentation.propertylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkostrubiec.premiumestate.domain.model.Property
import com.kkostrubiec.premiumestate.domain.usecase.GetPropertiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor(
    private val getPropertiesUseCase: GetPropertiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyListUiState())
    val uiState: StateFlow<PropertyListUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getPropertiesUseCase()
                .onSuccess { properties ->
                    _uiState.value = _uiState.value.copy(
                        properties = properties,
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

    fun retry() {
        loadProperties()
    }
}

data class PropertyListUiState(
    val properties: List<Property> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
