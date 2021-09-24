package com.sary.task

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.sary.task.theme.AppTheme
import com.sary.task.util.ProvideImageLoader

@Composable
fun SaryApp() {
    ProvideWindowInsets {
        ProvideImageLoader {
            AppTheme {
                val mainTabs = rememberMainTabs()
                val navController = rememberNavController()
                Scaffold(
                    content = { NavGraph(navController) },
                    bottomBar = { BottomBar(navController = navController, tabs = mainTabs) }
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    tabs: Array<MainTabs>
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: MainTabs.STORE.route

    BottomNavigation(
        modifier = modifier.navigationBarsHeight(additional = 56.dp),
        backgroundColor = AppTheme.colors.surface
    ) {
        tabs.forEach { tab ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(tab.icon), contentDescription = null) },
                label = { Text(text = stringResource(tab.title)) },
                selected = currentRoute == tab.route,
                onClick = {
                    if (tab.route != currentRoute) {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                alwaysShowLabel = true,
                selectedContentColor = AppTheme.colors.onSurface,
                unselectedContentColor = AppTheme.colors.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun rememberMainTabs(): Array<MainTabs> = remember { MainTabs.values() }

@Preview(name = "Bottom Bar")
@Preview(name = "Bottom Bar - Dark Theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun BottomBarPreview() {
    AppTheme {
        BottomBar(navController = rememberNavController(), tabs = rememberMainTabs())
    }
}