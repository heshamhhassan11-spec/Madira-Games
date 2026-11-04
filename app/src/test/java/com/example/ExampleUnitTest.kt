package com.example

import com.example.data.model.ChallengeIconType
import com.example.data.repository.AppDataRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun repository_initialChallenges_containsFourStrictOrderedChallenges() {
        val repository = AppDataRepository()
        val challenges = repository.challenges.value

        assertEquals(4, challenges.size)

        // 1. Challenge 1: خمن اسم اللاعب (10, 25, 50, 100)
        val c1 = challenges[0]
        assertEquals("guess_player", c1.id)
        assertEquals(1, c1.orderIndex)
        assertEquals("خمن اسم اللاعب", c1.title)
        assertEquals(listOf(10, 25, 50, 100), c1.allowedQuestionCounts)

        // 2. Challenge 2: خمن شعار او اسم النادي (10, 25, 50, 100)
        val c2 = challenges[1]
        assertEquals("guess_club", c2.id)
        assertEquals(2, c2.orderIndex)
        assertEquals("خمن شعار او اسم النادي", c2.title)
        assertEquals(listOf(10, 25, 50, 100), c2.allowedQuestionCounts)

        // 3. Challenge 3: كأس العالم (10, 25, 50)
        val c3 = challenges[2]
        assertEquals("world_cup", c3.id)
        assertEquals(3, c3.orderIndex)
        assertEquals("كأس العالم", c3.title)
        assertEquals(listOf(10, 25, 50), c3.allowedQuestionCounts)

        // 4. Challenge 4: كوّن فريق
        val c4 = challenges[3]
        assertEquals("squad_builder", c4.id)
        assertEquals(4, c4.orderIndex)
        assertEquals("كوّن فريق", c4.title)
    }

    @Test
    fun repository_allTenLeagues_availableInSquadPlayersPool() {
        val repository = AppDataRepository()
        val pool = repository.squadPlayersPool

        val requiredLeagues = setOf(
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

        val leaguesInPool = pool.map { it.league }.toSet()
        assertTrue(leaguesInPool.containsAll(requiredLeagues))
    }

    @Test
    fun repository_twelveTournaments_availableForSimulation() {
        val repository = AppDataRepository()
        val tournaments = repository.tournamentChoices

        assertEquals(12, tournaments.size)

        // Verify simulation runs and generates matches & report
        val chosenTournament = tournaments.first { it.id == "ucl" }
        val dummySquad = repository.squadPlayersPool.take(11)

        val report = repository.simulateTournament(chosenTournament, dummySquad)
        assertNotNull(report)
        assertEquals("دوري أبطال أوروبا", report.tournamentName)
        assertEquals(3, report.matches.size)
        assertTrue(report.xpEarned > 0)
    }

    @Test
    fun repository_addScore_awardsTenPointsPerCorrectAnswer() {
        val repository = AppDataRepository()
        val initialPoints = repository.currentUser.value.points

        repository.addPoints(10)
        assertEquals(initialPoints + 10, repository.currentUser.value.points)
    }

    @Test
    fun levelingSystem_calculatesLevelAndTierProgressCorrectly() {
        val userWithZeroPoints = com.example.data.model.UserProfile(points = 0)
        val progressZero = userWithZeroPoints.levelProgress
        assertEquals(1, progressZero.level)
        assertEquals("tier_rookie", progressZero.currentTier.tierId)
        assertEquals(250, progressZero.pointsToNextLevel)
        assertEquals(0.0f, progressZero.levelProgressRatio, 0.001f)

        val userMid = com.example.data.model.UserProfile(points = 2850)
        val progressMid = userMid.levelProgress
        assertEquals(12, progressMid.level)
        assertEquals("tier_star", progressMid.currentTier.tierId)
    }

    @Test
    fun repository_v14_soundSettingsAndNotifications() {
        val repository = AppDataRepository()

        // 1. Sound settings test
        val initialSound = repository.soundSettings.value
        assertTrue(initialSound.soundEffectsEnabled)
        repository.toggleSoundEffects(false)
        assertEquals(false, repository.soundSettings.value.soundEffectsEnabled)
        repository.setSoundVolume(0.5f)
        assertEquals(0.5f, repository.soundSettings.value.volume, 0.01f)

        // 2. Notifications test & v1.4 release summary
        val initialNotifs = repository.notifications.value
        assertTrue(initialNotifs.isNotEmpty())
        val v14Notif = initialNotifs.first()
        assertEquals("v1.4", v14Notif.versionTag)
        assertTrue(v14Notif.title.contains("1.4"))
        repository.markNotificationAsRead(v14Notif.id)
        assertTrue(repository.notifications.value.first { it.id == v14Notif.id }.isRead)

        // Custom notification creation
        repository.addCustomNotification(
            title = "تحديث دفاعي جديد",
            description = "إضافة أسئلة عن أساطير الدفاع في كؤوس العالم",
            category = "تحديث كروي",
            versionTag = "v1.4"
        )
        val updatedNotifs = repository.notifications.value
        assertEquals("تحديث دفاعي جديد", updatedNotifs.first().title)
        assertEquals("v1.4", updatedNotifs.first().versionTag)

        // 3. User profile update test
        repository.updateUserProfile(
            name = "الكابتن هشام",
            avatarEmoji = "👑",
            favoriteClub = "ريال مدريد"
        )
        assertEquals("الكابتن هشام", repository.currentUser.value.name)
        assertEquals("👑", repository.currentUser.value.avatarEmoji)
        assertEquals("ريال مدريد", repository.currentUser.value.favoriteClub)
    }

    @Test
    fun repository_exchangePointsForCoins_deductsPointsAndAddsCoins() {
        val repository = AppDataRepository()
        // Ensure user has points
        repository.addPoints(500)
        val initialPoints = repository.currentUser.value.points
        val initialCoins = repository.currentUser.value.virtualCoins

        val success = repository.exchangePointsForCoins(requiredPoints = 200, grantedCoins = 100.0)
        assertTrue(success)
        assertEquals(initialPoints - 200, repository.currentUser.value.points)
        assertEquals(initialCoins + 100.0, repository.currentUser.value.virtualCoins, 0.001)
    }

    @Test
    fun repository_deductCoinsForChallenge_deductsCorrectCost() {
        val repository = AppDataRepository()
        // Give enough coins
        repository.exchangePointsForCoins(requiredPoints = 0, grantedCoins = 100.0)
        val initialCoins = repository.currentUser.value.virtualCoins

        val success = repository.deductCoinsForChallenge("guess_player")
        assertTrue(success)
        // guess_player costs 25 coins
        assertEquals(initialCoins - 25.0, repository.currentUser.value.virtualCoins, 0.001)
    }

    @Test
    fun repository_grantPackageSubscription_unlocksChallengesAndAddsCoins() {
        val repository = AppDataRepository()
        val initialCoins = repository.currentUser.value.virtualCoins

        // Grant Gold VIP package
        repository.grantPackageSubscription("باقة الكرة الذهبية (VIP) - 220 ج.م")

        val updatedUser = repository.currentUser.value
        assertEquals(initialCoins + 4000.0, updatedUser.virtualCoins, 0.001)
        assertEquals("pkg_gold", updatedUser.subscribedPackageId)
        assertTrue(updatedUser.unlockedChallengeIds.contains("guess_club"))
        assertTrue(updatedUser.unlockedChallengeIds.contains("world_cup"))
        assertTrue(updatedUser.unlockedChallengeIds.contains("squad_builder"))
    }

    @Test
    fun admin_loginWithExactCredentials_activatesInfinitePrivilegesAndMadiraTag() {
        val repository = AppDataRepository()

        // Test with invalid credentials first
        val fail1 = repository.loginAsAdmin("01228823207", "wrong_pass")
        assertEquals(false, fail1)
        val fail2 = repository.loginAsAdmin("01000000000", "879546123")
        assertEquals(false, fail2)

        // Test with exact credentials requested by user
        val success = repository.loginAsAdmin("01228823207", "879546123")
        assertTrue(success)

        val admin = repository.currentUser.value
        assertEquals("Madira", admin.userTagId)
        assertEquals("Madira", admin.formattedTagId)
        assertTrue(admin.isAdmin)
        assertEquals(999999999.0, admin.virtualCoins, 0.001)
        assertEquals(999999999, admin.points)

        // All challenges must be unlocked forever
        assertTrue(admin.unlockedChallengeIds.contains("guess_player"))
        assertTrue(admin.unlockedChallengeIds.contains("guess_club"))
        assertTrue(admin.unlockedChallengeIds.contains("world_cup"))
        assertTrue(admin.unlockedChallengeIds.contains("squad_builder"))

        // Coins must not decrease when playing challenges
        val deductOk = repository.deductCoinsForChallenge("squad_builder")
        assertTrue(deductOk)
        assertEquals(999999999.0, repository.currentUser.value.virtualCoins, 0.001)
    }

    @Test
    fun userTags_guestAccountHasGuestTagAnd100Coins500Points_andNewGoogleMatches() {
        val repository = AppDataRepository()

        // 1. Initial / Guest user has "Guest" tag and 500 points and 100 coins
        val initialGuest = repository.currentUser.value
        assertEquals("Guest", initialGuest.userTagId)
        assertEquals("Guest", initialGuest.formattedTagId)
        assertEquals(500, initialGuest.points)
        assertEquals(100.0, initialGuest.virtualCoins, 0.001)

        repository.loginAsGuest()
        val guest = repository.currentUser.value
        assertEquals("Guest", guest.userTagId)
        assertEquals("Guest", guest.formattedTagId)
        assertEquals(500, guest.points)
        assertEquals(100.0, guest.virtualCoins, 0.001)

        // 2. New Google user gets unique MAD- tag, and starts with 500 points and 100 coins
        repository.login("user@gmail.com", "كابتن جوجل", "الأهلي المصري", "⚽", isNewUser = true)
        val googleUser = repository.currentUser.value
        assertTrue(googleUser.userTagId.startsWith("MAD-"))
        assertTrue(googleUser.formattedTagId.startsWith("#MAD-"))
        assertEquals(500, googleUser.points)
        assertEquals(100.0, googleUser.virtualCoins, 0.001)
        assertEquals(false, googleUser.isAdmin)

        // 3. Admin tag formatting test
        val adminUser = com.example.data.model.UserProfile(userTagId = "Madira", isAdmin = true)
        assertEquals("Madira", adminUser.formattedTagId)
    }

    @Test
    fun friendSystem_noFakeFriendsOrRequestsInitially_andCanSendRealRequests() {
        val repository = AppDataRepository()

        // 1. Initial list of friends must be empty (no fake friends)
        assertTrue(repository.friends.value.isEmpty())

        // 2. Initial list of friend requests must be empty (no fake requests)
        assertTrue(repository.friendRequests.value.isEmpty())

        // 3. Sending friend request to real player tag succeeds
        val sendResult = repository.sendFriendRequest("MAD-48192")
        assertTrue(sendResult.contains("تم إرسال طلب الصداقة بنجاح"))

        // 4. Sending friend request to Admin connects directly
        val sendAdmin = repository.sendFriendRequest("Madira")
        assertTrue(sendAdmin.contains("مدير النظام Madira 👑"))
        assertTrue(repository.friends.value.any { it.userTagId == "Madira" })
    }
}
