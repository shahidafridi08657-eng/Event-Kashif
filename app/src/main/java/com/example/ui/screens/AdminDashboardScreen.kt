package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AdminSettings
import com.example.data.model.PaymentClaim
import com.example.data.model.Tournament
import com.example.ui.components.ProfitCalculatorView
import com.example.ui.theme.DarkBlueAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkError
import com.example.ui.theme.DarkPrimaryGreen
import com.example.ui.theme.DarkPurpleAccent
import com.example.ui.theme.DarkWarning
import com.example.ui.viewmodel.EventKashifViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: EventKashifViewModel,
    onBack: () -> Unit,
    onCreateTournamentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val pendingClaims by viewModel.pendingClaims.collectAsStateWithLifecycle()
    val allTournaments by viewModel.allTournaments.collectAsStateWithLifecycle()
    val adminSettings by viewModel.adminSettings.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Claims (${pendingClaims.size})",
        "Profit Calc",
        "Manage Rooms",
        "Issue Code",
        "Payment Settings"
    )

    // Financial Overview Calculations
    val verifiedTourneys = allTournaments.filter { !it.isDemo }
    val totalVerifiedEntries = verifiedTourneys.sumOf { it.verifiedCount }
    val totalVerifiedCollection = verifiedTourneys.sumOf { it.verifiedCount * (it.entryFeePaise / 100) }
    val totalPrizeCommitment = verifiedTourneys.sumOf { it.totalPrizeCommitmentPaise / 100 }
    val netSurplus = totalVerifiedCollection - totalPrizeCommitment

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = DarkWarning,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Panel • EVENT KASHIF",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onCreateTournamentClick) {
                        Icon(Icons.Default.Add, contentDescription = "New Tourney", tint = DarkPrimaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Financial Ledger Overview Strip
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("VERIFIED COLLECTION", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text("₹$totalVerifiedCollection", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DarkPrimaryGreen)
                    }
                    Column {
                        Text("PRIZE COMMITMENT", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text("₹$totalPrizeCommitment", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkWarning)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("NET MARGIN", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (netSurplus >= 0) "+₹$netSurplus" else "-₹${-netSurplus}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (netSurplus >= 0) DarkPrimaryGreen else DarkError
                        )
                    }
                }
            }

            // Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title, fontSize = 11.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal, maxLines = 1) }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Pending Claims List
                        PendingClaimsSection(
                            claims = pendingClaims,
                            onAccept = { claim ->
                                viewModel.acceptPaymentClaim(claim)
                                Toast.makeText(context, "Claim accepted & Room unlocked for ${claim.playerName}", Toast.LENGTH_SHORT).show()
                            },
                            onReject = { claim, reason ->
                                viewModel.rejectPaymentClaim(claim, reason)
                                Toast.makeText(context, "Claim rejected", Toast.LENGTH_SHORT).show()
                            },
                            onRestrict = { claim, mins ->
                                viewModel.restrictUser(claim.playerUid, mins, "Repeated unverified payment claims spam")
                                viewModel.rejectPaymentClaim(claim, "Account restricted for $mins minutes")
                                Toast.makeText(context, "Player restricted for $mins minutes", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    1 -> {
                        // Profit Calculator
                        ProfitCalculatorView()
                    }
                    2 -> {
                        // Manage Rooms
                        ManageRoomsSection(
                            tournaments = allTournaments,
                            onUpdateRoom = { id, room, pass ->
                                viewModel.updateRoomCredentials(id, room, pass)
                                Toast.makeText(context, "Room credentials updated!", Toast.LENGTH_SHORT).show()
                            },
                            onUpdateStatus = { id, st ->
                                viewModel.updateTournamentStatus(id, st)
                            },
                            onPublishWinner = { id, winner ->
                                viewModel.publishChampion(id, winner)
                                Toast.makeText(context, "Tournament completed & champion announced!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    3 -> {
                        // Issue Redemption Code Section
                        IssueRedemptionCodeSection(
                            onSendCode = { uid, code, note ->
                                viewModel.sendRedemptionCodeToWinner(uid, code, note)
                                Toast.makeText(context, "Redemption code delivered to player's chat!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    4 -> {
                        // Admin Payment Settings
                        AdminSettingsSection(
                            settings = adminSettings,
                            onSave = { updated ->
                                viewModel.updateAdminSettings(updated)
                                Toast.makeText(context, "Payment settings saved!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingClaimsSection(
    claims: List<PaymentClaim>,
    onAccept: (PaymentClaim) -> Unit,
    onReject: (PaymentClaim, String) -> Unit,
    onRestrict: (PaymentClaim, Int) -> Unit
) {
    if (claims.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Check, contentDescription = null, tint = DarkPrimaryGreen, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("All Clear! No Pending Payment Claims", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("New player payment requests will appear here for review.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(claims, key = { it.id }) { claim ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = claim.playerName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = claim.playerEmail, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "₹${claim.claimedAmountPaise / 100}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkPrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Tournament: ${claim.tournamentTitle}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkBlueAccent)
                    Text(text = "UTR / Reference: ${claim.transactionReference}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Actions: Accept, Reject, Restrict
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onAccept(claim) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Accept ✓", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onReject(claim, "Transaction ID not found in bank statement") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reject", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onRestrict(claim, 10) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Restrict 10m", fontSize = 10.sp, color = DarkError)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManageRoomsSection(
    tournaments: List<Tournament>,
    onUpdateRoom: (tournamentId: String, roomId: String, pass: String) -> Unit,
    onUpdateStatus: (tournamentId: String, status: String) -> Unit,
    onPublishWinner: (tournamentId: String, winnerName: String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items(tournaments) { tourney ->
            var roomIdInput by remember { mutableStateOf("") }
            var passInput by remember { mutableStateOf("") }
            var winnerInput by remember { mutableStateOf(tourney.championTeamName) }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tourney.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(tourney.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkPrimaryGreen)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Room ID & Pass inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = roomIdInput,
                            onValueChange = { roomIdInput = it },
                            placeholder = { Text("New Room ID", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = passInput,
                            onValueChange = { passInput = it },
                            placeholder = { Text("Password", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (roomIdInput.isNotBlank()) {
                                    onUpdateRoom(tourney.id, roomIdInput.trim(), passInput.trim())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlueAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status and Winner controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onUpdateStatus(tourney.id, "ONGOING") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Mark Live", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onUpdateStatus(tourney.id, "COMPLETED") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Completed", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = winnerInput,
                            onValueChange = { winnerInput = it },
                            placeholder = { Text("Declare Winner / Champion", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (winnerInput.isNotBlank()) {
                                    onPublishWinner(tourney.id, winnerInput.trim())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkWarning, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Publish Winner", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IssueRedemptionCodeSection(
    onSendCode: (recipientUid: String, code: String, note: String) -> Unit
) {
    var recipientUid by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("1st Prize Google Play Code for EVENT KASHIF Cup") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Issue Prize Redemption Code", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkWarning)
            Text("Deliver a Google Play / Game Code directly into the winner's chat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = recipientUid,
                onValueChange = { recipientUid = it },
                label = { Text("Winner Player UID or Email") },
                placeholder = { Text("e.g. player UID or all_players") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = codeInput,
                onValueChange = { codeInput = it },
                label = { Text("Redemption Gift Code *") },
                placeholder = { Text("e.g. EK-PLAY-9482-1082-9901") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                label = { Text("Winner Note / Instructions") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (codeInput.isNotBlank()) {
                        onSendCode(recipientUid.ifBlank { "all_players" }, codeInput.trim(), noteInput.trim())
                        codeInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkWarning, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deliver Code to Player Chat", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminSettingsSection(
    settings: AdminSettings,
    onSave: (AdminSettings) -> Unit
) {
    var upiId by remember(settings.upiId) { mutableStateOf(settings.upiId) }
    var accountName by remember(settings.accountHolderName) { mutableStateOf(settings.accountHolderName) }
    var instructions by remember(settings.paymentInstructions) { mutableStateOf(settings.paymentInstructions) }
    var announcements by remember(settings.announcements) { mutableStateOf(settings.announcements) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Admin Payment & App Configuration", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkPrimaryGreen)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text("Official UPI ID for Player Payments") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = accountName,
                onValueChange = { accountName = it },
                label = { Text("Account Holder Display Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Payment Instructions for Players") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = announcements,
                onValueChange = { announcements = it },
                label = { Text("Home Screen Announcement Banner") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSave(
                        settings.copy(
                            upiId = upiId.trim(),
                            accountHolderName = accountName.trim(),
                            paymentInstructions = instructions.trim(),
                            announcements = announcements.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Admin Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
}
