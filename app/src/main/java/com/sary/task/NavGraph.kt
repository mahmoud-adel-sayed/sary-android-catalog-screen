package com.sary.task

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sary.task.store.ui.Store

enum class MainTabs(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String
) {
    STORE(R.string.store, R.drawable.ic_storefront, MainDestinations.STORE_ROUTE),
    ORDERS(R.string.orders, R.drawable.ic_orders, MainDestinations.ORDERS_ROUTE),
    MY_PAGE(R.string.my_page, R.drawable.ic_my_page, MainDestinations.MY_PAGE_ROUTE)
}

/**
 * Destinations used in the App.
 */
object MainDestinations {
    const val STORE_ROUTE = "store"
    const val ORDERS_ROUTE = "orders"
    const val MY_PAGE_ROUTE = "my_page"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.STORE_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.STORE_ROUTE) {
            Store()
        }
        composable(MainDestinations.ORDERS_ROUTE) {

        }
        composable(MainDestinations.MY_PAGE_ROUTE) {

        }
    }
}

/**
 * Models the navigation actions in the app.
 */
@Suppress("unused")
class MainActions(navController: NavHostController) {
    val upPress: (from: NavBackStackEntry) -> Unit = { from ->
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigateUp()
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED