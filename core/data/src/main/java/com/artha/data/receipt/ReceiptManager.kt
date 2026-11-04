package com.artha.data.receipt

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

/**
 * Manages receipt photo attachments in app-private scoped storage.
 * Completely offline with zero cloud backup or transmission.
 */
class ReceiptManager(private val context: Context) {

    private val receiptsDir: File by lazy {
        val dir = File(context.filesDir, "receipts")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    /**
     * Saves an input stream (e.g. from camera or photo picker) to internal storage.
     * Returns the absolute file path to store in TransactionEntity.receiptImagePath.
     */
    fun saveReceipt(inputStream: InputStream, extension: String = "jpg"): String {
        val filename = "receipt_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$extension"
        val destinationFile = File(receiptsDir, filename)

        FileOutputStream(destinationFile).use { output ->
            inputStream.copyTo(output)
        }

        return destinationFile.absolutePath
    }

    fun getReceiptFile(path: String): File? {
        val file = File(path)
        return if (file.exists()) file else null
    }

    fun deleteReceipt(path: String): Boolean {
        val file = File(path)
        return if (file.exists()) file.delete() else false
    }
}
