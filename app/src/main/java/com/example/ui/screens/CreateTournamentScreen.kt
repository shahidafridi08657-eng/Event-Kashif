package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentCategory
import com.example.ui.theme.DarkBlueAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkPrimaryGreen
import com.example.ui.theme.DarkPurpleAccent
import com.example.ui.theme.DarkWarning
import com.example.ui.viewmodel.EventKashifViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(
    viewModel: EventKashifViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var title by remember { mutableStateOf("EVENT KASHIF Squad Clash S1") }
    var description by remember { mutableStateOf("Official custom room tournament with cash prizes. Live room details on confirmation.") }
    var selectedCategory by remember { mutableStateOf("BR") }
    var matchFormat by remember { mutableStateOf("Squad (4v4)") }
    var entryFeeInput by remember { mutableStateOf("50") }
    var maxSlotsInput by remember { mutableStateOf("48") }
    var expectedSlotsInput by remember { mutableStateOf("40") }
    var firstPrizeInput by remember { mutableStateOf("1000") }
    var secondPrizeInput by remember { mutableStateOf("500") }
    var thirdPrizeInput by remember { mutableStateOf("250") }
    var startAt by remember { mutableStateOf("Tonight 8:30 PM") }
    var deadline by remember { mutableStateOf("Tonight 8:00 PM") }
    var rules by remember { mutableStateOf("Fair play rules strictly enforced. No emulators or hacking.") }
    var roomId by remember { mutableStateOf("881290") }
    var roomPass by remember { mutableStateOf("kashif2026") }
    var isDemoTournament by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fee = entryFeeInput.toDoubleOrNull() ?: 0.0
    val expected = expectedSlotsInput.toIntOrNull() ?: 0
    val p1 = firstPrizeInput.toDoubleOrNull() ?: 0.0
    val p2 = secondPrizeInput.toDoubleOrNull() ?: 0.0
    val p3 = thirdPrizeInput.toDoubleOrNull() ?: 0.0
    val potentialGross = fee * expected
    val totalPrizes = p1 + p2 + p3
    val projectedNet = potentialGross - totalPrizes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Publish New Tournament", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode Selector
            Text("1. CHOOSE GAME MODE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "BR" to "Battle Royale",
                    "CS" to "Clash Squad",
                    "LONE_WOLF" to "Lone Wolf (1v1)"
                ).forEach { (code, label) ->
                    val isSel = selectedCategory == code
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            selectedCategory = code
                            matchFormat = when (code) {
                                "BR" -> "Squad (4v4)"
                                "CS" -> "4v4 Squad Duel"
                                else -> "1v1 Solo"
                            }
                            title = "EVENT KASHIF $label Cup"
                        },
                        label = { Text(label, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tournament Title & Description
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tournament Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Short Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Format & Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = matchFormat,
                    onValueChange = { matchFormat = it },
                    label = { Text("Match Format") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = entryFeeInput,
                    onValueChange = { entryFeeInput = it },
                    label = { Text("Entry Fee (₹) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = maxSlotsInput,
                    onValueChange = { maxSlotsInput = it },
                    label = { Text("Max Slots") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = expectedSlotsInput,
                    onValueChange = { expectedSlotsInput = it },
                    label = { Text("Expected Slots") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Prize Distribution
            Text("2. PRIZE POOL BREAKDOWN (₹)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = firstPrizeInput,
                    onValueChange = { firstPrizeInput = it },
                    label = { Text("1st (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = secondPrizeInput,
                    onValueChange = { secondPrizeInput = it },
                    label = { Text("2nd (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = thirdPrizeInput,
                    onValueChange = { thirdPrizeInput = it },
                    label = { Text("3rd (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Live Profit Preview Strip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ESTIMATED REVENUE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${potentialGross.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkBlueAccent)
                    }
                    Column {
                        Text("TOTAL PRIZES", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${totalPrizes.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkWarning)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("PROJECTED PROFIT", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (projectedNet >= 0) "+₹${projectedNet.toInt()}" else "-₹${(-projectedNet).toInt()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (projectedNet >= 0) DarkPrimaryGreen else Color.Red
                        )
                    }
                }
            }

            // Room Credentials
            Text("3. PRIVATE ROOM CREDENTIALS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = roomId,
                    onValueChange = { roomId = it },
                    label = { Text("Room ID") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = roomPass,
                    onValueChange = { roomPass = it },
                    label = { Text("Password") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Schedule & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = startAt,
                    onValueChange = { startAt = it },
                    label = { Text("Start Time") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Registration Ends") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Real vs Demo Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mark as DEMO Simulation Tournament", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Demo tournaments are marked with a DEMO badge and excluded from cash payouts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isDemoTournament,
                    onCheckedChange = { isDemoTournament = it }
                )
            }

            if (errorMessage != null) {
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            // Submit Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter a tournament title"
                        return@Button
                    }
                    viewModel.createTournament(
                        title = title.trim(),
                        description = description.trim(),
                        gameMode = selectedCategory,
                        matchFormat = matchFormat.trim(),
                        entryFeeRupees = fee,
                        maxParticipants = maxSlotsInput.toLongOrNull() ?: 48L,
                        expectedParticipants = expectedSlotsInput.toLongOrNull() ?: 40L,
                        firstPrizeRupees = p1,
                        secondPrizeRupees = p2,
                        thirdPrizeRupees = p3,
                        startAt = startAt.trim(),
                        registrationDeadline = deadline.trim(),
                        rules = rules.trim(),
                        roomId = roomId.trim(),
                        roomPass = roomPass.trim(),
                        isDemo = isDemoTournament,
                        onSuccess = onBack
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_tournament_button")
            ) {
                Text("Publish Tournament to Live Feed", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
