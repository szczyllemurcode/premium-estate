package com.kkostrubiec.premiumestate.presentation.propertylist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kkostrubiec.premiumestate.domain.model.OfferType
import com.kkostrubiec.premiumestate.domain.model.Property
import com.kkostrubiec.premiumestate.domain.usecase.GetPropertiesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for PropertyListViewModel following clean architecture and MVVM best practices.
 * Tests cover all scenarios: success, error, loading states, and edge cases.
 *
 * Uses Turbine for Flow testing, MockK for mocking, and follows structured concurrency patterns.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PropertyListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // Mock dependencies
    private val getPropertiesUseCase: GetPropertiesUseCase = mockk()

    // System under test
    private lateinit var viewModel: PropertyListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading with empty properties list`() = runTest {
        // Given
        coEvery { getPropertiesUseCase() } returns Result.success(emptyList())

        // When
        viewModel = PropertyListViewModel(getPropertiesUseCase)

        // Then
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue("Initial state should be loading", initialState.isLoading)
            assertTrue("Initial properties list should be empty", initialState.properties.isEmpty())
            assertNull("Initial error should be null", initialState.error)
        }
    }

    @Test
    fun `when loadProperties succeeds, should emit loading then success state`() = runTest {
        // Given
        val mockProperties = createMockPropertiesList()
        coEvery { getPropertiesUseCase() } returns Result.success(mockProperties)

        // When
        viewModel = PropertyListViewModel(getPropertiesUseCase)

        // Then
        viewModel.uiState.test {
            // Loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)
            assertTrue("Properties should be empty during loading", loadingState.properties.isEmpty())
            assertNull("Error should be null during loading", loadingState.error)

            // Success state
            val successState = awaitItem()
            assertFalse("Should not be loading after success", successState.isLoading)
            assertEquals("Properties should match mock data", mockProperties, successState.properties)
            assertNull("Error should be null on success", successState.error)
        }

        // Verify use case was called exactly once
        coVerify(exactly = 1) { getPropertiesUseCase() }
    }

    @Test
    fun `when loadProperties fails, should emit loading then error state`() = runTest {
        // Given
        val errorMessage = "Network connection failed"
        val exception = RuntimeException(errorMessage)
        coEvery { getPropertiesUseCase() } returns Result.failure(exception)

        // When
        viewModel = PropertyListViewModel(getPropertiesUseCase)

        // Then
        viewModel.uiState.test {
            // Loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)

            // Error state
            val errorState = awaitItem()
            assertFalse("Should not be loading after error", errorState.isLoading)
            assertTrue("Properties should remain empty on error", errorState.properties.isEmpty())
            assertEquals("Error message should match", errorMessage, errorState.error)
        }

        coVerify(exactly = 1) { getPropertiesUseCase() }
    }

    @Test
    fun `when retry is called, should reload properties and emit loading state again`() = runTest {
        // Given
        val mockProperties = createMockPropertiesList()
        coEvery { getPropertiesUseCase() } returns Result.success(mockProperties)

        viewModel = PropertyListViewModel(getPropertiesUseCase)

        // Wait for initial load to complete
        viewModel.uiState.test {
            skipItems(2) // Skip loading and success states

            // When
            viewModel.retry()

            // Then
            val retryLoadingState = awaitItem()
            assertTrue("Should be loading on retry", retryLoadingState.isLoading)
            assertNull("Error should be cleared on retry", retryLoadingState.error)

            val retrySuccessState = awaitItem()
            assertFalse("Should not be loading after retry success", retrySuccessState.isLoading)
            assertEquals("Properties should be loaded on retry", mockProperties, retrySuccessState.properties)
        }

        // Verify use case was called twice (initial + retry)
        coVerify(exactly = 2) { getPropertiesUseCase() }
    }

    @Test
    fun `when retry is called after error, should clear error and reload`() = runTest {
        // Given
        val errorMessage = "Network error"
        val exception = RuntimeException(errorMessage)
        val mockProperties = createMockPropertiesList()

        // First call fails, second call succeeds
        coEvery { getPropertiesUseCase() } returnsMany listOf(
            Result.failure(exception),
            Result.success(mockProperties)
        )

        viewModel = PropertyListViewModel(getPropertiesUseCase)

        viewModel.uiState.test {
            // Skip initial loading and error states
            skipItems(2)

            // When
            viewModel.retry()

            // Then
            val retryLoadingState = awaitItem()
            assertTrue("Should be loading on retry", retryLoadingState.isLoading)
            assertNull("Error should be cleared", retryLoadingState.error)

            val retrySuccessState = awaitItem()
            assertFalse("Should not be loading after success", retrySuccessState.isLoading)
            assertEquals("Properties should be loaded", mockProperties, retrySuccessState.properties)
            assertNull("Error should remain null", retrySuccessState.error)
        }

        coVerify(exactly = 2) { getPropertiesUseCase() }
    }

    @Test
    fun `when properties list is empty, should handle empty state correctly`() = runTest {
        // Given
        coEvery { getPropertiesUseCase() } returns Result.success(emptyList())

        // When
        viewModel = PropertyListViewModel(getPropertiesUseCase)

        // Then
        viewModel.uiState.test {
            skipItems(1) // Skip loading state
            val successState = awaitItem()

            assertFalse("Should not be loading", successState.isLoading)
            assertTrue("Properties list should be empty", successState.properties.isEmpty())
            assertNull("Error should be null", successState.error)
        }
    }

    @Test
    fun `ui state should be immutable and properly structured`() = runTest {
        // Given
        val mockProperties = createMockPropertiesList()
        coEvery { getPropertiesUseCase() } returns Result.success(mockProperties)

        // When
        viewModel = PropertyListViewModel(getPropertiesUseCase)

        // Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            val successState = awaitItem()

            // Verify state immutability by checking different instances
            assertTrue("States should be different instances", loadingState !== successState)

            // Verify state structure
            assertTrue("Loading state should have isLoading=true", loadingState.isLoading)
            assertFalse("Success state should have isLoading=false", successState.isLoading)
        }
    }

    @Test
    fun `when multiple retries are called rapidly, should handle gracefully`() = runTest {
        // Given
        val mockProperties = createMockPropertiesList()
        coEvery { getPropertiesUseCase() } returns Result.success(mockProperties)

        viewModel = PropertyListViewModel(getPropertiesUseCase)

        // Wait for initial load
        viewModel.uiState.test {
            skipItems(2) // Skip loading and success

            // When - call retry multiple times rapidly
            repeat(3) {
                viewModel.retry()
            }

            // Then - should handle all calls
            // We expect at least one loading and one success state
            val states = mutableListOf<PropertyListUiState>()
            repeat(6) { // Collect several states
                states.add(awaitItem())
            }

            // Verify that we have loading states
            assertTrue("Should have loading states", states.any { it.isLoading })
            assertTrue("Should have success states", states.any { !it.isLoading && it.error == null })
        }

        // Verify use case was called multiple times (initial + retries)
        coVerify(atLeast = 4) { getPropertiesUseCase() }
    }

    /**
     * Helper function to create mock properties list for testing
     */
    private fun createMockPropertiesList(): List<Property> = listOf(
        Property(
            id = 1,
            bedrooms = 3,
            city = "Paris",
            area = 120.0,
            imageUrl = "https://example.com/image1.jpg",
            price = 850000.0,
            professional = "John Doe",
            propertyType = "Apartment",
            offerType = OfferType.SALE,
            rooms = 4
        ),
        Property(
            id = 2,
            bedrooms = 2,
            city = "Lyon",
            area = 80.0,
            imageUrl = "https://example.com/image2.jpg",
            price = 1200.0,
            professional = "Jane Smith",
            propertyType = "House",
            offerType = OfferType.RENT,
            rooms = 3
        ),
        Property(
            id = 3,
            bedrooms = null,
            city = "Marseille",
            area = 45.0,
            imageUrl = null,
            price = 350000.0,
            professional = "Bob Wilson",
            propertyType = "Studio",
            offerType = OfferType.SALE,
            rooms = null
        )
    )
}
