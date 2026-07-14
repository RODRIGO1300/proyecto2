package com.example.proyeto2.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import java.io.OutputStream

object PdfUtils {

    fun crearYGuardarPdfEnDescargas(
        context: Context,
        nombreArchivo: String,
        contenido: String,
        titulo: String = "Documento Generado",
        subtitulo: String? = null
    ) {
        val pdfDocument = PdfDocument()

        // Página A4
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isAntiAlias = true
            isFakeBoldText = true
        }

        // Título
        canvas.drawText(titulo, 50f, 60f, paint)

        var y = 90f

        // Subtítulo
        if (subtitulo != null) {
            paint.apply {
                textSize = 14f
                isFakeBoldText = false
                color = Color.DKGRAY
            }
            canvas.drawText(subtitulo, 50f, y, paint)
            y += 30f
        }

        // Contenido
        paint.apply {
            textSize = 12f
            color = Color.BLACK
        }
        val lines = contenido.split("\n")
        for (line in lines) {
            canvas.drawText(line, 50f, y, paint)
            y += 20f
        }

        pdfDocument.finishPage(page)

        // Guardar en Descargas
        val nombreConExtension = if (nombreArchivo.endsWith(".pdf")) nombreArchivo else "$nombreArchivo.pdf"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nombreConExtension)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")
            }
        }

        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val uri = resolver.insert(collection, contentValues)

        if (uri != null) {
            var outputStream: OutputStream? = null
            try {
                outputStream = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(context, "PDF guardado en Descargas", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al guardar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                outputStream?.close()
                pdfDocument.close()
            }
        } else {
            pdfDocument.close()
            Toast.makeText(context, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show()
        }
    }
}