package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.BuildConfig
import com.example.R
import com.example.ui.components.SecurityBadgeCard
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkAppBarColor
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: (name: String, email: String, club: String, avatar: String, isNewUser: Boolean) -> Unit,
    onGuestLogin: () -> Unit = {},
    onAdminLogin: (phone: String, pass: String) -> Boolean = { _, _ -> false },
    fontScale: Float = 1.0f
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isGoogleSigningIn by remember { mutableStateOf(false) }
    var showAdminBox by remember { mutableStateOf(false) }
    var adminPhoneInput by remember { mutableStateOf("") }
    var adminPassInput by remember { mutableStateOf("") }
    var adminErrorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { user ->
                onLoginSuccess(
                    user.displayName?.ifBlank { "كابتن كروي" } ?: "كابتن كروي",
                    user.email ?: "",
                    "الأهلي المصري",
                    "⚽",
                    false
                )
            },
            onUnauthenticated = { /* stay on login screen */ },
            scope = coroutineScope
        )
    }

    var showOnboardingWizard by remember { mutableStateOf(false) }
    var pendingUserEmail by remember { mutableStateOf("") }
    var pendingUserName by remember { mutableStateOf("") }

    if (showOnboardingWizard) {
        NewUserOnboardingWizard(
            initialEmail = pendingUserEmail,
            initialName = pendingUserName,
            fontScale = fontScale,
            onCompleteOnboarding = { finalName, finalEmail, chosenClub, chosenAvatar ->
                showOnboardingWizard = false
                onLoginSuccess(finalName, finalEmail, chosenClub, chosenAvatar, true)
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkAppBackground)
                .testTag("auth_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Official Madira Logo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(GoldPrimary.copy(alpha = 0.3f), DarkAppBarColor)
                            )
                        )
                        .border(2.5.dp, GoldPrimary, RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_madira_logo),
                        contentDescription = "شعار تطبيق Madira",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Madira Games 🏆",
                        color = GoldPrimary,
                        fontSize = (28 * fontScale).sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "التطبيق العربي الأول لتحديات كرة القدم وبورصة اللاعبين",
                    color = TextSecondary,
                    fontSize = (14 * fontScale).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // REAL GOOGLE SIGN-IN BUTTON VIA CREDENTIAL MANAGER
                Button(
                    onClick = {
                        isGoogleSigningIn = true
                        onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = { user ->
                                isGoogleSigningIn = false
                                val displayName = user.displayName ?: "كابتن كروي"
                                val email = user.email ?: ""
                                onLoginSuccess(displayName, email, "الأهلي المصري", "⚽", false)
                                Toast.makeText(context, "تم تسجيل الدخول بنجاح! ⚽", Toast.LENGTH_SHORT).show()
                            },
                            onAuthError = { errorMsg ->
                                isGoogleSigningIn = false
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            },
                            scope = coroutineScope,
                            onAuthCancelled = {
                                isGoogleSigningIn = false
                            }
                        )
                    },
                    enabled = !isGoogleSigningIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_google_login"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isGoogleSigningIn) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF1F1F1F),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F3F4),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "G",
                                        color = GoogleRed,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "تسجيل الدخول باستخدام Google",
                                color = Color(0xFF1F1F1F),
                                fontSize = (16 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // GUEST LOGIN BUTTON
                OutlinedButton(
                    onClick = onGuestLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_guest_login"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "الدخول كزائر وتجربة التطبيق ⚡",
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }



                Spacer(modifier = Modifier.height(28.dp))

                // Security badge
                SecurityBadgeCard(fontScale = fontScale)
            }
        }
    }
}

