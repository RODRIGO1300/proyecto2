package com.example.proyeto2.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

object TranslationHelper {
    fun translateEnglishToSpanish(
        text: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanText = text.trim()
        if (cleanText.isBlank()) {
            onSuccess("")
            return
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator.translate(cleanText)
                    .addOnSuccessListener { translatedText ->
                        onSuccess(translatedText)
                        translator.close()
                    }
                    .addOnFailureListener { exception ->
                        onError(exception.message ?: "No se pudo traducir el texto.")
                        translator.close()
                    }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "No se pudo descargar el modelo de traduccion.")
                translator.close()
            }
    }
}
