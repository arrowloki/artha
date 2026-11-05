package com.artha.database.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * Manages the SQLCipher encryption key using Android KeyStore-backed MasterKey
 * with a resilient private SharedPreferences fallback for devices with KeyStore issues.
 */
class DatabaseKeyManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        initPreferences()
    }

    private fun initPreferences(): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Throwable) {
            Log.w(TAG, "EncryptedSharedPreferences failed; falling back to app-private vault", e)
            context.getSharedPreferences(FALLBACK_PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Retrieves or generates a 256-bit cryptographically secure passphrase for SQLCipher.
     */
    fun getOrCreateDatabasePassphrase(): ByteArray {
        return try {
            val existingKeyBase64 = prefs.getString(KEY_DB_PASSPHRASE, null)
            if (!existingKeyBase64.isNullOrEmpty()) {
                Base64.decode(existingKeyBase64, Base64.NO_WRAP)
            } else {
                generateAndSaveKey()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to read database passphrase, regenerating key", e)
            generateAndSaveKey()
        }
    }

    private fun generateAndSaveKey(): ByteArray {
        val keyBytes = ByteArray(32)
        SecureRandom().nextBytes(keyBytes)
        val encoded = Base64.encodeToString(keyBytes, Base64.NO_WRAP)
        prefs.edit().putString(KEY_DB_PASSPHRASE, encoded).apply()
        return keyBytes
    }

    companion object {
        private const val TAG = "DatabaseKeyManager"
        private const val PREFS_NAME = "artha_secure_vault"
        private const val FALLBACK_PREFS_NAME = "artha_secure_vault_compat"
        private const val KEY_DB_PASSPHRASE = "artha_db_cipher_key"
    }
}
