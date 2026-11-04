package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SquadPlayer
import com.example.data.model.TournamentSimulationChoice
import com.example.data.model.TournamentSimulationReport
import com.example.ui.theme.*

data class FormationSlot(
    val id: String,
    val titleAr: String,
    val shortPos: String, // GK, DEF, MID, FWD
    val order: Int
)

val FORMATION_11_SLOTS = listOf(
    FormationSlot("slot_gk", "حارس مرمى", "GK", 1),
    FormationSlot("slot_lb", "ظهير أيسر", "DEF", 2),
    FormationSlot("slot_cb1", "قلب دفاع 1", "DEF", 3),
    FormationSlot("slot_cb2", "قلب دفاع 2", "DEF", 4),
    FormationSlot("slot_rb", "ظهير أيمن", "DEF", 5),
    FormationSlot("slot_cm1", "وسط ميدان 1", "MID", 6),
    FormationSlot("slot_cm2", "وسط ميدان 2", "MID", 7),
    FormationSlot("slot_cam", "صانع ألعاب", "MID", 8),
    FormationSlot("slot_lw", "جناح أيسر", "FWD", 9),
    FormationSlot("slot_st", "رأس حربة", "FWD", 10),
    FormationSlot("slot_rw", "جناح أيمن", "FWD", 11)
)

val AVAILABLE_10_LEAGUES = listOf(
    "الكل",
    "الدوري الإنجليزي",
    "الدوري الإسباني",
    "الدوري الإيطالي",
    "الدوري الألماني",
    "الدوري الفرنسي",
    "الدوري السعودي",
    "الدوري المصري",
    "الدوري الأمريكي",
    "الدوري التركي",
    "الدوري البرتغالي"
)

