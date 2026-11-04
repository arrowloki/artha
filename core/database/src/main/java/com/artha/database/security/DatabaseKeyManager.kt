package com.artha.database.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import java.util.Base64

/**
 * Manages the SQLCipher encryption key using Android KeyStore-backed MasterKey.
 */
class DatabaseKeyManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Retrieves or generates a 256-bit cryptographically secure passphrase for SQLCipher.
     */
    fun getOrCreateDatabasePassphrase(): ByteArray {
        val existingKeyBase64 = encryptedPrefs.getString(KEY_DB_PASSPHRASE, null)
        if (existingKeyBase64 != null) {
            return Base64.getDecoder().decode(existingKeyBase64)
        }

        // Generate 32 bytes (256-bit) of cryptographically secure random bytes
        val keyBytes = ByteArray(32)
        SecureRandom().nextBytes(keyBytes)

        val encoded = Base64.getEncoder().encodeToString(keyBytes)
        encryptedPrefs.edit().putString(KEY_DB_PASSPHRASE, encoded).apply()

        return keyBytes
    }

    companion object {
        private const val PREFS_NAME = "artha_secure_vault"
        private const val KEY_DB_PASSPHRASE = "artha_db_cipher_key"
    }
}
