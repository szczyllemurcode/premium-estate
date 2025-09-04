package com.kkostrubiec.premiumestate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kkostrubiec.premiumestate.presentation.propertydetails.PropertyDetailsScreen
import com.kkostrubiec.premiumestate.presentation.propertylist.PropertyListScreen
import com.kkostrubiec.premiumestate.ui.theme.PremiumEstateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PremiumEstateTheme {
                PremiumEstateApp()
            }
        }
    }
}

@Composable
fun PremiumEstateApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "property_list",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("property_list") {
            PropertyListScreen(
                onPropertyClick = { propertyId ->
                    navController.navigate("property_details/$propertyId")
                }
            )
        }

        composable("property_details/{listingId}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId")?.toIntOrNull() ?: 0
            PropertyDetailsScreen(
                listingId = listingId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}