package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.AdminSettings
import com.example.data.model.ChatMessage
import com.example.data.model.PaymentClaim
import com.example.data.model.Registration
import com.example.data.model.Tournament
import com.example.data.model.TournamentPrivate
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    companion object {
        private const val TAG = "FirebaseRepository"

        fun create(context: Context): FirebaseRepository {
            try {
                if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                    com.google.firebase.FirebaseApp.initializeApp(context)
                }
            } catch (e: Exception) {
                Log.e(TAG, "FirebaseApp check failed in repository", e)
            }

            val databaseId = try {
                context.getString(R.string.firestore_database_id)
            } catch (e: Exception) {
                "(default)"
            }
            val firestore = try {
                if (databaseId.isNotBlank() && databaseId != "(default)") {
                    FirebaseFirestore.getInstance(databaseId)
                } else {
                    FirebaseFirestore.getInstance()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get named Firestore instance, falling back to default", e)
                try {
                    FirebaseFirestore.getInstance()
                } catch (e2: Exception) {
                    Log.e(TAG, "Fatal firestore instance error", e2)
                    throw e2
                }
            }
            val auth = try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                Log.e(TAG, "Error obtaining FirebaseAuth instance", e)
                FirebaseAuth.getInstance()
            }
            return FirebaseRepository(firestore, auth)
        }
    }

    val currentUserId: String? get() = auth.currentUser?.uid
    val currentUserEmail: String? get() = auth.currentUser?.email

    fun isOwner(email: String?): Boolean {
        val check = email?.lowercase() ?: currentUserEmail?.lowercase() ?: ""
        return check == "shahidafridi08657@gmail.com" || check == "kashif@eventkashif.com"
    }

    // --- Users ---
    fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val docRef = db.collection("users").document(uid)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed for user profile", error)
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                trySend(profile)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val docRef = db.collection("users").document(profile.uid)
        val isOwnerUser = isOwner(profile.email)
        val roleToSave = if (isOwnerUser) "admin" else profile.role
        val data = mapOf(
            "uid" to profile.uid,
            "displayName" to profile.displayName,
            "email" to profile.email,
            "photoURL" to profile.photoURL,
            "role" to roleToSave,
            "accountStatus" to profile.accountStatus,
            "restrictionUntilMillis" to profile.restrictionUntilMillis,
            "payoutPreference" to profile.payoutPreference,
            "upiId" to profile.upiId,
            "tournamentsPlayed" to profile.tournamentsPlayed,
            "tournamentsWon" to profile.tournamentsWon,
            "totalEarningsPaise" to profile.totalEarningsPaise,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    // --- Tournaments ---
    fun observeTournaments(): Flow<List<Tournament>> = callbackFlow {
        val query = db.collection("tournaments")
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Tournaments snapshot error", error)
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Tournament::class.java)
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun createTournament(tournament: Tournament, roomId: String, roomPass: String) {
        val tourneyId = if (tournament.id.isNotBlank()) tournament.id else UUID.randomUUID().toString()
        val docRef = db.collection("tournaments").document(tourneyId)

        val data = mapOf(
            "id" to tourneyId,
            "title" to tournament.title,
            "description" to tournament.description,
            "gameMode" to tournament.gameMode,
            "matchFormat" to tournament.matchFormat,
            "entryFeePaise" to tournament.entryFeePaise,
            "maxParticipants" to tournament.maxParticipants,
            "expectedParticipants" to tournament.expectedParticipants,
            "registeredCount" to tournament.registeredCount,
            "verifiedCount" to tournament.verifiedCount,
            "firstPrizePaise" to tournament.firstPrizePaise,
            "secondPrizePaise" to tournament.secondPrizePaise,
            "thirdPrizePaise" to tournament.thirdPrizePaise,
            "totalPrizeCommitmentPaise" to tournament.totalPrizeCommitmentPaise,
            "startAt" to tournament.startAt,
            "registrationDeadline" to tournament.registrationDeadline,
            "status" to tournament.status,
            "isDemo" to tournament.isDemo,
            "rules" to tournament.rules,
            "bannerGradientIndex" to tournament.bannerGradientIndex,
            "championTeamName" to tournament.championTeamName,
            "createdBy" to (auth.currentUser?.email ?: "admin"),
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(data).await()

        // Set private room credentials
        if (roomId.isNotBlank()) {
            val privateRef = db.collection("tournamentPrivate").document(tourneyId)
            privateRef.set(
                mapOf(
                    "tournamentId" to tourneyId,
                    "roomId" to roomId,
                    "roomPassword" to roomPass,
                    "releaseTime" to "15 minutes before match",
                    "instructions" to "Join room on time. Strictly no teaming.",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun updateTournamentStatus(id: String, status: String) {
        db.collection("tournaments").document(id)
            .update("status", status)
            .await()
    }

    suspend fun publishChampion(id: String, championName: String) {
        db.collection("tournaments").document(id)
            .update(
                mapOf(
                    "championTeamName" to championName,
                    "status" to "COMPLETED"
                )
            ).await()
    }

    // --- Private Room Credentials ---
    fun observeRoomDetails(tournamentId: String): Flow<TournamentPrivate?> = callbackFlow {
        val docRef = db.collection("tournamentPrivate").document(tournamentId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(TournamentPrivate::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateRoomDetails(tournamentId: String, roomId: String, roomPass: String) {
        db.collection("tournamentPrivate").document(tournamentId)
            .set(
                mapOf(
                    "tournamentId" to tournamentId,
                    "roomId" to roomId,
                    "roomPassword" to roomPass,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()
    }

    // --- Registrations ---
    fun observeUserRegistrations(playerUid: String): Flow<List<Registration>> = callbackFlow {
        val query = db.collection("registrations").whereEqualTo("playerUid", playerUid)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Registration::class.java) } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun registerPlayer(
        tournament: Tournament,
        playerName: String,
        teamName: String,
        inGameId: String
    ): Registration {
        val uid = auth.currentUser?.uid ?: UUID.randomUUID().toString()
        val email = auth.currentUser?.email ?: ""
        val regId = "${tournament.id}_$uid"

        val reg = Registration(
            id = regId,
            tournamentId = tournament.id,
            tournamentTitle = tournament.title,
            playerUid = uid,
            playerName = playerName,
            playerEmail = email,
            teamName = teamName,
            inGameId = inGameId,
            registrationStatus = "PENDING",
            paymentStatus = "PENDING_CLAIM",
            transactionReference = ""
        )

        val docRef = db.collection("registrations").document(regId)
        docRef.set(
            mapOf(
                "id" to regId,
                "tournamentId" to tournament.id,
                "tournamentTitle" to tournament.title,
                "playerUid" to uid,
                "playerName" to playerName,
                "playerEmail" to email,
                "teamName" to teamName,
                "inGameId" to inGameId,
                "registrationStatus" to "PENDING",
                "paymentStatus" to "PENDING_CLAIM",
                "transactionReference" to "",
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()

        // Increment registeredCount on tournament
        db.collection("tournaments").document(tournament.id)
            .update("registeredCount", FieldValue.increment(1))
            .await()

        return reg
    }

    // --- Payment Claims ---
    fun observePendingPaymentClaims(): Flow<List<PaymentClaim>> = callbackFlow {
        val query = db.collection("paymentClaims").whereEqualTo("status", "SUBMITTED")
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(PaymentClaim::class.java) } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun submitPaymentClaim(
        registrationId: String,
        tournamentId: String,
        tournamentTitle: String,
        claimedAmountPaise: Long,
        transactionReference: String
    ) {
        val uid = auth.currentUser?.uid ?: ""
        val email = auth.currentUser?.email ?: ""
        val claimId = UUID.randomUUID().toString()

        val data = mapOf(
            "id" to claimId,
            "registrationId" to registrationId,
            "tournamentId" to tournamentId,
            "tournamentTitle" to tournamentTitle,
            "playerUid" to uid,
            "playerName" to (auth.currentUser?.displayName ?: "Player"),
            "playerEmail" to email,
            "claimedAmountPaise" to claimedAmountPaise,
            "transactionReference" to transactionReference,
            "status" to "SUBMITTED",
            "submittedAt" to FieldValue.serverTimestamp()
        )

        db.collection("paymentClaims").document(claimId).set(data).await()

        // Update registration payment status
        db.collection("registrations").document(registrationId)
            .update(
                mapOf(
                    "paymentStatus" to "UNDER_REVIEW",
                    "transactionReference" to transactionReference
                )
            ).await()
    }

    suspend fun acceptPaymentClaim(claim: PaymentClaim) {
        val reviewer = auth.currentUser?.email ?: "admin"

        // 1. Update Payment Claim
        db.collection("paymentClaims").document(claim.id)
            .update(
                mapOf(
                    "status" to "APPROVED",
                    "reviewedAt" to FieldValue.serverTimestamp(),
                    "reviewedBy" to reviewer
                )
            ).await()

        // 2. Set Registration to CONFIRMED
        db.collection("registrations").document(claim.registrationId)
            .update(
                mapOf(
                    "registrationStatus" to "CONFIRMED",
                    "paymentStatus" to "VERIFIED",
                    "verifiedAt" to FieldValue.serverTimestamp()
                )
            ).await()

        // 3. Increment verifiedCount on tournament
        db.collection("tournaments").document(claim.tournamentId)
            .update("verifiedCount", FieldValue.increment(1))
            .await()

        // 4. Record entry in Financial Ledger
        val ledgerId = UUID.randomUUID().toString()
        db.collection("financialLedger").document(ledgerId)
            .set(
                mapOf(
                    "id" to ledgerId,
                    "tournamentId" to claim.tournamentId,
                    "registrationId" to claim.registrationId,
                    "playerUid" to claim.playerUid,
                    "eventType" to "VERIFIED_ENTRY_FEE",
                    "amountPaise" to claim.claimedAmountPaise,
                    "referenceId" to claim.transactionReference,
                    "status" to "CONFIRMED",
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

        // 5. Audit Log
        recordAuditLog("ACCEPT_PAYMENT", "PaymentClaim", claim.id, "Verified ₹${claim.claimedAmountPaise / 100} for player ${claim.playerName}")
    }

    suspend fun rejectPaymentClaim(claim: PaymentClaim, reason: String) {
        val reviewer = auth.currentUser?.email ?: "admin"
        db.collection("paymentClaims").document(claim.id)
            .update(
                mapOf(
                    "status" to "REJECTED",
                    "rejectionReason" to reason,
                    "reviewedAt" to FieldValue.serverTimestamp(),
                    "reviewedBy" to reviewer
                )
            ).await()

        db.collection("registrations").document(claim.registrationId)
            .update("paymentStatus", "REJECTED")
            .await()

        recordAuditLog("REJECT_PAYMENT", "PaymentClaim", claim.id, "Rejected with reason: $reason")
    }

    suspend fun restrictUserSpam(playerUid: String, minutes: Int, reason: String) {
        val until = System.currentTimeMillis() + (minutes * 60 * 1000L)
        db.collection("users").document(playerUid)
            .update(
                mapOf(
                    "accountStatus" to "restricted",
                    "restrictionUntilMillis" to until
                )
            ).await()

        recordAuditLog("RESTRICT_USER", "User", playerUid, "Restricted for $minutes mins. Reason: $reason")
    }

    suspend fun liftRestriction(playerUid: String) {
        db.collection("users").document(playerUid)
            .update(
                mapOf(
                    "accountStatus" to "active",
                    "restrictionUntilMillis" to 0L
                )
            ).await()
    }

    // --- In-App Chat & Messages ---
    fun observeMessages(playerUid: String?): Flow<List<ChatMessage>> = callbackFlow {
        val query = db.collection("messages").orderBy("createdAt", Query.Direction.ASCENDING)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
            val filtered = if (playerUid != null && !isOwner(null)) {
                list.filter { it.senderUid == playerUid || it.recipientUid == playerUid }
            } else {
                list
            }
            trySend(filtered)
        }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(
        recipientUid: String,
        content: String,
        isRedemptionCode: Boolean = false,
        redemptionCode: String = ""
    ) {
        val sender = auth.currentUser
        val senderUid = sender?.uid ?: "admin_kashif"
        val senderName = sender?.displayName ?: if (isOwner(null)) "Admin Kashif" else "Player"
        val senderRole = if (isOwner(null)) "admin" else "player"

        val msgId = UUID.randomUUID().toString()
        val data = mapOf(
            "id" to msgId,
            "senderUid" to senderUid,
            "senderName" to senderName,
            "senderRole" to senderRole,
            "recipientUid" to recipientUid,
            "content" to content,
            "isRedemptionCode" to isRedemptionCode,
            "redemptionCode" to redemptionCode,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("messages").document(msgId).set(data).await()
    }

    // --- Admin Settings ---
    fun observeAdminSettings(): Flow<AdminSettings> = callbackFlow {
        val docRef = db.collection("adminSettings").document("settings")
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(AdminSettings())
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(AdminSettings::class.java) ?: AdminSettings())
            } else {
                trySend(AdminSettings())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateAdminSettings(settings: AdminSettings) {
        db.collection("adminSettings").document("settings")
            .set(settings, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    private suspend fun recordAuditLog(action: String, targetType: String, targetId: String, details: String) {
        val logId = UUID.randomUUID().toString()
        db.collection("auditLogs").document(logId)
            .set(
                mapOf(
                    "id" to logId,
                    "actorUid" to (auth.currentUser?.uid ?: "system"),
                    "actionType" to action,
                    "targetType" to targetType,
                    "targetId" to targetId,
                    "details" to details,
                    "timestamp" to FieldValue.serverTimestamp()
                )
            ).await()
    }

    // --- Seed Demo & Initial Real Tournaments ---
    suspend fun checkAndSeedInitialData() {
        try {
            val snap = db.collection("tournaments").limit(1).get().await()
            if (snap.isEmpty) {
                // 1. Real Battle Royale (BR) Squad Tournament
                createTournament(
                    tournament = Tournament(
                        id = "tourney-br-weekend-clash",
                        title = "EVENT KASHIF Pro BR Clash (Squad)",
                        description = "Official Battle Royale Championship with cash prizes. Erangel & Bermuda custom maps.",
                        gameMode = "BR",
                        matchFormat = "Squad (4v4)",
                        entryFeePaise = 5000L, // ₹50 / player
                        maxParticipants = 48L,
                        expectedParticipants = 40L,
                        registeredCount = 28L,
                        verifiedCount = 22L,
                        firstPrizePaise = 100000L, // ₹1,000
                        secondPrizePaise = 50000L,  // ₹500
                        thirdPrizePaise = 25000L,   // ₹250
                        totalPrizeCommitmentPaise = 175000L,
                        startAt = "Tonight at 8:30 PM",
                        registrationDeadline = "Tonight 8:00 PM",
                        status = "ONGOING",
                        isDemo = false,
                        bannerGradientIndex = 0
                    ),
                    roomId = "889412",
                    roomPass = "kashif889"
                )

                // 2. Real Clash Squad (CS) Tournament
                createTournament(
                    tournament = Tournament(
                        id = "tourney-cs-strike-series",
                        title = "Clash Squad Elite Masters",
                        description = "7 rounds tactical clash squad tournament. Gun attributes OFF, character skills ON.",
                        gameMode = "CS",
                        matchFormat = "4v4 Squad Duel",
                        entryFeePaise = 4000L, // ₹40 / player
                        maxParticipants = 16L,
                        expectedParticipants = 16L,
                        registeredCount = 12L,
                        verifiedCount = 8L,
                        firstPrizePaise = 60000L, // ₹600
                        secondPrizePaise = 30000L, // ₹300
                        thirdPrizePaise = 10000L,  // ₹100
                        totalPrizeCommitmentPaise = 100000L,
                        startAt = "Tomorrow 7:00 PM",
                        registrationDeadline = "Tomorrow 6:30 PM",
                        status = "UPCOMING",
                        isDemo = false,
                        bannerGradientIndex = 1
                    ),
                    roomId = "772109",
                    roomPass = "cselite44"
                )

                // 3. Real Lone Wolf (1v1) Duel Tournament
                createTournament(
                    tournament = Tournament(
                        id = "tourney-lonewolf-duel",
                        title = "Lone Wolf 1v1 Gladiator Cup",
                        description = "Hardcore solo 1v1 duel tournament. Test your individual aim and movement mechanics.",
                        gameMode = "LONE_WOLF",
                        matchFormat = "1v1 Solo",
                        entryFeePaise = 2000L, // ₹20 / player
                        maxParticipants = 32L,
                        expectedParticipants = 24L,
                        registeredCount = 18L,
                        verifiedCount = 15L,
                        firstPrizePaise = 30000L, // ₹300
                        secondPrizePaise = 15000L, // ₹150
                        thirdPrizePaise = 5000L,   // ₹50
                        totalPrizeCommitmentPaise = 50000L,
                        startAt = "Sunday 6:00 PM",
                        registrationDeadline = "Sunday 5:30 PM",
                        status = "UPCOMING",
                        isDemo = false,
                        bannerGradientIndex = 2
                    ),
                    roomId = "551201",
                    roomPass = "gladiator1v1"
                )

                // 4. Clearly Marked DEMO Tournament for testing
                createTournament(
                    tournament = Tournament(
                        id = "demo-tourney-simulation",
                        title = "[DEMO] Free Fire Clash Squad Demo Cup",
                        description = "Simulation tournament for exploring app features. No real entry fee or cash prizes.",
                        gameMode = "CS",
                        matchFormat = "4v4 Squad",
                        entryFeePaise = 0L,
                        maxParticipants = 8L,
                        expectedParticipants = 8L,
                        registeredCount = 8L,
                        verifiedCount = 8L,
                        firstPrizePaise = 0L,
                        secondPrizePaise = 0L,
                        thirdPrizePaise = 0L,
                        totalPrizeCommitmentPaise = 0L,
                        startAt = "Simulation Mode",
                        registrationDeadline = "Open",
                        status = "COMPLETED",
                        isDemo = true,
                        championTeamName = "Demo Champions",
                        bannerGradientIndex = 3
                    ),
                    roomId = "DEMO-ROOM-99",
                    roomPass = "demo123"
                )

                // Default Admin Settings
                updateAdminSettings(
                    AdminSettings(
                        upiId = "kashif.tournaments@upi",
                        accountHolderName = "Kashif (EVENT KASHIF Admin)",
                        paymentInstructions = "1. Pay entry fee to UPI ID or scan QR.\n2. Copy the 12-digit UTR/Txn ID from your UPI app.\n3. Submit for verification to unlock your secret Room ID & Password.",
                        ownerEmail = "shahidafridi08657@gmail.com",
                        announcements = "Welcome to EVENT KASHIF! Real tournament schedules and instant verified room details."
                    )
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "checkAndSeedInitialData safe catch: ${e.message}")
        }
    }
}
