package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AdminSettings
import com.example.data.model.Tournament
import com.example.ui.theme.DarkBlueAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkPrimaryGreen
import com.example.ui.theme.DarkWarning

@Composable
fun PaymentDialog(
    tournament: Tournament,
    adminSettings: AdminSettings,
    onDismiss: () -> Unit,
    onSubmitClaim: (teamName: String, inGameId: String, txnRef: String) -> Unit
) {
    val context = LocalContext.current
    var teamName by remember { mutableStateOf("") }
    var inGameId by remember { mutableStateOf("") }
    var txnReference by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val feeRupees = tournament.entryFeePaise / 100
    val regRef = remember { "EK-" + (10000..99999).random() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .testTag("payment_verification_dialog"),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = DarkPrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Tournament Registration",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ref: $regRef",
                                fontSize = 11.sp,
                                color = DarkBlueAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Details Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tournament:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = tournament.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Entry Fee Required:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (feeRupees == 0L) "FREE" else "₹$feeRupees",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkPrimaryGreen
                            )
                        }

                        if (feeRupees > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            // UPI ID Box with 1-tap Copy
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", adminSettings.upiId))
                                        Toast.makeText(context, "UPI ID copied: ${adminSettings.upiId}", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Admin Payment UPI ID (Tap to Copy):",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = adminSettings.upiId,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkBlueAccent
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = DarkBlueAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Inputs
                OutlinedTextField(
                    value = inGameId,
                    onValueChange = { inGameId = it },
                    label = { Text("Your In-Game UID / Character ID *") },
                    placeholder = { Text("e.g. 5189240182") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ingame_id_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team Name / Tag") },
                    placeholder = { Text("e.g. Apex Predators") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("team_name_input"),
                    singleLine = true
                )

                if (feeRupees > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = txnReference,
                        onValueChange = { txnReference = it },
                        label = { Text("12-Digit UPI Ref / UTR / Txn ID *") },
                        placeholder = { Text("e.g. 428901238491") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("txn_reference_input"),
                        singleLine = true
                    )
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorText ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (inGameId.isBlank()) {
                                errorText = "Please enter your in-game UID"
                                return@Button
                            }
                            if (feeRupees > 0 && txnReference.isBlank()) {
                                errorText = "Please enter your payment 12-digit UTR/Txn reference"
                                return@Button
                            }
                            onSubmitClaim(teamName.trim(), inGameId.trim(), txnReference.trim())
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkPrimaryGreen,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("submit_payment_claim_button")
                    ) {
                        Text(
                            text = if (feeRupees == 0L) "Confirm Join" else "Submit for Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
