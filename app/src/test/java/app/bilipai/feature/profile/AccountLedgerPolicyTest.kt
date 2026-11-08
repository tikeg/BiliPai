package app.bilipai.feature.profile

import app.bilipai.core.store.AccountSessionIdentity
import app.bilipai.data.model.AccountLedgerKind
import app.bilipai.data.model.response.AccountLedgerEntry
import app.bilipai.data.model.response.AccountLedgerResponse
import app.bilipai.navigation3.BiliPaiNavKey
import app.bilipai.navigation3.legacyRouteToBiliPaiNavKey
import app.bilipai.navigation3.toLegacyRoute
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccountLedgerPolicyTest {
    @Test
    fun preservesDecimalCoinsAndShowsDirectionWithoutRounding() {
        assertEquals(LedgerDelta("+0.5", LedgerDeltaDirection.INCREASE), formatLedgerDelta(JsonPrimitive("0.50")))
        assertEquals(LedgerDelta("-1.25", LedgerDeltaDirection.DECREASE), formatLedgerDelta(JsonPrimitive("-1.250")))
        assertEquals("+9007199254740993.01", formatLedgerDelta(JsonPrimitive("9007199254740993.01")).text)
        assertEquals("+1", formatLedgerDelta(JsonPrimitive(1)).text)
    }

    @Test
    fun zeroAndMissingOrMalformedNumbersHaveDifferentMeanings() {
        assertEquals(LedgerDelta("0", LedgerDeltaDirection.ZERO), formatLedgerDelta(JsonPrimitive("-0.00")))
        listOf(null, JsonNull, JsonPrimitive(""), JsonPrimitive("NaN"), JsonPrimitive(true), JsonPrimitive("1e100000")).forEach {
            assertEquals(LedgerDelta("未知", LedgerDeltaDirection.UNKNOWN), formatLedgerDelta(it))
        }
    }

    @Test
    fun decodesNumericAndStringDeltaAndDistinguishesEmptyFromMissingData() {
        val numeric = Json.decodeFromString<AccountLedgerResponse>("""{"code":0,"data":{"list":[{"time":"2026-10-26","delta":0.5,"reason":"每日登录"}]}}""")
        assertEquals("+0.5", formatLedgerDelta(numeric.data?.list?.single()?.delta).text)
        val string = Json.decodeFromString<AccountLedgerResponse>("""{"code":0,"data":{"list":[{"delta":"-2"}]}}""")
        assertEquals("-2", formatLedgerDelta(string.data?.list?.single()?.delta).text)
        assertNull(Json.decodeFromString<AccountLedgerResponse>("""{"code":0,"data":{}}""").data?.list)
        assertEquals(emptyList(), Json.decodeFromString<AccountLedgerResponse>("""{"code":0,"data":{"list":[]}}""").data?.list)
        assertEquals(-1, Json.decodeFromString<AccountLedgerResponse>("{}").code)
    }

    @Test
    fun sectionsKeepIndependentResultsAndErrors() {
        val identity = AccountSessionIdentity(mid = 1L, generation = 1L)
        val coins = AccountLedgerSection(entries = listOf(AccountLedgerEntry(delta = JsonPrimitive(1))))
        val experience = AccountLedgerSection(error = "暂时无法加载")
        val state = AccountLedgerState(identity = identity)
            .withSection(AccountLedgerKind.COINS, coins)
            .withSection(AccountLedgerKind.EXPERIENCE, experience)
        assertEquals(coins, state.section(AccountLedgerKind.COINS))
        assertEquals(experience, state.section(AccountLedgerKind.EXPERIENCE))
        val switched = AccountLedgerState(identity = AccountSessionIdentity(mid = 2L, generation = 2L))
        assertNull(switched.section(AccountLedgerKind.COINS).entries)
        assertNull(switched.section(AccountLedgerKind.EXPERIENCE).error)
    }

    @Test
    fun profileMenusAndRouteBridgeSelectTheRequestedLedger() {
        val opened = mutableListOf<AccountLedgerKind>()
        val actions = ledgerMenuActions(open = { opened += it })
        actions.forEach { it.onClick?.invoke() }
        assertEquals(AccountLedgerKind.entries.toList(), opened)
        assertTrue(ledgerMenuActions(null).isEmpty())
        AccountLedgerKind.entries.forEach { kind ->
            val key = BiliPaiNavKey.AccountLedger(kind)
            assertEquals(key, legacyRouteToBiliPaiNavKey(key.toLegacyRoute()))
        }
        assertIs<BiliPaiNavKey.Unknown>(legacyRouteToBiliPaiNavKey("account_ledger/invalid"))
        assertIs<BiliPaiNavKey.Unknown>(legacyRouteToBiliPaiNavKey("account_ledger/coins/extra"))
    }
}
