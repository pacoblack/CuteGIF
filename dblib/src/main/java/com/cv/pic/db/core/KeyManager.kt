package com.cv.pic.db.core

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object KeyManager {

  private const val KEY_ALIAS = "db_encryption_key"
  private const val SHARED_PREFS_NAME = "secure_prefs"
  private const val ENCRYPTED_PASSPHRASE_KEY = "encrypted_passphrase"

  // 生成或获取数据库密钥
  fun getOrCreateDatabasePassphrase(context: Context): ByteArray {
    val encryptedPassphrase = getStoredPassphrase(context)
    if (encryptedPassphrase != null) {
      return decryptPassphrase(encryptedPassphrase)
    }

    // 生成新密钥
    val passphrase = generateRandomPassphrase()
    val encrypted = encryptPassphrase(passphrase)
    storePassphrase(context, encrypted)
    return passphrase
  }

  // 生成随机密钥（32字节）
  private fun generateRandomPassphrase(): ByteArray {
    val passphrase = ByteArray(32)
    java.security.SecureRandom().nextBytes(passphrase)
    return passphrase
  }

  // 获取存储的加密密钥
  private fun getStoredPassphrase(context: Context): ByteArray? {
    val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    val base64 = prefs.getString(ENCRYPTED_PASSPHRASE_KEY, null) ?: return null
    return Base64.decode(base64, Base64.DEFAULT)
  }

  // 存储加密密钥
  private fun storePassphrase(context: Context, encrypted: ByteArray) {
    val base64 = Base64.encodeToString(encrypted, Base64.DEFAULT)
    val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(ENCRYPTED_PASSPHRASE_KEY, base64).apply()
  }

  // 使用KeyStore密钥加密数据库密钥
  private fun encryptPassphrase(passphrase: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
    return cipher.doFinal(passphrase)
  }

  // 解密数据库密钥
  private fun decryptPassphrase(encrypted: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey())
    return cipher.doFinal(encrypted)
  }

  // 获取或创建KeyStore密钥
  private fun getOrCreateKey(): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

    if (!keyStore.containsAlias(KEY_ALIAS)) {
      createKey()
    }

    return keyStore.getKey(KEY_ALIAS, null) as SecretKey
  }

  // 创建KeyStore密钥
  private fun createKey() {
    val keyGenerator = KeyGenerator.getInstance(
      KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
    )

    keyGenerator.init(
      KeyGenParameterSpec.Builder(
        KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
      )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .setUserAuthenticationRequired(true) // 需要用户认证
        .setUserAuthenticationValidityDurationSeconds(60) // 认证有效期
        .build()
    )

    keyGenerator.generateKey()
  }
}