package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Registration
import com.example.data.model.Tournament
import com.example.ui.theme.BannerGradients
import com.example.ui.theme.DarkBlueAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkError
import com.example.ui.theme.DarkPrimaryGreen
import com.example.ui.theme.DarkPurpleAccent
import com.example.ui.theme.DarkWarning

@Composable
fun TournamentCard(
    tournament: Tournament,
    userRegistration: Registration?,
    onClick: () -> Unit,
    onJoinClick: () -> Unit,
    onViewRoomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = BannerGradients[tournament.bannerGradientIndex % BannerGradients.size]
    val entryFeeText = if (tournament.entryFeePaise == 0L) "FREE ENTRY" else "₹${tournament.entryFeePaise / 100}"
    val totalPrizeText = "₹${tournament.totalPrizeCommitmentPaise / 100}"
    val isConfirmed = userRegistration?.registrationStatus == "CONFIRMED"
    val isUnderReview = userRegistration?.paymentStatus == "UNDER_REVIEW"
    val isPendingClaim = userRegistration != null && userRegistration.paymentStatus == "PENDING_CLAIM"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tournament_card_${tournament.id}")
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isConfirmed) 1.5.dp else 1.dp,
                color = if (isConfirmed) DarkPrimaryGreen else DarkBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(Brush.horizontalGradient(gradientColors))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Category Badge & Demo Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.55f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = DarkBlueAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${tournament.gameMode} • ${tournament.matchFormat}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        if (tournament.isDemo) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkPurpleAccent.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkPurpleAccent)
                            ) {
                                Text(
                                    text = "DEMO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DarkPurpleAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Status Badge
                    StatusPill(status = tournament.status)
                }

                // Banner bottom: Entry fee tag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENTRY: $entryFeeText",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkPrimaryGreen
                    )

                    Text(
                        text = "PRIZE: $totalPrizeText",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkWarning
                    )
                }
            }

            // Card Body
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = tournament.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (tournament.championTeamName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Champion",
                            tint = DarkWarning,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Winner: ${tournament.championTeamName}",
                            color = DarkWarning,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats: Verified Slots & Schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Slots info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = DarkBlueAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${tournament.verifiedCount}/${tournament.maxParticipants} Verified Slots",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Time info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tournament.startAt,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Green Join / Status Action Button (Section 10 of master prompt)
                when {
                    isConfirmed -> {
                        Button(
                            onClick = onViewRoomClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkPrimaryGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("view_room_button_${tournament.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "✓ Registration Confirmed (Room Info)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    isUnderReview -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkWarning.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkWarning),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clickable(onClick = onClick)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⏳ Payment Under Review by Admin",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DarkWarning
                                )
                            }
                        }
                    }
                    isPendingClaim -> {
                        Button(
                            onClick = onJoinClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkPrimaryGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text(
                                text = "Submit Payment UTR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    tournament.status == "COMPLETED" -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tournament Completed",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    tournament.verifiedCount >= tournament.maxParticipants -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tournament Full (Registration Closed)",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    else -> {
                        // Standard Primary Green Join Button
                        Button(
                            onClick = onJoinClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkPrimaryGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("join_tournament_button_${tournament.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (tournament.entryFeePaise == 0L) "Join Tournament (FREE)" else "Join Tournament ($entryFeeText)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "ONGOING" -> Triple(DarkError.copy(alpha = 0.2f), DarkError, "● LIVE NOW")
        "COMPLETED" -> Triple(DarkPrimaryGreen.copy(alpha = 0.2f), DarkPrimaryGreen, "✓ COMPLETED")
        else -> Triple(DarkWarning.copy(alpha = 0.2f), DarkWarning, "UPCOMING")
    }

    Surface(
        shape = CircleShape,
        color = bgColor
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
