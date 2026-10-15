package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class UserRole {
    ADMIN, PLAYER
}

enum class AccountStatus {
    ACTIVE, RESTRICTED, BANNED
}

enum class TournamentCategory(val code: String, val title: String, val desc: String) {
    BR("BR", "Battle Royale", "Solo, Duo, Squad lobbies on Bermuda & Erangel"),
    CS("CS", "Clash Squad", "Fast-paced 4v4 tactical squad battles"),
    LONE_WOLF("LONE_WOLF", "Lone Wolf", "1v1 hardcore duel tournaments")
}

enum class TournamentStatus(val label: String) {
    UPCOMING("Upcoming"),
    ONGOING("LIVE NOW"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class RegistrationStatus {
    PENDING, CONFIRMED, REJECTED, CANCELLED
}

enum class PaymentStatus {
    PENDING_CLAIM, UNDER_REVIEW, VERIFIED, REJECTED
}

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val uid: String = "",
    val displayName: String = "Player",
    val email: String = "",
    val photoURL: String = "",
    val role: String = "player", // "admin" or "player"
    val accountStatus: String = "active", // "active", "restricted", "banned"
    val restrictionUntilMillis: Long = 0L,
    val payoutPreference: String = "upi", // "upi" or "redemption_code"
    val upiId: String = "",
    val tournamentsPlayed: Int = 0,
    val tournamentsWon: Int = 0,
    val totalEarningsPaise: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Tournament(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val gameMode: String = "BR", // BR, CS, LONE_WOLF
    val matchFormat: String = "Squad (4v4)",
    val entryFeePaise: Long = 5000L, // in Paise (e.g. 5000 = ₹50)
    val maxParticipants: Long = 48L,
    val expectedParticipants: Long = 40L,
    val registeredCount: Long = 0L,
    val verifiedCount: Long = 0L,
    val firstPrizePaise: Long = 100000L, // ₹1,000
    val secondPrizePaise: Long = 50000L,  // ₹500
    val thirdPrizePaise: Long = 25000L,   // ₹250
    val totalPrizeCommitmentPaise: Long = 175000L,
    val startAt: String = "Tonight 8:00 PM",
    val registrationDeadline: String = "Tonight 7:30 PM",
    val status: String = "UPCOMING", // UPCOMING, ONGOING, COMPLETED
    val isDemo: Boolean = false,
    val rules: String = "No hacking, emulators or teaming allowed. Fair play strictly enforced.",
    val bannerGradientIndex: Int = 0,
    val championTeamName: String = "",
    val createdBy: String = "admin",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TournamentPrivate(
    @DocumentId val tournamentId: String = "",
    val roomId: String = "",
    val roomPassword: String = "",
    val releaseTime: String = "15 mins before match",
    val instructions: String = "Enter custom room using Room ID & Password. Do not share with unverified players.",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Registration(
    @DocumentId val id: String = "",
    val tournamentId: String = "",
    val tournamentTitle: String = "",
    val playerUid: String = "",
    val playerName: String = "",
    val playerEmail: String = "",
    val teamName: String = "",
    val inGameId: String = "",
    val registrationStatus: String = "PENDING", // PENDING, CONFIRMED, REJECTED
    val paymentStatus: String = "PENDING_CLAIM", // PENDING_CLAIM, UNDER_REVIEW, VERIFIED, REJECTED
    val transactionReference: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val verifiedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PaymentClaim(
    @DocumentId val id: String = "",
    val registrationId: String = "",
    val tournamentId: String = "",
    val tournamentTitle: String = "",
    val playerUid: String = "",
    val playerName: String = "",
    val playerEmail: String = "",
    val claimedAmountPaise: Long = 0L,
    val transactionReference: String = "", // UTR / Txn ID
    val status: String = "SUBMITTED", // SUBMITTED, APPROVED, REJECTED
    val rejectionReason: String = "",
    @ServerTimestamp val submittedAt: Timestamp? = null,
    @ServerTimestamp val reviewedAt: Timestamp? = null,
    val reviewedBy: String = ""
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val senderRole: String = "player", // "admin" or "player"
    val recipientUid: String = "",
    val tournamentId: String = "",
    val content: String = "",
    val isRedemptionCode: Boolean = false,
    val redemptionCode: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AdminSettings(
    @DocumentId val id: String = "settings",
    val upiId: String = "kashif.tournaments@upi",
    val accountHolderName: String = "Kashif (EVENT KASHIF Admin)",
    val paymentInstructions: String = "1. Scan QR code or pay to UPI ID.\n2. Note down 12-digit UTR / Reference number.\n3. Enter UTR number below and submit for instant verification.",
    val qrImageUrl: String = "",
    val ownerEmail: String = "shahidafridi08657@gmail.com",
    val announcements: String = "🔥 Welcome to EVENT KASHIF! Register now for weekend BGMI & Free Fire cash cups!"
)

data class FinancialCalculation(
    val entryFeeRupees: Double,
    val maxParticipants: Int,
    val expectedParticipants: Int,
    val verifiedParticipants: Int,
    val firstPrizeRupees: Double,
    val secondPrizeRupees: Double,
    val thirdPrizeRupees: Double,
    val otherCostsRupees: Double
) {
    val potentialCollection: Double get() = entryFeeRupees * expectedParticipants
    val maxCapacityCollection: Double get() = entryFeeRupees * maxParticipants
    val actualVerifiedCollection: Double get() = entryFeeRupees * verifiedParticipants
    val totalPrizeCommitment: Double get() = firstPrizeRupees + secondPrizeRupees + thirdPrizeRupees
    val projectedNetSurplus: Double get() = potentialCollection - totalPrizeCommitment - otherCostsRupees
    val actualVerifiedNetResult: Double get() = actualVerifiedCollection - totalPrizeCommitment - otherCostsRupees
}
