package com.example.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages persistent user login session and caching.
 * Keeps user logged in across app restarts, reboots, and updates.
 */
class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "autoverse_user_session_cache"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_AVATAR = "key_user_avatar"

        const val DEFAULT_AVATAR = "https://res.cloudinary.com/dkdzuyjc4/image/upload/c_thumb,g_face,w_600,h_600,z_0.75/v1792432515/1784644774412_nsmlyh.jpg"
        const val DEFAULT_NAME = "Muhammad Adil Zaman"
        const val DEFAULT_EMAIL = "malikmuhammadadilzaman@gmail.com"
        const val DEFAULT_ID = "user_adil_zaman"
    }

    /**
     * True if user has authenticated previously. Defaults to true for Muhammad Adil Zaman
     * so user is automatically logged in upon launching the app.
     */
    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, DEFAULT_EMAIL) ?: DEFAULT_EMAIL
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userId: String
        get() = prefs.getString(KEY_USER_ID, DEFAULT_ID) ?: DEFAULT_ID
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, DEFAULT_NAME) ?: DEFAULT_NAME
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userAvatar: String
        get() = prefs.getString(KEY_USER_AVATAR, DEFAULT_AVATAR) ?: DEFAULT_AVATAR
        set(value) = prefs.edit().putString(KEY_USER_AVATAR, value).apply()

    fun saveLoginSession(id: String, email: String, name: String, avatar: String = DEFAULT_AVATAR) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, id)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_AVATAR, avatar)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
}