@Composable
fun NewUserOnboardingWizard(
    initialEmail: String,
    initialName: String,
    fontScale: Float,
    onCompleteOnboarding: (name: String, email: String, club: String, avatar: String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Profile & Avatar, 2: Favorite Club, 3: Tactic & Welcome

    var userName by remember { mutableStateOf(initialName.ifBlank { "كابتن كروي" }) }
    var selectedAvatar by remember { mutableStateOf("⚽") }
    var selectedClub by remember { mutableStateOf("الأهلي المصري") }

    val avatars = listOf("⚽", "🧤", "👑", "⚡", "🎯", "🦁", "🏆", "🔥")
    val footballClubs = listOf(
        "الأهلي المصري",
        "الزمالك",
        "ريال مدريد",
        "برشلونة",
        "ليفربول",
        "مانشستر سيتي",
        "بايرن ميونخ",
        "أرسنال",
        "باريس سان جيرمان"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkAppBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (step == 1) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "اختر الأفاتار الخاص بك:",
                        color = TextPrimary,
                        fontSize = (13.5 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        avatars.take(4).forEach { emoji ->
                            val isSelected = selectedAvatar == emoji
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.25f) else Color(0xFF1E1E2C),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GoldPrimary else DarkCardBorder
                                ),
                                modifier = Modifier
                                    .size(52.dp)
                                    .clickable { selectedAvatar = emoji }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(emoji, fontSize = 24.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        avatars.takeLast(4).forEach { emoji ->
                            val isSelected = selectedAvatar == emoji
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.25f) else Color(0xFF1E1E2C),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GoldPrimary else DarkCardBorder
                                ),
                                modifier = Modifier
                                    .size(52.dp)
                                    .clickable { selectedAvatar = emoji }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(emoji, fontSize = 24.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "اسم اللاعب أو اللقب الكروي:",
                        color = TextPrimary,
                        fontSize = (13.5 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        placeholder = { Text("مثال: الكابتن هشام", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("التالي: اختيار النادي المفضل ➔", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (step == 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "اختر ناديك المفضل لتخصيص الأخبار والتحديات:",
                        color = TextPrimary,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        footballClubs.forEach { club ->
                            val isSelected = selectedClub == club
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedClub = club }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else DarkCardBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF28283E) else Color(0xFF1E1E2C)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = if (isSelected) GoldPrimary else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = club,
                                            color = if (isSelected) GoldPrimary else TextPrimary,
                                            fontSize = (13.5 * fontScale).sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("السابق", color = TextSecondary)
                        }

                        Button(
                            onClick = { step = 3 },
                            modifier = Modifier.weight(2f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("التالي: الخطة والتأكيد ➔", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (step == 3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NeonGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "🎁 هدية انضمام اللاعب الجديد",
                            color = NeonGreen,
                            fontSize = (12 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "مرحباً بك يا $userName في الملعب! 🏆",
                        color = Color.White,
                        fontSize = (18 * fontScale).sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onCompleteOnboarding(userName, initialEmail, selectedClub, selectedAvatar)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_complete_onboarding"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "انطلق إلى الملعب وابدأ التحدي ⚽",
                            color = Color.Black,
                            fontSize = (14.5 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: (FirebaseUser) -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onAuthError("Google Sign-In configuration missing: default_web_client_id not found")
        return
    }

    val secureNonce = java.util.UUID.randomUUID().toString()
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId)
        .setNonce(secureNonce)
        .build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    scope.launch {
        try {
            val activity = context as? Activity ?: (context as? android.content.ContextWrapper)?.baseContext as? Activity
            if (activity == null) {
                onAuthError("Activity context not found")
                return@launch
            }
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    onAuthSuccess(user)
                } else {
                    onAuthError("Firebase authentication succeeded but user is null")
                }
            } else {
                onAuthError("Unexpected credential type received")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            onAuthError(e.localizedMessage ?: "Sign in failed")
        }
    }
}

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: (FirebaseUser) -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    val current = Firebase.auth.currentUser
    if (current != null) {
        onAuthSuccess(current)
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onUnauthenticated()
        return
    }

    val secureNonce = java.util.UUID.randomUUID().toString()
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .setNonce(secureNonce)
        .build()

    val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    onAuthSuccess(user)
                } else {
                    onUnauthenticated()
                }
            } else {
                onUnauthenticated()
            }
        } catch (e: Exception) {
            onUnauthenticated()
        }
    }
}
