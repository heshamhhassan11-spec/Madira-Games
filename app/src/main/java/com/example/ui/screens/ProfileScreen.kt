package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FOOTBALL_TIERS
import com.example.data.model.FootballRankTier
import com.example.data.model.FriendRequestItem
import com.example.data.model.FriendUser
import com.example.data.model.SoundSettings
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkAppBarColor
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserProfile,
    friendsList: List<FriendUser> = emptyList(),
    friendRequestsList: List<FriendRequestItem> = emptyList(),
    transactionsList: List<TransactionRecord> = emptyList(),
    onSendFriendRequest: (String) -> String = { "تم إرسال الطلب بنجاح!" },
    onAcceptFriendRequest: (String) -> Unit = {},
    onRejectFriendRequest: (String) -> Unit = {},
    onChallengeFriend: (String) -> String = { _ -> "فزت في التحدي! +30 نقطة 🏆" },
    onSendGiftCoins: (String) -> String = { _ -> "تم إرسال هدية 10 عملات بنجاح 🎁" },
    onRemoveFriend: (String) -> Unit = {},
    soundSettings: SoundSettings = SoundSettings(),
    onUpdateSoundSettings: (SoundSettings) -> Unit = {},
    onUpdateProfile: (name: String, avatar: String, club: String) -> Unit = { _, _, _ -> },
    onExchangePointsForCoins: (requiredPoints: Int, grantedCoins: Double) -> Boolean = { _, _ -> true },
    fontScale: Float = 1.0f,
    onFontScaleChange: (Float) -> Unit = {},
    onSignOut: () -> Unit = {},
    onNavigateToChallenges: () -> Unit = {},
    onNavigateToAdminRecharge: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    val progress = user.levelProgress
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showExchangeStoreDialog by remember { mutableStateOf(false) }
    var showGameGuideDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    if (showGameGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGameGuideDialog = false },
            title = {
                Text("📖 إرشادات وقواعد اللعبة وطريقة الفوز", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = (16 * fontScale).sp)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "مرحباً بك في Madira Games! إليك دليل اللعب الشامل:\n\n" +
                                "1️⃣ التحديات الأربعة الكروية:\n" +
                                "• خمن اسم اللاعب: تعرف على الأساطير من خلال الجنسية، الأندية، المركز، والأرقام.\n" +
                                "• خمن شعار أو اسم النادي: تعرف على شعارات وألقاب أشهر الأندية العربية والعالمية.\n" +
                                "• كأس العالم: الإجابة عن تاريخ المونديال والنهائيات الخالدة.\n" +
                                "• كوّن فريق (ساقد بولدر): اختر تشكيلتك بميزانية 200 مليون وحاكي بطولتك!\n\n" +
                                "2️⃣ النقاط والعملات (Coins & Points):\n" +
                                "• تكسب النقاط مقابل الإجابات الصحيحة ورفع مستواك في الرتب الكروية.\n" +
                                "• يمكنك استبدال النقاط بعملات (Coins) من متجر الاستبدال لشحن رصيدك ودخول التحديات.\n\n" +
                                "3️⃣ الأصدقاء والتحديات المباشرة:\n" +
                                "• أضف أصدقائك عبر الرقم التعريفي (ID) مثل (MAD-XXXXX أو Guest أو Madira).\n" +
                                "• نافسهم في تحديات أونلاين مباشرة وأرسل لهم الهدايا والكوينز.",
                        color = TextPrimary,
                        fontSize = (12 * fontScale).sp,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGameGuideDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("فهمت، لنبدأ اللعب! ⚽", color = Color(0xFF101018), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCardSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text("⚠️ تحذير: حذف الحساب نهائياً", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = (16 * fontScale).sp)
            },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف حسابك وجميع بياناتك؟ سيتم محو نقاطك، عملاتك، سجلك، وقائمة أصدقائك نهائياً من الخادم ولا يمكن التراجع عن هذا الإجراء.",
                    color = TextPrimary,
                    fontSize = (13 * fontScale).sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("نعم، احذف الحساب نهائياً", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteAccountDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Text("إلغاء")
                }
            },
            containerColor = DarkCardSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Smooth animated progress bar values
    val animatedLevelProgress by animateFloatAsState(
        targetValue = progress.levelProgressRatio,
        animationSpec = tween(durationMillis = 800),
        label = "levelProgressAnim"
    )

    val animatedTierProgress by animateFloatAsState(
        targetValue = progress.tierProgressRatio,
        animationSpec = tween(durationMillis = 800),
        label = "tierProgressAnim"
    )

    if (showEditProfileDialog) {
        EditProfileModalDialog(
            currentName = user.name,
            currentAvatar = user.avatarEmoji,
            currentClub = user.favoriteClub,
            fontScale = fontScale,
            onDismiss = { showEditProfileDialog = false },
            onSave = { newName, newAvatar, newClub ->
                onUpdateProfile(newName, newAvatar, newClub)
                showEditProfileDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkAppBackground)
            .testTag("profile_screen")
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_madira_logo),
                            contentDescription = "Madira Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "الملف الشخصي والإعدادات",
                            color = TextPrimary,
                            fontSize = (17 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Madira Games ⚽",
                            color = GoldPrimary,
                            fontSize = (11 * fontScale).sp
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkAppBarColor)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Avatar & Badges Header with Edit Action
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF242436))
                    .border(2.5.dp, GoldPrimary, CircleShape)
                    .clickable { showEditProfileDialog = true }
                    .testTag("btn_avatar_edit"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = user.avatarEmoji, fontSize = 42.sp)

                // Edit badge icon overlay
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل الصورة",
                        tint = Color(0xFF101018),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    color = TextPrimary,
                    fontSize = (20 * fontScale).sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_edit_profile_pen")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل الاسم",
                        tint = GoldPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Text(
                text = user.email,
                color = TextSecondary,
                fontSize = (12.5 * fontScale).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // User Tag ID Badge for recharge & friend requests
            var idCopiedAlert by remember { mutableStateOf(false) }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF242436),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { idCopiedAlert = true }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "الرقم التعريفي: ${user.formattedTagId}",
                        color = GoldPrimary,
                        fontSize = (13 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (idCopiedAlert) "تم النسخ! ✅" else "📋 نسخ",
                        color = NeonGreen,
                        fontSize = (11 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1E2C),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                ) {
                    Text(
                        text = "مشجع: ${user.favoriteClub} ⚽",
                        color = GoldPrimary,
                        fontSize = (11.5 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                    modifier = Modifier.clickable { showEditProfileDialog = true }
                ) {
                    Text(
                        text = "تعديل البيانات ✏️",
                        color = NeonGreen,
                        fontSize = (11.5 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // MAIN LEVELING & PROGRESS BARS SYSTEM CARD
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_level_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header of Level Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GoldPrimary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "المستوى ${progress.level}",
                                        color = GoldPrimary,
                                        fontSize = (14 * fontScale).sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(progress.currentTier.badgeColorHex).copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(progress.currentTier.badgeColorHex))
                            ) {
                                Text(
                                    text = "${progress.currentTier.badgeIconEmoji} ${progress.currentTier.titleAr}",
                                    color = Color(progress.currentTier.badgeColorHex),
                                    fontSize = (12.5 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Total Points Badge
                        Text(
                            text = "${user.points} نقطة",
                            color = TextPrimary,
                            fontSize = (13.5 * fontScale).sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ------------------------------------------------------------------
                    // Progress Bar 1: التقدم نحو المستوى التالي (Next Level Progress Bar)
                    // ------------------------------------------------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "التقدم نحو المستوى ${progress.level + 1}",
                            color = TextPrimary,
                            fontSize = (13 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${progress.currentPoints} / ${progress.levelNextPoints} ن (${(progress.levelProgressRatio * 100).toInt()}%)",
                            color = GoldPrimary,
                            fontSize = (12 * fontScale).sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { animatedLevelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .testTag("level_progress_bar"),
                        color = GoldPrimary,
                        trackColor = Color(0xFF1E1E2C),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🎯 متبقي ${progress.pointsToNextLevel} نقطة للصعود للمستوى التالي",
                            color = TextSecondary,
                            fontSize = (11.5 * fontScale).sp
                        )
                        Text(
                            text = "+250 ن/مستوى",
                            color = TextMuted,
                            fontSize = (10.5 * fontScale).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ------------------------------------------------------------------
                    // Progress Bar 2: التقدم نحو الرتبة الكروية القادمة (Next Football Tier)
                    // ------------------------------------------------------------------
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (progress.nextTier != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "الرتبة القادمة: ",
                                    color = TextPrimary,
                                    fontSize = (13 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${progress.nextTier.badgeIconEmoji} ${progress.nextTier.titleAr}",
                                    color = Color(progress.nextTier.badgeColorHex),
                                    fontSize = (13 * fontScale).sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Text(
                                text = "${(progress.tierProgressRatio * 100).toInt()}%",
                                color = Color(progress.nextTier.badgeColorHex),
                                fontSize = (12 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { animatedTierProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .testTag("tier_progress_bar"),
                            color = Color(progress.nextTier.badgeColorHex),
                            trackColor = Color(0xFF1E1E2C),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "🏆 متبقي ${progress.pointsToNextTier} نقطة خبرة لفتح رتبة \"${progress.nextTier.titleAr}\"",
                            color = TextSecondary,
                            fontSize = (11.5 * fontScale).sp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "أنت في أعلى رتبة كروية (الكرة الذهبية) 👑",
                                color = GoldPrimary,
                                fontSize = (12.5 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Tier Perk Note
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E1E2C)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎁", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ميزة رتبتك الحالية (${progress.currentTier.titleAr}):",
                                    color = GoldPrimary,
                                    fontSize = (11 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = progress.currentTier.perkDescriptionAr,
                                    color = TextSecondary,
                                    fontSize = (11 * fontScale).sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            var friendSearchInput by remember { mutableStateOf("") }
            var searchFeedbackMessage by remember { mutableStateOf<String?>(null) }
            var friendInteractionDialogMessage by remember { mutableStateOf<String?>(null) }

            if (friendInteractionDialogMessage != null) {
                AlertDialog(
                    onDismissRequest = { friendInteractionDialogMessage = null },
                    containerColor = Color(0xFF1E1E2C),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚔️ التحدي الكروي الودي", color = GoldPrimary, fontSize = (16 * fontScale).sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Text(
                            text = friendInteractionDialogMessage!!,
                            color = TextPrimary,
                            fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { friendInteractionDialogMessage = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("حسناً ⚽", color = Color(0xFF101018), fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // 🌟 EMOJI ICONS GALLERY CARD (معرض الأيقونات والرموز التعبيرية)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_emoji_icons_gallery"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🌟 أيقونات الرموز التعبيرية (Emoji Badges)",
                                color = GoldPrimary,
                                fontSize = (15 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "الحالية: ${user.avatarEmoji}",
                                color = GoldPrimary,
                                fontSize = (11 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اضغط على أي رمز تعبيري لتعيينه فوراً كأيقونة حسابك وشارتك في اللعبة:",
                        color = TextSecondary,
                        fontSize = (11.5 * fontScale).sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val quickEmojis = listOf(
                        "⚽", "👑", "🏆", "🥇", "🦁", "⚡", "🦅", "🧤",
                        "🎯", "🚀", "🌟", "🛡️", "🪄", "🔥", "💎", "🌪️",
                        "👟", "🥅", "📣", "⏱️", "🎽", "⚔️", "🥈", "🥉"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        quickEmojis.take(8).forEach { emoji ->
                            val isSelected = user.avatarEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.35f) else Color(0xFF242436))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onUpdateProfile(user.name, emoji, user.favoriteClub)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        quickEmojis.drop(8).take(8).forEach { emoji ->
                            val isSelected = user.avatarEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.35f) else Color(0xFF242436))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onUpdateProfile(user.name, emoji, user.favoriteClub)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        quickEmojis.drop(16).take(8).forEach { emoji ->
                            val isSelected = user.avatarEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.35f) else Color(0xFF242436))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onUpdateProfile(user.name, emoji, user.favoriteClub)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 🤝 Friends & Friend Requests Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_friends_section"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👥 الأصدقاء والتحديات المباشرة", color = TextPrimary, fontSize = (15.5 * fontScale).sp, fontWeight = FontWeight.Bold)
                            if (friendRequestsList.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = CircleShape, color = Color(0xFFFF3D00)) {
                                    Text("${friendRequestsList.size}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "معرفك: ${user.formattedTagId}",
                                color = NeonGreen,
                                fontSize = (10.5 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Suggested community player quick chips
                    Text(
                        text = "لاعبون مميزون ومسؤولون متاحون للإضافة السريعة:",
                        color = TextSecondary,
                        fontSize = (11 * fontScale).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val suggestedChips = listOf(
                        Pair("Madira", "مدير Madira 👑"),
                        Pair("MAD-48192", "كابتن طارق ⚡"),
                        Pair("MAD-77341", "سلطان الهدافين 🎯"),
                        Pair("MAD-39105", "عمر المدريدي 🔥"),
                        Pair("MAD-62184", "ياسين الكتلوني 🪄")
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(suggestedChips) { (tag, label) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF242436),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable {
                                    friendSearchInput = tag
                                    searchFeedbackMessage = onSendFriendRequest(tag)
                                }
                            ) {
                                Text(
                                    text = "$label ($tag)",
                                    color = GoldPrimary,
                                    fontSize = (11 * fontScale).sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add Friend by Tag ID Search Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = friendSearchInput,
                            onValueChange = { friendSearchInput = it },
                            placeholder = { Text("أدخل الرقم التعريفي (مثال: MAD-48192 أو Guest أو Madira)", color = TextMuted, fontSize = 11.5.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (friendSearchInput.isNotBlank()) {
                                    searchFeedbackMessage = onSendFriendRequest(friendSearchInput)
                                    friendSearchInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إضافة ➕", color = Color(0xFF1E1E2C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (searchFeedbackMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(searchFeedbackMessage!!, color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Friend Requests list if any
                    if (friendRequestsList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("📩 طلبات الصداقة المعلقة:", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        friendRequestsList.forEach { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(req.senderAvatar, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(req.senderName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("الرقم التعريفي: #${req.senderTagId} • ${req.timestamp}", color = TextSecondary, fontSize = 11.sp)
                                        }
                                    }
                                    Row {
                                        Button(
                                            onClick = { onAcceptFriendRequest(req.requestId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("قبول 🟢", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        OutlinedButton(
                                            onClick = { onRejectFriendRequest(req.requestId) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("رفض", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("🟢 قائمة الأصدقاء والتحديات (${friendsList.size}):", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (friendsList.isEmpty()) {
                        Text("لا يوجد أصدقاء مضافون حالياً. يمكنك استخدام الرقم التعريفي لأصدقائك (مثل MAD-XXXXX أو Guest أو Madira) لإرسال طلبات صداقة حقيقية والمنافسة معهم!", color = TextMuted, fontSize = 12.sp)
                    } else {
                        friendsList.forEach { friend ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C)),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(friend.avatarEmoji, fontSize = 24.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(friend.name, color = TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(friend.formattedUserTag, color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(friend.statusText, color = if (friend.isOnline) NeonGreen else TextMuted, fontSize = 11.sp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("• ${friend.favoriteClub}", color = TextSecondary, fontSize = 11.sp)
                                                }
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF28283C)
                                        ) {
                                            Text(
                                                text = "${friend.points} ن",
                                                color = GoldPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Interactive Action Buttons: Friendly Challenge & Gift Coins
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                friendInteractionDialogMessage = onChallengeFriend(friend.userTagId)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("⚔️ تحدي كروي", color = Color(0xFF101018), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Button(
                                            onClick = {
                                                val msg = onSendGiftCoins(friend.userTagId)
                                                searchFeedbackMessage = msg
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C40)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("🎁 هدية كوينز", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onRemoveFriend(friend.userTagId) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "حذف الصديق", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // FOOTBALL TIERS ROADMAP & PERKS
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("football_tiers_roadmap"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎖️ مسار الرتب الكروية والجوائز:",
                        color = GoldPrimary,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FOOTBALL_TIERS.forEach { tier ->
                        val isUnlocked = user.points >= tier.minPoints
                        val isCurrent = user.points >= tier.minPoints && user.points <= tier.maxPoints

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isCurrent) GoldPrimary.copy(alpha = 0.15f)
                                    else if (isUnlocked) Color(0xFF1E1E2C)
                                    else Color(0xFF1A1A24)
                                )
                                .border(
                                    1.dp,
                                    if (isCurrent) GoldPrimary else DarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = tier.badgeIconEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = tier.titleAr,
                                            color = if (isUnlocked) TextPrimary else TextMuted,
                                            fontSize = (13 * fontScale).sp,
                                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GoldPrimary
                                            ) {
                                                Text(
                                                    text = "رتبتك الحالية",
                                                    color = Color.Black,
                                                    fontSize = (9 * fontScale).sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = tier.perkDescriptionAr,
                                        color = if (isUnlocked) TextSecondary else TextMuted,
                                        fontSize = (11 * fontScale).sp
                                    )
                                }
                            }

                            if (isUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "تم فتح الرتبة",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "مغلق",
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${tier.minPoints} ن",
                                        color = TextMuted,
                                        fontSize = (11 * fontScale).sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick CTA to play challenges and earn points
                    Button(
                        onClick = onNavigateToChallenges,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_earn_points_cta"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF1E1E2C))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "العب التحديات واكسب المزيد من النقاط ⚽",
                                color = Color(0xFF1E1E2C),
                                fontSize = (13 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Stats Grid (Points, Rank, Streak)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 إحصائياتك وسجل الفوز:",
                        color = GoldPrimary,
                        fontSize = (13.5 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatItem(
                            icon = Icons.Default.Star,
                            label = "النقاط الإجمالية",
                            value = "${user.points}",
                            accentColor = GoldPrimary,
                            fontScale = fontScale
                        )

                        ProfileStatItem(
                            icon = Icons.Default.EmojiEvents,
                            label = "الترتيب العام",
                            value = "#${user.rank}",
                            accentColor = NeonGreen,
                            fontScale = fontScale
                        )

                        ProfileStatItem(
                            icon = Icons.Default.LocalFireDepartment,
                            label = "سلسلة الانتصارات",
                            value = "${user.winStreak} مباريات",
                            accentColor = Color(0xFFFF7043),
                            fontScale = fontScale
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Points to Coins Exchange Store Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showExchangeStoreDialog = true }
                    .testTag("card_exchange_store"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonGreen.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🪙", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "متجر استبدال النقاط بـ عملات 🪙",
                                color = TextPrimary,
                                fontSize = (14.5 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "1000ن = 10عملات | 5000ن = 50عملة | 10000ن = 100عملة",
                                color = NeonGreen,
                                fontSize = (11 * fontScale).sp
                            )
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscription & VIP Recharge Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSubscription() }
                    .testTag("card_subscription_recharge"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "شحن كوينز والاشتراكات VIP 🌟",
                                color = TextPrimary,
                                fontSize = (14.5 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "فودافون كاش و إنستاباي (01044581972)",
                                color = TextSecondary,
                                fontSize = (11.5 * fontScale).sp
                            )
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (user.isAdmin) {
                // Admin Recharge Panel Card (Strictly for Admin only)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAdminRecharge() }
                        .testTag("card_admin_recharge"),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "لوحة تحكم الأدمن - طلبات شحن الكوينز 💳",
                                    color = TextPrimary,
                                    fontSize = (14.5 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "إدارة طلبات فودافون كاش وإنستاباي والموافقة عليها (خاص بالإدارة)",
                                    color = TextSecondary,
                                    fontSize = (11.5 * fontScale).sp
                                )
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Madira App Brand & Version 1.2 Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141420)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_madira_logo),
                            contentDescription = "Madira Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Madira Games 🏆",
                                color = GoldPrimary,
                                fontSize = (15 * fontScale).sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "تطبيق وموسوعة الألعاب والمسابقات الكروية الشاملة ⚽",
                            color = TextSecondary,
                            fontSize = (11 * fontScale).sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // 📜 Transaction History Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📜 تاريخ معاملات الشحن", color = TextPrimary, fontSize = (16 * fontScale).sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (transactionsList.isEmpty()) {
                        Text("لا يوجد تاريخ معاملات متاح.", color = TextMuted, fontSize = 12.sp)
                    } else {
                        transactionsList.sortedByDescending { it.timestamp }.forEach { tx ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tx.amount, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    val statusText = when(tx.status) {
                                        "تم التنفيذ" -> "تم التنفيذ ✅"
                                        "تم الرفض" -> "تم الرفض ❌"
                                        else -> "قيد المراجعة ⏳"
                                    }
                                    val statusColor = when(tx.status) {
                                        "تم التنفيذ" -> NeonGreen
                                        "تم الرفض" -> Color.Red
                                        else -> GoldPrimary
                                    }
                                    Text(text = statusText, color = statusColor, fontSize = 11.sp)
                                }
                                Text("تاريخ: ${java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault()).format(java.util.Date(tx.timestamp))}", color = TextMuted, fontSize = 11.sp)
                            }
                            HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val context = androidx.compose.ui.platform.LocalContext.current

            // Support Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("هل تحتاج إلى مساعدة؟ 🛠️", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://wa.me/Madira?s=t")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("دعم واتساب (01228823207) 💬", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Guide & How to Play Button
            Button(
                onClick = { showGameGuideDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_game_guide"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📖 إرشادات وقواعد اللعبة وطريقة الفوز", color = Color(0xFF101018), fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign out button
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_sign_out"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp), tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تسجيل الخروج من الحساب", color = GoldPrimary, fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delete Account & Data Button
            OutlinedButton(
                onClick = { showDeleteAccountDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_delete_account"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFFF5252))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حذف الحساب نهائياً ومسح كافة البيانات 🗑️", fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showExchangeStoreDialog) {
        AlertDialog(
            onDismissRequest = { showExchangeStoreDialog = false },
            containerColor = DarkCardSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙 متجر استبدال النقاط بـ عملات", color = GoldPrimary, fontSize = (16 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "نقاطك الحالية: ${user.points} نقطة  |  عملاتك: ${user.virtualCoins.toInt()} عملة",
                        color = TextSecondary,
                        fontSize = (12 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val options = listOf(
                        Triple(1000, 10.0, "1000 نقطة ➔ 10 عملات 🪙"),
                        Triple(5000, 50.0, "5000 نقطة ➔ 50 عملة 🪙"),
                        Triple(10000, 100.0, "10000 نقطة ➔ 100 عملة 🪙")
                    )

                    options.forEach { (reqPts, grantedCoins, label) ->
                        val canAfford = user.points >= reqPts
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = canAfford) {
                                    val success = onExchangePointsForCoins(reqPts, grantedCoins)
                                    if (success) {
                                        showExchangeStoreDialog = false
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (canAfford) Color(0xFF1E1E2C) else Color(0xFF14141E)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (canAfford) NeonGreen else DarkCardBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(label, color = if (canAfford) TextPrimary else TextMuted, fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                                    Text("احصل على ${grantedCoins.toInt()} عملة فوراً", color = if (canAfford) NeonGreen else TextMuted, fontSize = (11 * fontScale).sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (canAfford) NeonGreen else Color.Gray.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = if (canAfford) "استبدال ⚡" else "غير كافٍ",
                                        color = if (canAfford) Color.Black else Color.White,
                                        fontSize = (11 * fontScale).sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExchangeStoreDialog = false }) {
                    Text("إغلاق", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun EditProfileModalDialog(
    currentName: String,
    currentAvatar: String,
    currentClub: String,
    fontScale: Float,
    onDismiss: () -> Unit,
    onSave: (newName: String, newAvatar: String, newClub: String) -> Unit
) {
    var nameState by remember { mutableStateOf(currentName) }
    var avatarState by remember { mutableStateOf(currentAvatar) }
    var clubState by remember { mutableStateOf(currentClub) }

    val avatarChoices = listOf(
        "⚽", "👑", "🏆", "🥇", "🦁", "⚡",
        "🦅", "🧤", "🎯", "🚀", "🌟", "🛡️",
        "🪄", "🔥", "💎", "🌪️", "👟", "🥅",
        "📣", "⏱️", "🎽", "⚔️", "🥈", "🥉",
        "🇪🇬", "🇸🇦", "🇩🇿", "🇲🇦", "🇮🇶", "🇹🇳",
        "🇯🇴", "🇰🇼", "🇶🇦", "🇦🇪", "🇧🇭", "🇵🇸"
    )

    val popularClubs = listOf(
        "الأهلي المصري", "الزمالك", "الهلال السعودي", "النصر السعودي",
        "الاتحاد السعودي", "ريال مدريد", "برشلونة", "ليفربول",
        "مانشستر سيتي", "بايرن ميونخ", "باريس سان جيرمان", "إنتر ميلان"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A28),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تعديل الاسم والصورة ✏️",
                    color = TextPrimary,
                    fontSize = (17 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "اختر أيقونة/رمز تعبيري للاعب (Emoji Badge):",
                    color = GoldPrimary,
                    fontSize = (12.5 * fontScale).sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar Choice Grid with all emojis
                avatarChoices.chunked(6).forEach { rowEmojis ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowEmojis.forEach { emoji ->
                            val isSelected = avatarState == emoji
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.35f) else Color(0xFF242436))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { avatarState = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "اسم المستخدم الجديد:",
                    color = GoldPrimary,
                    fontSize = (12.5 * fontScale).sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = nameState,
                    onValueChange = { nameState = it },
                    singleLine = true,
                    placeholder = { Text("أدخل اسمك الكروي...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_username"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "النادي المفضل:",
                    color = GoldPrimary,
                    fontSize = (12.5 * fontScale).sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(popularClubs) { club ->
                        val isSelected = clubState == club
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldPrimary else Color(0xFF242436),
                            modifier = Modifier.clickable { clubState = club }
                        ) {
                            Text(
                                text = club,
                                color = if (isSelected) Color(0xFF101018) else TextPrimary,
                                fontSize = (11.5 * fontScale).sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameState.isNotBlank()) {
                        onSave(nameState.trim(), avatarState, clubState)
                    }
                },
                enabled = nameState.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_save_profile_changes")
            ) {
                Text(
                    text = "حفظ التغييرات",
                    color = Color(0xFF101018),
                    fontWeight = FontWeight.Bold,
                    fontSize = (13 * fontScale).sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "إلغاء", fontSize = (12 * fontScale).sp)
            }
        }
    )
}

@Composable
fun ProfileStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    fontScale: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, color = TextPrimary, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextSecondary, fontSize = (10.5 * fontScale).sp)
    }
}

