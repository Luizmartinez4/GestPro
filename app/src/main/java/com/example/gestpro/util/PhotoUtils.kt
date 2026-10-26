package com.example.gestpro.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

object PhotoUtils {
    /** Copia a foto escolhida para o armazenamento interno do app e devolve o caminho. */
    fun copyToInternal(context: Context, uri: Uri): String? = try {
        val dir = File(context.filesDir, "employees").apply { mkdirs() }
        val file = File(dir, "emp_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }

    fun load(path: String?): ImageBitmap? {
        if (path == null || !File(path).exists()) return null
        val options = BitmapFactory.Options().apply { inSampleSize = 4 }
        return BitmapFactory.decodeFile(path, options)?.asImageBitmap()
    }
}
