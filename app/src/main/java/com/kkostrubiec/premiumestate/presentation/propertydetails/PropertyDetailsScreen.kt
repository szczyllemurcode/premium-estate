package com.kkostrubiec.premiumestate.presentation.propertydetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kkostrubiec.premiumestate.data.model.Property
import com.kkostrubiec.premiumestate.presentation.common.ErrorScreen
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    listingId: Int,
    onBackClick: () -> Unit,
    viewModel: PropertyDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(listingId) {
        viewModel.loadPropertyDetails(listingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Property Details",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                !uiState.error.isNullOrEmpty() -> {
                    ErrorScreen(
                        title = "Property Details Unavailable",
                        message = "We couldn't load the details for this property. Please check your connection and try again.",
                        onRetry = { viewModel.retry(listingId) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.property != null -> {
                    PropertyDetailsContent(
                        property = uiState.property!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyDetailsContent(
    property: Property,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Property Image - only show if exists
        property.url?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Property image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Main Property Information
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = property.propertyType,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = property.city,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = formatPrice(property.price),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Property Specifications
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Property Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Area (always present)
                PropertyDetailRow(
                    label = "Area",
                    value = "${property.area.toInt()} m²"
                )

                // Rooms (conditional)
                property.rooms?.let { rooms ->
                    PropertyDetailRow(
                        label = "Total Rooms",
                        value = rooms.toString()
                    )
                }

                // Bedrooms (conditional)
                property.bedrooms?.let { bedrooms ->
                    PropertyDetailRow(
                        label = "Bedrooms",
                        value = bedrooms.toString()
                    )
                }

                // Offer Type
                PropertyDetailRow(
                    label = "Offer Type",
                    value = getOfferTypeText(property.offerType)
                )
            }
        }

        // Professional Information
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Listed by",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = property.professional,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Add bottom spacing for better scrolling experience
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PropertyDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        currency = Currency.getInstance("EUR")
    }
    return formatter.format(price)
}

private fun getOfferTypeText(offerType: Int): String {
    return when (offerType) {
        1 -> "For Sale"
        2 -> "For Rent"
        3 -> "Sold"
        4 -> "Rented"
        else -> "Unknown"
    }
}
