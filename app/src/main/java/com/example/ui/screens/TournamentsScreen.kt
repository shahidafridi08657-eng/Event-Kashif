package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Tournament
import com.example.ui.components.TournamentCard
import com.example.ui.viewmodel.EventKashifViewModel

@Composable
fun TournamentsScreen(
    viewModel: EventKashifViewModel,
    onTournamentClick: (Tournament) -> Unit,
    onJoinTournamentClick: (Tournament) -> Unit,
    onViewRoomClick: (Tournament) -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.filteredTournaments.collectAsStateWithLifecycle()
    val userRegistrations by viewModel.userRegistrations.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Search Bar
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search tournament name or mode...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tournaments_search_bar"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Statuses",
                    "ONGOING" to "● Live Now",
                    "UPCOMING" to "Upcoming",
                    "COMPLETED" to "Completed"
                ).forEach { (code, label) ->
                    val isSel = selectedStatus == code
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.setStatusFilter(code) },
                        label = { Text(label, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tournaments List
        items(tournaments, key = { it.id }) { tourney ->
            val userReg = userRegistrations.firstOrNull { it.tournamentId == tourney.id }
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TournamentCard(
                    tournament = tourney,
                    userRegistration = userReg,
                    onClick = { onTournamentClick(tourney) },
                    onJoinClick = { onJoinTournamentClick(tourney) },
                    onViewRoomClick = { onViewRoomClick(tourney) }
                )
            }
        }
    }
}