fun getMaxBudgetForTournament(tournament: TournamentSimulationChoice?): Double {
    if (tournament == null) return 200.0
    val id = tournament.id.lowercase()
    val name = tournament.nameAr
    return when {
        id.contains("ucl") || id.contains("epl") || id.contains("laliga") || id.contains("serie_a") || id.contains("bundesliga") || id.contains("ligue1") ||
        name.contains("أوروبا") || name.contains("الإنجليزي") || name.contains("الإسباني") || name.contains("الإيطالي") || name.contains("الألماني") || name.contains("الفرنسي") -> 200.0

        id.contains("caf") || id.contains("saudi") || id.contains("portuguese") ||
        name.contains("إفريقيا") || name.contains("السعودي") || name.contains("البرتغالي") -> 150.0

        else -> 100.0 // Egyptian, Turkish, MLS
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadBuilderScreen(
    playersPool: List<SquadPlayer>,
    tournamentChoices: List<TournamentSimulationChoice>,
    onSimulate: (TournamentSimulationChoice, List<SquadPlayer>) -> TournamentSimulationReport,
    onBack: () -> Unit,
    onDeductCoins: () -> Boolean = { true },
    fontScale: Float = 1.0f
) {
    BackHandler { onBack() }

    val selectedSquad = remember { mutableStateMapOf<String, SquadPlayer>() }

    var selectedTournament by remember { mutableStateOf(tournamentChoices.firstOrNull()) }
    var insufficientCoinsError by remember { mutableStateOf(false) }
    var activePickingSlot by remember { mutableStateOf<FormationSlot?>(null) }
    var viewingPlayerStats by remember { mutableStateOf<SquadPlayer?>(null) }
    var selectedLeagueFilter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    var showTournamentPicker by remember { mutableStateOf(false) }
    var activeSimulationReport by remember { mutableStateOf<TournamentSimulationReport?>(null) }

    val totalBudget = getMaxBudgetForTournament(selectedTournament)
    val spentBudget = selectedSquad.values.sumOf { it.priceMillions }
    val remainingBudget = (totalBudget - spentBudget).coerceAtLeast(0.0)
    val isBudgetExceeded = spentBudget > totalBudget
    val isSquadComplete = selectedSquad.size == 11 && !isBudgetExceeded

    // Detailed Player Stats Bottom Sheet
    if (viewingPlayerStats != null) {
        val player = viewingPlayerStats!!
        ModalBottomSheet(
            onDismissRequest = { viewingPlayerStats = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DarkAppBarColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.2f))
                        .border(2.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (player.position) {
                            "GK" -> "🧤"
                            "DEF" -> "🛡️"
                            "MID" -> "⚙️"
                            else -> "⚡"
                        },
                        fontSize = 34.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = player.name,
                    color = TextPrimary,
                    fontSize = (20 * fontScale).sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "${player.club} • ${player.league} (${player.nationality})",
                    color = GoldPrimary,
                    fontSize = (13 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📊 بطاقة الإحصائيات الفنية والمالية:",
                            color = GoldPrimary,
                            fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PlayerStatItem("المركز", player.position, NeonGreen, fontScale)
                            PlayerStatItem("التقييم الإجمالي", "${player.rating}", GoldPrimary, fontScale)
                            PlayerStatItem("القيمة السوقية", "${player.priceMillions}M €", Color(0xFF64B5F6), fontScale)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DarkCardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PlayerStatItem("الجنسية", player.nationality, TextPrimary, fontScale)
                            PlayerStatItem("الدوري", player.league, TextPrimary, fontScale)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewingPlayerStats = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("إغلاق الإحصائيات", color = Color.Black, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (activeSimulationReport != null) {
        val report = activeSimulationReport!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkAppBackground)
                .testTag("simulation_report_screen")
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "تقرير محاكاة الذكاء الاصطناعي 🤖",
                        color = TextPrimary,
                        fontSize = (17 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activeSimulationReport = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkAppBarColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (report.isChampion) GoldPrimary.copy(alpha = 0.2f) else Color(0xFF2C2418)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (report.isChampion) "🏆" else "🏅", fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = report.tournamentName,
                    color = GoldPrimary,
                    fontSize = (20 * fontScale).sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = report.finalPlacement,
                    color = if (report.isChampion) NeonGreen else TextSecondary,
                    fontSize = (16 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Team Ratings Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBadge("الهجوم", "${report.teamAttackRating}", GoldPrimary, fontScale)
                        StatBadge("الوسط", "${report.teamMidfieldRating}", NeonGreen, fontScale)
                        StatBadge("الدفاع", "${report.teamDefenseRating}", Color(0xFF64B5F6), fontScale)
                        StatBadge("التناغم", "${report.teamOverallChemistry}%", Color(0xFFBA68C8), fontScale)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Matches Breakdown List (From Group Stage to Finals)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚽ نتائج مباريات المحاكاة (من المجموعات إلى النهائيات):",
                            color = GoldPrimary,
                            fontSize = (14.5 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        report.matches.forEachIndexed { idx, match ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (match.isWin) Color(0xFF1B2E23) else Color(0xFF2E1B1B)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (match.isWin) NeonGreen.copy(alpha = 0.5f) else Color(0xFFFF5252).copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = match.stageName,
                                            color = GoldPrimary,
                                            fontSize = (12.5 * fontScale).sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (match.isWin) NeonGreen else Color(0xFFFF5252)
                                        ) {
                                            Text(
                                                text = if (match.isWin) "فوز 🟢" else "خسارة 🔴",
                                                color = Color.Black,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("فريقك 🛡️", color = TextPrimary, fontSize = (13.5 * fontScale).sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${match.userGoals} - ${match.opponentGoals}",
                                            color = GoldPrimary,
                                            fontSize = (16 * fontScale).sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text("${match.opponentLogoSymbol} ${match.opponentName}", color = TextSecondary, fontSize = (13 * fontScale).sp)
                                    }

                                    if (match.userScorers.isNotEmpty() || match.opponentScorers.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "مسجلو الأهداف: " + (match.userScorers + match.opponentScorers).joinToString(" • "),
                                            color = TextMuted,
                                            fontSize = (11 * fontScale).sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tactical AI Verdict & Awarded Points
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("التقرير الفني للذكاء الاصطناعي:", color = GoldPrimary, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = report.tacticalAiVerdict,
                            color = TextPrimary,
                            fontSize = (13 * fontScale).sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, NeonGreen)
                        ) {
                            Text(
                                text = "🎉 تمت إضافة +${report.xpEarned} نقطة لرصيدك العام بنجاح!",
                                color = NeonGreen,
                                fontSize = (13 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Direct Return to Home Screen Button
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_go_home_direct"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "التحويل إلى الشاشة الرئيسية مباشرةً 🏠",
                            color = Color.Black,
                            fontSize = (15 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { showTournamentPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF1E1E2C))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("محاكاة في بطولة أخرى 🏆", color = Color(0xFF1E1E2C), fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { activeSimulationReport = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    border = BorderStroke(1.dp, GoldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("العودة لتعديل التشكيلة ⚽", color = GoldPrimary, fontSize = (13.5 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkAppBackground)
                .testTag("squad_builder_screen")
        ) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "التحدي الرابع: كوّن فريق (11 لاعباً) ⚽",
                            color = TextPrimary,
                            fontSize = (16 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "محاكاة ذكاء اصطناعي واقعية • 10 دوريات عالمية",
                            color = TextSecondary,
                            fontSize = (11 * fontScale).sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = GoldPrimary)
                    }
                },
                actions = {
                    if (selectedSquad.isNotEmpty()) {
                        IconButton(onClick = { selectedSquad.clear() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "إفراغ التشكيلة", tint = Color(0xFFFF5252))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkAppBarColor)
            )

            // Tournament & Budget Selector Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GoldPrimary)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedTournament?.iconEmoji ?: "🏆", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "البطولة المستهدفة: ${selectedTournament?.nameAr ?: "دوري أبطال أوروبا"}",
                                    color = GoldPrimary,
                                    fontSize = (13.5 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "الحد الأقصى للميزانية: ${totalBudget.toInt()}M €",
                                    color = TextSecondary,
                                    fontSize = (11 * fontScale).sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showTournamentPicker = true },
                            modifier = Modifier.height(34.dp),
                            border = BorderStroke(1.dp, GoldPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text("تغيير البطولة 🏆", color = GoldPrimary, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DarkCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اللاعبين: ${selectedSquad.size} / 11",
                            color = if (selectedSquad.size == 11) NeonGreen else TextPrimary,
                            fontSize = (13 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = String.format("المتبقي: %.1fM / %.0fM €", remainingBudget, totalBudget),
                            color = if (isBudgetExceeded) Color(0xFFFF5252) else NeonGreen,
                            fontSize = (13 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // The 11 Formation Slots List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FORMATION_11_SLOTS) { slot ->
                    val assignedPlayer = selectedSquad[slot.id]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activePickingSlot = slot }
                            .testTag("slot_${slot.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (assignedPlayer != null) DarkCardSurface else Color(0xFF1A1A26)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (assignedPlayer != null) GoldPrimary.copy(alpha = 0.7f) else DarkCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (slot.shortPos) {
                                        "GK" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                        "DEF" -> Color(0xFF29B6F6).copy(alpha = 0.2f)
                                        "MID" -> NeonGreen.copy(alpha = 0.2f)
                                        else -> Color(0xFFE53935).copy(alpha = 0.2f)
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = when (slot.shortPos) {
                                                "GK" -> "🧤"
                                                "DEF" -> "🛡️"
                                                "MID" -> "⚙️"
                                                else -> "⚡"
                                            },
                                            fontSize = 18.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "${slot.titleAr} (${slot.shortPos})",
                                        color = TextMuted,
                                        fontSize = (11 * fontScale).sp
                                    )
                                    if (assignedPlayer != null) {
                                        Text(
                                            text = assignedPlayer.name,
                                            color = TextPrimary,
                                            fontSize = (14.5 * fontScale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${assignedPlayer.club} • تقييم ${assignedPlayer.rating}",
                                            color = NeonGreen,
                                            fontSize = (11 * fontScale).sp
                                        )
                                    } else {
                                        Text(
                                            text = "انقر لاختيار لاعب لهذا المركز ➕",
                                            color = GoldPrimary,
                                            fontSize = (13 * fontScale).sp
                                        )
                                    }
                                }
                            }

                            if (assignedPlayer != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewingPlayerStats = assignedPlayer },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = "إحصائيات", tint = GoldPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${assignedPlayer.priceMillions}M €",
                                            color = GoldPrimary,
                                            fontSize = (13.5 * fontScale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { selectedSquad.remove(slot.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "حذف", tint = Color(0xFFFF5252))
                                    }
                                }
                            } else {
                                Icon(Icons.Default.Add, contentDescription = "إضافة", tint = GoldPrimary)
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkAppBarColor,
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val tourney = selectedTournament ?: tournamentChoices.first()
                            if (onDeductCoins()) {
                                activeSimulationReport = onSimulate(tourney, selectedSquad.values.toList())
                            } else {
                                insufficientCoinsError = true
                            }
                        },
                        enabled = isSquadComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_start_ai_simulation"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedSquad.size < 11) "اكتمل ${selectedSquad.size}/11 لاعباً لبدء المحاكاة"
                                else if (isBudgetExceeded) "تجاوزت الميزانية! قلل تكلفة التشكيلة"
                                else "بدء المحاكاة (الدخول بـ 100 عملة) 🤖🏆",
                                color = Color.Black,
                                fontSize = (14 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Player Picker Modal Bottom Sheet
    if (activePickingSlot != null) {
        val slot = activePickingSlot!!
        ModalBottomSheet(
            onDismissRequest = { activePickingSlot = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DarkAppBarColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "اختيار ${slot.titleAr} (${slot.shortPos}) من الـ 10 دوريات",
                    color = TextPrimary,
                    fontSize = (17 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث باسم اللاعب أو النادي...", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E2C),
                        unfocusedContainerColor = Color(0xFF1E1E2C),
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AVAILABLE_10_LEAGUES) { league ->
                        FilterChip(
                            selected = selectedLeagueFilter == league,
                            onClick = { selectedLeagueFilter = league },
                            label = { Text(league, fontSize = (11 * fontScale).sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E1E2C),
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val filteredPool = playersPool.filter { player ->
                    val posMatches = player.position == slot.shortPos
                    val leagueMatches = selectedLeagueFilter == "الكل" || player.league == selectedLeagueFilter
                    val searchMatches = searchQuery.isBlank() || player.name.contains(searchQuery, ignoreCase = true) || player.club.contains(searchQuery, ignoreCase = true)
                    posMatches && leagueMatches && searchMatches
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredPool) { player ->
                        val isAlreadyChosen = selectedSquad.values.any { it.id == player.id }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isAlreadyChosen) {
                                    selectedSquad[slot.id] = player
                                    activePickingSlot = null
                                    searchQuery = ""
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAlreadyChosen) Color(0xFF151520) else DarkCardSurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isAlreadyChosen) DarkCardBorder else GoldPrimary.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldPrimary.copy(alpha = 0.15f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = when (player.position) {
                                                    "GK" -> "🧤"
                                                    "DEF" -> "🛡️"
                                                    "MID" -> "⚙️"
                                                    else -> "⚡"
                                                },
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = player.name,
                                            color = if (isAlreadyChosen) TextMuted else TextPrimary,
                                            fontSize = (14 * fontScale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${player.club} • ${player.league}",
                                            color = TextSecondary,
                                            fontSize = (11 * fontScale).sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewingPlayerStats = player },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = "إحصائيات", tint = GoldPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${player.priceMillions}M €",
                                            color = GoldPrimary,
                                            fontSize = (13.5 * fontScale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "تقييم ${player.rating}",
                                            color = NeonGreen,
                                            fontSize = (11 * fontScale).sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (isAlreadyChosen) {
                                        Text("مختار", color = TextMuted, fontSize = 11.sp)
                                    } else {
                                        Icon(Icons.Default.Add, contentDescription = "اختيار", tint = GoldPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTournamentPicker) {
        ModalBottomSheet(
            onDismissRequest = { showTournamentPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DarkAppBarColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "اختر البطولة لمحاكاة فريقك بالذكاء الاصطناعي 🏆",
                    color = TextPrimary,
                    fontSize = (16.5 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tournamentChoices) { tournament ->
                        val tourneyMaxBudget = getMaxBudgetForTournament(tournament)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTournament = tournament
                                    showTournamentPicker = false
                                }
                                .testTag("tournament_${tournament.id}"),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (selectedTournament?.id == tournament.id) GoldPrimary else DarkCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(tournament.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = tournament.nameAr,
                                            color = TextPrimary,
                                            fontSize = (14.5 * fontScale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "ميزانية أقصاها: ${tourneyMaxBudget.toInt()} مليون €",
                                            color = GoldPrimary,
                                            fontSize = (11.5 * fontScale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = tournament.description,
                                            color = TextSecondary,
                                            fontSize = (11 * fontScale).sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Icon(Icons.Default.PlayArrow, contentDescription = "اختيار البطولة", tint = NeonGreen)
                            }
                        }
                    }
                }
            }
        }
    }

    if (insufficientCoinsError) {
        AlertDialog(
            onDismissRequest = { insufficientCoinsError = false },
            containerColor = DarkCardSurface,
            title = {
                Text("🪙 رصيد العملات غير كافٍ!", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "يتطلب بدء محاكاة التشكيلة وجود 100 عملة. يمكنك استبدال نقاطك بعملات من شاشة الملف الشخصي أو شحن كوينز جديدة!",
                    color = TextPrimary,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { insufficientCoinsError = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("حسناً", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun PlayerStatItem(label: String, value: String, color: Color, fontScale: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextMuted, fontSize = (11.5 * fontScale).sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = color, fontSize = (15 * fontScale).sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatBadge(title: String, value: String, color: Color, fontScale: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextMuted, fontSize = (11 * fontScale).sp)
        Text(value, color = color, fontSize = (18 * fontScale).sp, fontWeight = FontWeight.Black)
    }
}
