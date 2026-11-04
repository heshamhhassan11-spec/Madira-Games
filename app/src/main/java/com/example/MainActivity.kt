package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import android.content.Intent
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import androidx.compose.ui.unit.LayoutDirection
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.data.model.ChallengeItem
import com.example.data.repository.AppDataRepository
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppNavDestination
import com.example.ui.screens.AdminRechargeScreen
import com.example.ui.screens.AuthScreen
import com.example.data.model.RechargeRequest
import com.example.ui.screens.SubscriptionRechargeScreen
import com.example.ui.screens.ChallengesMainScreen
import com.example.ui.screens.ClubGuessScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.PlayerGuessScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SquadBuilderScreen
import com.example.ui.screens.WorldCupScreen
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.MadiraGamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            MadiraGamesTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FootballChallengesApp()
                }
            }
        }
    }
}

@Composable
fun FootballChallengesApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val repository = remember { AppDataRepository(context) }
    val soundManager = remember { com.example.ui.util.SoundManager(context, repository.soundSettings) }

    // User session state from Firebase Auth & App authentication
    var firebaseUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isGuest by remember { mutableStateOf(false) }
    val isAppAuthenticated = firebaseUser != null || isGuest

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            firebaseUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
            soundManager.release()
        }
    }

    LaunchedEffect(firebaseUser) {
        val uid = firebaseUser?.uid
        if (uid != null) {
            Log.d("Auth", "Syncing Firestore for user: $uid")
            repository.syncWithFirestore(uid, this)
        } else {
            Log.d("Auth", "No user, not syncing.")
            // Reset to default or guest state if needed
        }
    }

    // Navigation state
    var currentDestination by remember { mutableStateOf(AppNavDestination.CHALLENGES) }
    var activeChallenge by remember { mutableStateOf<ChallengeItem?>(null) }
    var showAdminRecharge by remember { mutableStateOf(false) }
    var showSubscriptionScreen by remember { mutableStateOf(false) }
    val pendingRechargeRequests by repository.observeRechargeRequests().collectAsState(initial = emptyList())

    // Eye Comfort Font Scale
    var fontScale by remember { mutableFloatStateOf(1.0f) }

    // Observed Data
    val currentUser by repository.currentUser.collectAsState()
    val challenges by repository.challenges.collectAsState()
    val leaderboard by repository.leaderboard.collectAsState()
    val soundSettings by repository.soundSettings.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val friendsList by repository.friends.collectAsState()
    val friendRequestsList by repository.friendRequests.collectAsState()
    val transactionsList by repository.transactions.collectAsState()

    val favoriteChallenges = remember(challenges) {
        challenges.filter { it.isFavorite }
    }

    if (!isAppAuthenticated && firebaseUser == null) {
        // Login & Registration Auth Gate
        AuthScreen(
            onLoginSuccess = { name, email, club, avatar, isNewUser ->
                repository.login(email = email, name = name, club = club, avatar = avatar, isNewUser = isNewUser)
                firebaseUser = Firebase.auth.currentUser
            },
            onGuestLogin = {
                isGuest = true
                repository.loginAsGuest(context)
            },
            onAdminLogin = { phone, pass ->
                val success = repository.loginAsAdmin(phone, pass)
                if (success) {
                    isGuest = true
                }
                success
            },
            fontScale = fontScale
        )
    } else if (showAdminRecharge) {
        AdminRechargeScreen(
            requests = pendingRechargeRequests,
            onApprove = { reqId, _ ->
                val req = pendingRechargeRequests.find { it.id == reqId }
                if (req != null) {
                    coroutineScope.launch {
                        repository.approveRechargeRequest(reqId, req)
                    }
                }
            },
            onReject = { reqId, _ ->
                coroutineScope.launch {
                    repository.updateRechargeRequestStatus(reqId, "مرفوض")
                }
            },
            onBack = { showAdminRecharge = false },
            fontScale = fontScale
        )
    } else if (showSubscriptionScreen) {
        SubscriptionRechargeScreen(
            currentUsername = currentUser.name,
            currentUserTagId = currentUser.userTagId,
            onSubmitRequest = { packageTitle, method, phone, receiptUri ->
                val newReq = RechargeRequest(
                    userId = currentUser.userId.ifBlank { currentUser.id },
                    username = currentUser.name,
                    userTagId = currentUser.userTagId,
                    amount = packageTitle,
                    method = method,
                    phone = phone,
                    receiptImageUri = receiptUri,
                    status = "معلق"
                )
                repository.submitRechargeRequest(newReq)
                showSubscriptionScreen = false
            },
            onBack = { showSubscriptionScreen = false },
            fontScale = fontScale
        )
    } else if (activeChallenge != null) {
        // Active challenge playable screens (The 4 challenges)
        when (activeChallenge?.id) {
            "guess_player" -> {
                PlayerGuessScreen(
                    questions = repository.playerQuestions,
                    onBack = { activeChallenge = null },
                    onAddScore = { points -> repository.addPoints(points) },
                    onDeductCoinsAmount = { amount -> repository.deductCoinsAmount(amount) },
                    onGrantCoins = { amount -> repository.grantCoins(amount) },
                    onPlaySound = { soundKey -> soundManager.playSound(soundKey) },
                    fontScale = fontScale
                )
            }
            "guess_club" -> {
                ClubGuessScreen(
                    questions = repository.clubQuestions,
                    onBack = { activeChallenge = null },
                    onAddScore = { points -> repository.addPoints(points) },
                    onDeductCoinsAmount = { amount -> repository.deductCoinsAmount(amount) },
                    onGrantCoins = { amount -> repository.grantCoins(amount) },
                    onPlaySound = { soundKey -> soundManager.playSound(soundKey) },
                    fontScale = fontScale
                )
            }
            "world_cup" -> {
                WorldCupScreen(
                    questions = repository.worldCupQuestions,
                    onBack = { activeChallenge = null },
                    onAddScore = { points -> repository.addPoints(points) },
                    onDeductCoinsAmount = { amount -> repository.deductCoinsAmount(amount) },
                    onGrantCoins = { amount -> repository.grantCoins(amount) },
                    onPlaySound = { soundKey -> soundManager.playSound(soundKey) },
                    fontScale = fontScale
                )
            }
            "squad_builder" -> {
                SquadBuilderScreen(
                    playersPool = repository.squadPlayersPool,
                    tournamentChoices = repository.tournamentChoices,
                    onSimulate = { tournament, squad ->
                        repository.simulateTournament(tournament, squad)
                    },
                    onBack = { activeChallenge = null },
                    onDeductCoins = { repository.deductCoinsForChallenge("squad_builder") },
                    fontScale = fontScale
                )
            }
            else -> {
                activeChallenge = null
            }
        }
    } else {
        // Main scaffold with bottom navigation (No VIP/Subscriptions)
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkAppBackground),
            containerColor = DarkAppBackground,
            bottomBar = {
                AppBottomNavigation(
                    currentDestination = currentDestination,
                    onNavigate = { destination -> currentDestination = destination },
                    favoritesCount = favoriteChallenges.size,
                    fontScale = fontScale
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    AppNavDestination.CHALLENGES -> {
                        ChallengesMainScreen(
                            challenges = challenges,
                            notifications = notifications,
                            onMarkNotificationAsRead = { notifId -> repository.markNotificationAsRead(notifId) },
                            onMarkAllNotificationsAsRead = { repository.markAllNotificationsAsRead() },
                            onAddNotification = { title, desc, cat, ver ->
                                repository.addCustomNotification(title, desc, cat, ver)
                            },
                            onChallengeSelected = { challenge -> activeChallenge = challenge },
                            onToggleFavorite = { challengeId -> repository.toggleFavorite(challengeId) },
                            fontScale = fontScale,
                            onFontScaleChange = { newScale -> fontScale = newScale },
                            userPoints = currentUser.points,
                            userCoins = currentUser.virtualCoins,
                            unlockedChallengeIds = currentUser.unlockedChallengeIds,
                            onNavigateToSubscription = { showSubscriptionScreen = true },
                            onDeductCoins = { id -> repository.deductCoinsForChallenge(id) }
                        )
                    }
                    AppNavDestination.SETTINGS -> {
                        SettingsScreen(
                            user = currentUser,
                            soundSettings = soundSettings,
                            onUpdateSoundSettings = { settings -> repository.updateSoundSettings(settings) },
                            fontScale = fontScale,
                            onFontScaleChange = { newScale -> fontScale = newScale }
                        )
                    }
                    AppNavDestination.PROFILE -> {
                        ProfileScreen(
                            user = currentUser,
                            friendsList = friendsList,
                            friendRequestsList = friendRequestsList,
                            transactionsList = transactionsList,
                            onSendFriendRequest = { tagId -> repository.sendFriendRequest(tagId) },
                            onAcceptFriendRequest = { reqId -> repository.acceptFriendRequest(reqId) },
                            onRejectFriendRequest = { reqId -> repository.rejectFriendRequest(reqId) },
                            onChallengeFriend = { tagId -> repository.challengeFriend(tagId) },
                            onSendGiftCoins = { tagId -> repository.sendGiftCoins(tagId) },
                            onRemoveFriend = { tagId -> repository.removeFriend(tagId) },
                            onUpdateProfile = { name, avatar, club ->
                                repository.updateUserProfile(name, avatar, club)
                            },
                            onExchangePointsForCoins = { pts, coins -> repository.exchangePointsForCoins(pts, coins) },
                            fontScale = fontScale,
                            onFontScaleChange = { newScale -> fontScale = newScale },
                            onSignOut = {
                                signOut(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onSignOutComplete = {
                                        firebaseUser = null
                                        isGuest = false
                                    },
                                    scope = coroutineScope
                                )
                            },
                            onDeleteAccount = {
                                repository.deleteAccount {
                                    firebaseUser = null
                                    isGuest = false
                                }
                            },
                            onNavigateToChallenges = { currentDestination = AppNavDestination.CHALLENGES },
                            onNavigateToAdminRecharge = {
                                if (currentUser.isAdmin) {
                                    showAdminRecharge = true
                                }
                            },
                            onNavigateToSubscription = { showSubscriptionScreen = true }
                        )
                    }
                }
            }
        }
    }
}

fun signOut(
    context: Context,
    credentialManager: CredentialManager,
    onSignOutComplete: () -> Unit,
    scope: CoroutineScope
) {
    Firebase.auth.signOut()
    scope.launch {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("Auth", "Failed to clear credential state", e)
        } finally {
            onSignOutComplete()
        }
    }
}

private const val TAG = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}
