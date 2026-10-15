package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialCalculation
import com.example.ui.theme.DarkBlueAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkError
import com.example.ui.theme.DarkPrimaryGreen
import com.example.ui.theme.DarkWarning

@Composable
fun ProfitCalculatorView(
    modifier: Modifier = Modifier
) {
    var entryFeeInput by remember { mutableStateOf("50") }
    var maxPlayersInput by remember { mutableStateOf("50") }
    var expectedPlayersInput by remember { mutableStateOf("50") }
    var verifiedPlayersInput by remember { mutableStateOf("30") }
    var firstPrizeInput by remember { mutableStateOf("500") }
    var secondPrizeInput by remember { mutableStateOf("200") }
    var thirdPrizeInput by remember { mutableStateOf("300") }
    var otherCostsInput by remember { mutableStateOf("0") }

    val entryFee = entryFeeInput.toDoubleOrNull() ?: 0.0
    val maxPlayers = maxPlayersInput.toIntOrNull() ?: 0
    val expectedPlayers = expectedPlayersInput.toIntOrNull() ?: 0
    val verifiedPlayers = verifiedPlayersInput.toIntOrNull() ?: 0
    val firstPrize = firstPrizeInput.toDoubleOrNull() ?: 0.0
    val secondPrize = secondPrizeInput.toDoubleOrNull() ?: 0.0
    val thirdPrize = thirdPrizeInput.toDoubleOrNull() ?: 0.0
    val otherCosts = otherCostsInput.toDoubleOrNull() ?: 0.0

    val calc = remember(entryFee, maxPlayers, expectedPlayers, verifiedPlayers, firstPrize, secondPrize, thirdPrize, otherCosts) {
        FinancialCalculation(
            entryFeeRupees = entryFee,
            maxParticipants = maxPlayers,
            expectedParticipants = expectedPlayers,
            verifiedParticipants = verifiedPlayers,
            firstPrizeRupees = firstPrize,
            secondPrizeRupees = secondPrize,
            thirdPrizeRupees = thirdPrize,
            otherCostsRupees = otherCosts
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("profit_calculator_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkPrimaryGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = DarkPrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Entry Fee & Profit Calculator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time mathematical model for tournament economics",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inputs: Entry fee & Players
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = entryFeeInput,
                    onValueChange = { entryFeeInput = it },
                    label = { Text("Entry Fee (₹)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = maxPlayersInput,
                    onValueChange = { maxPlayersInput = it },
                    label = { Text("Max Slots", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = expectedPlayersInput,
                    onValueChange = { expectedPlayersInput = it },
                    label = { Text("Expected Players", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = verifiedPlayersInput,
                    onValueChange = { verifiedPlayersInput = it },
                    label = { Text("Verified Players", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prize Distribution Inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = firstPrizeInput,
                    onValueChange = { firstPrizeInput = it },
                    label = { Text("1st Prize (₹)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = secondPrizeInput,
                    onValueChange = { secondPrizeInput = it },
                    label = { Text("2nd Prize (₹)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = thirdPrizeInput,
                    onValueChange = { thirdPrizeInput = it },
                    label = { Text("3rd Prize (₹)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calculated Output Cards
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "FINANCIAL SUMMARY & REVENUE REPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlueAccent
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CalcRow(
                        label = "Potential Total Collection (${calc.expectedParticipants} Expected × ₹${calc.entryFeeRupees.toInt()}):",
                        value = "₹${calc.potentialCollection.toInt()}",
                        color = DarkBlueAccent
                    )

                    CalcRow(
                        label = "Max Capacity Collection (${calc.maxParticipants} Max × ₹${calc.entryFeeRupees.toInt()}):",
                        value = "₹${calc.maxCapacityCollection.toInt()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    CalcRow(
                        label = "Actual Verified Collection (${calc.verifiedParticipants} Verified × ₹${calc.entryFeeRupees.toInt()}):",
                        value = "₹${calc.actualVerifiedCollection.toInt()}",
                        color = DarkPrimaryGreen,
                        bold = true
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DarkBorder)

                    CalcRow(
                        label = "Total Prize Pool Commitment:",
                        value = "- ₹${calc.totalPrizeCommitment.toInt()}",
                        color = DarkWarning
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DarkBorder)

                    // Projected Net Surplus
                    val isProfit = calc.projectedNetSurplus >= 0
                    CalcRow(
                        label = "Projected Net Surplus (Potential − Prizes):",
                        value = if (isProfit) "+ ₹${calc.projectedNetSurplus.toInt()}" else "- ₹${(-calc.projectedNetSurplus).toInt()}",
                        color = if (isProfit) DarkPrimaryGreen else DarkError,
                        bold = true
                    )

                    // Actual Verified Net Result
                    val isActualProfit = calc.actualVerifiedNetResult >= 0
                    CalcRow(
                        label = "Actual Verified Net Result (Verified − Prizes):",
                        value = if (isActualProfit) "+ ₹${calc.actualVerifiedNetResult.toInt()}" else "- ₹${(-calc.actualVerifiedNetResult).toInt()}",
                        color = if (isActualProfit) DarkPrimaryGreen else DarkError,
                        bold = true
                    )
                }
            }
        }
    }
}

@Composable
private fun CalcRow(
    label: String,
    value: String,
    color: Color,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = if (bold) 13.sp else 12.sp,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Bold,
            color = color
        )
    }
}
