package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminSettings
import com.example.data.model.ChatMessage
import com.example.data.model.PaymentClaim
import com.example.data.model.Registration
import com.example.data.model.Tournament
import com.example.data.model.TournamentPrivate
import com.example.data.model.UserProfile
import com.example.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventKashifViewModel(application: Application) : AndroidViewModel(application) {

    val repository = FirebaseRepository.create(application)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile: StateFlow<UserProfile?> = _currentUser.flatMapLatest { user ->
        if (user == null) {
            flowOf(null)
        } else {
            repository.observeUserProfile(user.uid)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isUserAdmin: Boolean
        get() {
            val user = _currentUser.value
            val profile = userProfile.value
            return repository.isOwner(user?.email) || profile?.role == "admin"
        }

    // Filters
    private val _selectedCategory = MutableStateFlow("ALL") // ALL, BR, CS, LONE_WOLF
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow("ALL") // ALL, REAL, DEMO
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _selectedStatus = MutableStateFlow("ALL") // ALL, UPCOMING, ONGOING, COMPLETED
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val allTournaments: StateFlow<List<Tournament>> = repository.observeTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTournaments: StateFlow<List<Tournament>> = combine(
        allTournaments,
        _selectedCategory,
        _selectedType,
        _selectedStatus,
        _searchQuery
    ) { tournaments, category, type, status, query ->
        tournaments.filter { t ->
            val matchesCategory = category == "ALL" || t.gameMode.equals(category, ignoreCase = true)
            val matchesType = when (type) {
                "REAL" -> !t.isDemo
                "DEMO" -> t.isDemo
                else -> true
            }
            val matchesStatus = status == "ALL" || t.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() || t.title.contains(query, ignoreCase = true) || t.gameMode.contains(query, ignoreCase = true)
            matchesCategory && matchesType && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Tournament
    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    val selectedTournament: StateFlow<Tournament?> = _selectedTournament.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedRoomDetails: StateFlow<TournamentPrivate?> = _selectedTournament.flatMapLatest { tourney ->
        if (tourney == null) flowOf(null) else repository.observeRoomDetails(tourney.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // User Registrations
    @OptIn(ExperimentalCoroutinesApi::class)
    val userRegistrations: StateFlow<List<Registration>> = _currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.observeUserRegistrations(user.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Pending Claims
    val pendingClaims: StateFlow<List<PaymentClaim>> = repository.observePendingPaymentClaims()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat messages
    @OptIn(ExperimentalCoroutinesApi::class)
    val chatMessages: StateFlow<List<ChatMessage>> = _currentUser.flatMapLatest { user ->
        repository.observeMessages(user?.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Settings
    val adminSettings: StateFlow<AdminSettings> = repository.observeAdminSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminSettings())

    init {
        // Listen to Auth changes safely
        try {
            auth.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                _currentUser.value = user
                if (user != null) {
                    viewModelScope.launch {
                        try {
                            val isOwnerUser = repository.isOwner(user.email)
                            repository.saveUserProfile(
                                UserProfile(
                                    uid = user.uid,
                                    displayName = user.displayName ?: "Player",
                                    email = user.email ?: "",
                                    photoURL = user.photoUrl?.toString() ?: "",
                                    role = if (isOwnerUser) "admin" else "player"
                                )
                            )
                        } catch (e: Exception) {
                            android.util.Log.w("EventKashifVM", "Failed to sync user profile: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("EventKashifVM", "Auth listener attach error: ${e.message}")
        }

        // Seed initial data if empty
        viewModelScope.launch {
            try {
                repository.checkAndSeedInitialData()
            } catch (e: Exception) {
                android.util.Log.w("EventKashifVM", "Seeding check skipped: ${e.message}")
            }
        }
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun setTypeFilter(type: String) {
        _selectedType.value = type
    }

    fun setStatusFilter(status: String) {
        _selectedStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTournament(tournament: Tournament?) {
        _selectedTournament.value = tournament
    }

    fun refreshUser() {
        _currentUser.value = auth.currentUser
    }

    // Player Actions
    fun registerForTournament(
        tournament: Tournament,
        teamName: String,
        inGameId: String,
        onSuccess: (Registration) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = _currentUser.value
        val profile = userProfile.value
        if (user == null) {
            onError("Please log in with Google to join tournaments")
            return
        }

        // Check restriction
        if (profile?.accountStatus == "restricted" && profile.restrictionUntilMillis > System.currentTimeMillis()) {
            val minsLeft = ((profile.restrictionUntilMillis - System.currentTimeMillis()) / (60 * 1000)).coerceAtLeast(1)
            onError("Your account is temporarily restricted for $minsLeft minutes due to verification spam.")
            return
        }

        viewModelScope.launch {
            try {
                val reg = repository.registerPlayer(
                    tournament = tournament,
                    playerName = user.displayName ?: "Player",
                    teamName = teamName.ifBlank { "Solo Player" },
                    inGameId = inGameId.ifBlank { user.uid.take(8) }
                )
                onSuccess(reg)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun submitPaymentClaim(
        registrationId: String,
        tournamentId: String,
        tournamentTitle: String,
        claimedAmountPaise: Long,
        transactionReference: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (transactionReference.isBlank()) {
            onError("Please enter valid 12-digit UPI UTR or Transaction ID")
            return
        }

        viewModelScope.launch {
            try {
                repository.submitPaymentClaim(
                    registrationId = registrationId,
                    tournamentId = tournamentId,
                    tournamentTitle = tournamentTitle,
                    claimedAmountPaise = claimedAmountPaise,
                    transactionReference = transactionReference.trim()
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to submit payment claim")
            }
        }
    }

    fun updatePayoutSettings(preference: String, upiId: String) {
        val user = _currentUser.value ?: return
        val current = userProfile.value ?: UserProfile(uid = user.uid, email = user.email ?: "")
        viewModelScope.launch {
            repository.saveUserProfile(
                current.copy(
                    payoutPreference = preference,
                    upiId = upiId
                )
            )
        }
    }

    fun sendChatMessage(recipientUid: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(recipientUid = recipientUid, content = content)
        }
    }

    // Admin Actions
    fun createTournament(
        title: String,
        description: String,
        gameMode: String,
        matchFormat: String,
        entryFeeRupees: Double,
        maxParticipants: Long,
        expectedParticipants: Long,
        firstPrizeRupees: Double,
        secondPrizeRupees: Double,
        thirdPrizeRupees: Double,
        startAt: String,
        registrationDeadline: String,
        rules: String,
        roomId: String,
        roomPass: String,
        isDemo: Boolean,
        onSuccess: () -> Unit
    ) {
        val entryFeePaise = (entryFeeRupees * 100).toLong()
        val p1Paise = (firstPrizeRupees * 100).toLong()
        val p2Paise = (secondPrizeRupees * 100).toLong()
        val p3Paise = (thirdPrizeRupees * 100).toLong()
        val totalPrizePaise = p1Paise + p2Paise + p3Paise

        val tournament = Tournament(
            title = title,
            description = description,
            gameMode = gameMode,
            matchFormat = matchFormat,
            entryFeePaise = entryFeePaise,
            maxParticipants = maxParticipants,
            expectedParticipants = expectedParticipants,
            firstPrizePaise = p1Paise,
            secondPrizePaise = p2Paise,
            thirdPrizePaise = p3Paise,
            totalPrizeCommitmentPaise = totalPrizePaise,
            startAt = startAt,
            registrationDeadline = registrationDeadline,
            status = "UPCOMING",
            isDemo = isDemo,
            rules = rules,
            bannerGradientIndex = (0..5).random()
        )

        viewModelScope.launch {
            repository.createTournament(tournament, roomId, roomPass)
            onSuccess()
        }
    }

    fun acceptPaymentClaim(claim: PaymentClaim) {
        viewModelScope.launch {
            repository.acceptPaymentClaim(claim)
        }
    }

    fun rejectPaymentClaim(claim: PaymentClaim, reason: String) {
        viewModelScope.launch {
            repository.rejectPaymentClaim(claim, reason)
        }
    }

    fun restrictUser(playerUid: String, minutes: Int, reason: String) {
        viewModelScope.launch {
            repository.restrictUserSpam(playerUid, minutes, reason)
        }
    }

    fun updateTournamentStatus(tournamentId: String, status: String) {
        viewModelScope.launch {
            repository.updateTournamentStatus(tournamentId, status)
        }
    }

    fun publishChampion(tournamentId: String, championName: String) {
        viewModelScope.launch {
            repository.publishChampion(tournamentId, championName)
        }
    }

    fun updateRoomCredentials(tournamentId: String, roomId: String, pass: String) {
        viewModelScope.launch {
            repository.updateRoomDetails(tournamentId, roomId, pass)
        }
    }

    fun updateAdminSettings(settings: AdminSettings) {
        viewModelScope.launch {
            repository.updateAdminSettings(settings)
        }
    }

    fun sendRedemptionCodeToWinner(recipientUid: String, code: String, note: String) {
        viewModelScope.launch {
            val content = "🎉 Congratulations! Here is your official Tournament Prize Redemption Code: $code\nNote: $note"
            repository.sendMessage(
                recipientUid = recipientUid,
                content = content,
                isRedemptionCode = true,
                redemptionCode = code
            )
        }
    }
}
