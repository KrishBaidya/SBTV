package com.example.sbtv.player

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists the user's chosen player engine across app restarts.
 * Supported values: "exoplayer" (default) or "vlc"
 */
class PlayerPreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSelectedPlayer(): String = PLAYER_VLC

    fun setSelectedPlayer(player: String) {
        prefs.edit().putString(KEY_PLAYER, player).apply()
    }

    companion object {
        const val PLAYER_VLC = "vlc"

        private const val PREFS_NAME = "sbtv_player_prefs"
        private const val KEY_PLAYER = "selected_player"
    }
}
