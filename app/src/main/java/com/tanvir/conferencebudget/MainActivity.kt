package com.tanvir.conferencebudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tanvir.conferencebudget.ui.navigation.AppNavigation
import com.tanvir.conferencebudget.ui.theme.ConferenceBudgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConferenceBudgetTheme {
                AppNavigation()
            }
        }
    }
}
