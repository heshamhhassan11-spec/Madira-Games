package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SoundSettings
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: UserProfile,
    soundSettings: SoundSettings,
    onUpdateSoundSettings: (SoundSettings) -> Unit,
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkAppBackground)
            .testTag("settings_screen")
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إعدادات التطبيق والخصائص",
                        color = TextPrimary,
                        fontSize = (18 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkAppBarColor)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Sound & Audio Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sound_settings_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "إعدادات الصوت والمؤثرات الميدانية 🔊",
                                    color = TextPrimary,
                                    fontSize = (14 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "صافرة الحكم، تفاعل الجماهير، والاهتزاز",
                                    color = TextSecondary,
                                    fontSize = (11 * fontScale).sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sound Effects Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "المؤثرات الصوتية عند الإجابة 🔔",
                                color = TextPrimary,
                                fontSize = (12.5 * fontScale).sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "صوت الصافرة والتصفيق عند الإجابة الصحيحة أو الخاطئة",
                                color = TextSecondary,
                                fontSize = (10.5 * fontScale).sp
                            )
                        }
                        Switch(
                            checked = soundSettings.soundEffectsEnabled,
                            onCheckedChange = { checked ->
                                onUpdateSoundSettings(soundSettings.copy(soundEffectsEnabled = checked))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF101018),
                                checkedTrackColor = NeonGreen,
                                uncheckedTrackColor = Color(0xFF242436)
                            ),
                            modifier = Modifier.testTag("switch_sound_effects")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stadium Crowd Ambience Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "أصوات وهتافات الملاعب 🏟️",
                                color = TextPrimary,
                                fontSize = (12.5 * fontScale).sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "أجواء حماسية تحاكي مباريات الديربي والنهائيات الكبرى",
                                color = TextSecondary,
                                fontSize = (10.5 * fontScale).sp
                            )
                        }
                        Switch(
                            checked = soundSettings.stadiumAmbienceEnabled,
                            onCheckedChange = { checked ->
                                onUpdateSoundSettings(soundSettings.copy(stadiumAmbienceEnabled = checked))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF101018),
                                checkedTrackColor = GoldPrimary,
                                uncheckedTrackColor = Color(0xFF242436)
                            ),
                            modifier = Modifier.testTag("switch_stadium_ambience")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Haptic Feedback Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الاهتزاز اللمسي التفاعلي 📳",
                                color = TextPrimary,
                                fontSize = (12.5 * fontScale).sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "نبضات لمسية عند اختيار الإجابات وتخطي المستويات",
                                color = TextSecondary,
                                fontSize = (10.5 * fontScale).sp
                            )
                        }
                        Switch(
                            checked = soundSettings.hapticFeedbackEnabled,
                            onCheckedChange = { checked ->
                                onUpdateSoundSettings(soundSettings.copy(hapticFeedbackEnabled = checked))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF101018),
                                checkedTrackColor = Color(0xFF00D2D3),
                                uncheckedTrackColor = Color(0xFF242436)
                            ),
                            modifier = Modifier.testTag("switch_haptic_feedback")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkCardBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "اختر الثيم الصوتي الحماسي (5 ثيمات مخصصة) 🔥:",
                        color = TextPrimary,
                        fontSize = (13 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val soundThemes = listOf(
                        Triple("stadium_roar", "هتافات الملاعب والصافرة", "🏟️"),
                        Triple("samba_drums", "إيقاع أطبال السامبا", "🥁"),
                        Triple("champions_anthem", "حماس الأبطال وقرعة الكأس", "🏆"),
                        Triple("commentator_hype", "المعلق الحماسي والصافرة", "🎙️"),
                        Triple("cyber_stadium", "المؤثرات الإلكترونية الحماسية", "⚡")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        soundThemes.forEach { (themeKey, themeLabel, themeIcon) ->
                            val isSelected = soundSettings.selectedSoundTheme == themeKey
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else Color(0xFF1E1E2C),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) GoldPrimary else DarkCardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onUpdateSoundSettings(soundSettings.copy(selectedSoundTheme = themeKey))
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = themeIcon, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = themeLabel,
                                            color = if (isSelected) GoldPrimary else TextPrimary,
                                            fontSize = (12.5 * fontScale).sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onUpdateSoundSettings(soundSettings.copy(selectedSoundTheme = themeKey))
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = GoldPrimary,
                                            unselectedColor = TextMuted
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Eye Comfort Font Size Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "التحكم في حجم الخط المريح للعين:",
                            color = TextPrimary,
                            fontSize = (13.5 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "المستوى الحالي: ${if (fontScale > 1.2f) "كبير جداً (1.3x)" else if (fontScale > 1.05f) "كبير (1.15x)" else "عادي (1.0x)"}",
                        color = TextSecondary,
                        fontSize = (12 * fontScale).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = fontScale,
                        onValueChange = { onFontScaleChange(it) },
                        valueRange = 1.0f..1.3f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = DarkCardBorder
                        )
                    )
                }
            }



            // 4. Direct WhatsApp Support Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_whatsapp_support"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("الدعم الفني المباشر 🛠️", color = TextPrimary, fontSize = (15 * fontScale).sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://wa.me/Madira?s=t")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("تواصل معنا عبر واتساب 💬", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = (13.5 * fontScale).sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
