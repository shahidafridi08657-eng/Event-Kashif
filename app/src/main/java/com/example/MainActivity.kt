package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Tournament
import com.example.data.model.TournamentPrivate
import com.example.ui.components.AuthScreen
import com.example.ui.components.PaymentDialog
import com.example.ui.components.RoomDetailsCard
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreateTournamentScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyMatchesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TournamentsScreen
import com.example.ui.theme.DarkPrimaryGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EventKashifViewModel

enum class NavigationTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Home", Icons.Default.Home),
    TOURNAMENTS("Tournaments", Icons.Default.SportsEsports),
    MY_MATCHES("My Matches", Icons.Default.EmojiEvents),
    MESSAGES("Messages", Icons.Default.Chat),
    PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val viewModel: EventKashifViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "FirebaseApp init check in onCreate", e)
        }
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EventKashifApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun EventKashifApp(viewModel: EventKashifViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val adminSettings by viewModel.adminSettings.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var isGuestBrowsing by remember { mutableStateOf(false) }

    var selectedTournamentForJoin by remember { mutableStateOf<Tournament?>(null) }
    var selectedTournamentForRoom by remember { mutableStateOf<Tournament?>(null) }
    var roomDetailsForModal by remember { mutableStateOf<TournamentPrivate?>(null) }
    var showAdminDashboard by remember { mutableStateOf(false) }
    var showCreateTournament by remember { mutableStateOf(false) }

    // If user is not logged in and hasn't chosen guest browsing, show AuthScreen
    if (currentUser == null && !isGuestBrowsing) {
        AuthScreen(
            onAuthSuccess = {
                viewModel.refreshUser()
                isGuestBrowsing = true
            },
            onContinueAsGuest = {
                isGuestBrowsing = true
            }
        )
        return
    }

    // Admin Dashboard Screen
    if (showAdminDashboard) {
        AdminDashboardScreen(
            viewModel = viewModel,
            onBack = { showAdminDashboard = false },
            onCreateTournamentClick = {
                showAdminDashboard = false
                showCreateTournament = true
            }
        )
        return
    }

    // Create Tournament Screen
    if (showCreateTournament) {
        CreateTournamentScreen(
            viewModel = viewModel,
            onBack = { showCreateTournament = false }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = DarkPrimaryGreen,
                            indicatorColor = DarkPrimaryGreen
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onTournamentClick = { tourney ->
                            selectedTournamentForJoin = tourney
                        },
                        onJoinTournamentClick = { tourney ->
                            selectedTournamentForJoin = tourney
                        },
                        onViewRoomClick = { tourney ->
                            viewModel.selectTournament(tourney)
                            selectedTournamentForRoom = tourney
                        },
                        onNavigateToProfile = { currentTab = NavigationTab.PROFILE }
                    )
                }
                NavigationTab.TOURNAMENTS -> {
                    TournamentsScreen(
                        viewModel = viewModel,
                        onTournamentClick = { tourney ->
                            selectedTournamentForJoin = tourney
                        },
                        onJoinTournamentClick = { tourney ->
                            selectedTournamentForJoin = tourney
                        },
                        onViewRoomClick = { tourney ->
                            viewModel.selectTournament(tourney)
                            selectedTournamentForRoom = tourney
                        }
                    )
                }
                NavigationTab.MY_MATCHES -> {
                    MyMatchesScreen(
                        viewModel = viewModel,
                        onViewRoomClick = { tourney ->
                            viewModel.selectTournament(tourney)
                            selectedTournamentForRoom = tourney
                        },
                        onSubmitPaymentClick = { tourney ->
                            selectedTournamentForJoin = tourney
                        },
                        onExploreClick = { currentTab = NavigationTab.TOURNAMENTS }
                    )
                }
                NavigationTab.MESSAGES -> {
                    ChatScreen(viewModel = viewModel)
                }
                NavigationTab.PROFILE -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToAdmin = { showAdminDashboard = true },
                        onSignOut = {
                            isGuestBrowsing = false
                            viewModel.refreshUser()
                        }
                    )
                }
            }
        }
    }

    // Payment Dialog
    selectedTournamentForJoin?.let { tourney ->
        PaymentDialog(
            tournament = tourney,
            adminSettings = adminSettings,
            onDismiss = { selectedTournamentForJoin = null },
            onSubmitClaim = { teamName, inGameId, txnRef ->
                viewModel.registerForTournament(
                    tournament = tourney,
                    teamName = teamName,
                    inGameId = inGameId,
                    onSuccess = { reg ->
                        if (tourney.entryFeePaise > 0) {
                            viewModel.submitPaymentClaim(
                                registrationId = reg.id,
                                tournamentId = tourney.id,
                                tournamentTitle = tourney.title,
                                claimedAmountPaise = tourney.entryFeePaise,
                                transactionReference = txnRef,
                                onSuccess = {
                                    Toast.makeText(context, "Payment claim submitted! Admin will verify and unlock Room ID.", Toast.LENGTH_LONG).show()
                                    selectedTournamentForJoin = null
                                    currentTab = NavigationTab.MY_MATCHES
                                },
                                onError = { err ->
                                    Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            // Free entry tournament
                            Toast.makeText(context, "Joined successfully! Check My Matches.", Toast.LENGTH_SHORT).show()
                            selectedTournamentForJoin = null
                            currentTab = NavigationTab.MY_MATCHES
                        }
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }

    // Room ID Details Dialog
    selectedTournamentForRoom?.let { tourney ->
        val selectedRoomDetails by viewModel.selectedRoomDetails.collectAsStateWithLifecycle()
        Dialog(onDismissRequest = { selectedTournamentForRoom = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                RoomDetailsCard(
                    roomDetails = selectedRoomDetails ?: TournamentPrivate(
                        tournamentId = tourney.id,
                        roomId = "Releasing 15 mins prior",
                        roomPassword = "Check before match",
                        instructions = "Room details are private for confirmed players."
                    ),
                    matchTime = tourney.startAt
                )
            }
        }
    }
}
