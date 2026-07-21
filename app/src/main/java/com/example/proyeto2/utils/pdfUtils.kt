package com.example.proyeto2.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import com.example.proyeto2.models.planner.MealPlanSlot
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfUtils {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f
    private const val BRAND_GREEN = 0xFF2F6F4E.toInt()
    private const val BRAND_LEAF = 0xFF76A96B.toInt()
    private const val BRAND_CREAM = 0xFFFFF8ED.toInt()
    private const val BRAND_INK = 0xFF17231F.toInt()
    private const val BRAND_MUTED = 0xFF6E7C72.toInt()
    private const val LIGHT_ROW = 0xFFF7EBDD.toInt()

    private val weekDays = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
    private val mealTimes = listOf("Almuerzo", "Comida", "Cena")

    fun crearReportePlaneadorSemanal(
        context: Context,
        nombreArchivo: String,
        slots: List<MealPlanSlot>
    ) {
        val pdfDocument = PdfDocument()
        val sortedSlots = slots.sortedWith(compareBy({ weekDays.indexOf(it.dia) }, { mealTimes.indexOf(it.tiempo) }))
        val generatedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        var pageNumber = 1
        var page = newPage(pdfDocument, pageNumber)
        var canvas = page.canvas
        var y = drawPlannerHeader(canvas, generatedDate)

        drawSummary(canvas, sortedSlots)
        y += 86f

        weekDays.forEach { day ->
            val requiredHeight = 42f + (mealTimes.size * 58f)
            if (y + requiredHeight > PAGE_HEIGHT - 50f) {
                drawFooter(canvas, pageNumber)
                pdfDocument.finishPage(page)
                pageNumber += 1
                page = newPage(pdfDocument, pageNumber)
                canvas = page.canvas
                y = drawContinuedHeader(canvas, generatedDate)
            }

            y = drawDaySection(canvas, y, day, sortedSlots)
            y += 12f
        }

        drawFooter(canvas, pageNumber)
        pdfDocument.finishPage(page)
        guardarPdfEnDescargas(context, nombreArchivo, pdfDocument)
    }

    fun crearYGuardarPdfEnDescargas(
        context: Context,
        nombreArchivo: String,
        contenido: String,
        titulo: String = "Documento Generado",
        subtitulo: String? = null
    ) {
        val pdfDocument = PdfDocument()
        val page = newPage(pdfDocument, 1)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }

        canvas.drawText(titulo, 50f, 60f, paint)
        var y = 90f

        if (subtitulo != null) {
            paint.apply {
                textSize = 14f
                isFakeBoldText = false
                color = Color.DKGRAY
            }
            canvas.drawText(subtitulo, 50f, y, paint)
            y += 30f
        }

        paint.apply {
            textSize = 12f
            color = Color.BLACK
        }
        contenido.split("\n").forEach { line ->
            canvas.drawText(line, 50f, y, paint)
            y += 20f
        }

        pdfDocument.finishPage(page)
        guardarPdfEnDescargas(context, nombreArchivo, pdfDocument)
    }

    private fun guardarPdfEnDescargas(
        context: Context,
        nombreArchivo: String,
        pdfDocument: PdfDocument
    ) {
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

    private fun newPage(pdfDocument: PdfDocument, pageNumber: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        return pdfDocument.startPage(pageInfo)
    }

    private fun drawPlannerHeader(canvas: Canvas, generatedDate: String): Float {
        canvas.drawRect(
            0f,
            0f,
            PAGE_WIDTH.toFloat(),
            132f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BRAND_GREEN }
        )

        canvas.drawRoundRect(
            RectF(MARGIN, 28f, 126f, 70f),
            20f,
            20f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BRAND_LEAF }
        )
        canvas.drawText(
            "Recetario",
            MARGIN + 15f,
            55f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 13f
                isFakeBoldText = true
            }
        )
        canvas.drawText(
            "Plan semanal de comidas",
            MARGIN,
            100f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 28f
                isFakeBoldText = true
            }
        )
        canvas.drawText(
            "Reporte generado: $generatedDate",
            MARGIN,
            120f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = BRAND_CREAM
                textSize = 12f
            }
        )

        return 170f
    }

    private fun drawContinuedHeader(canvas: Canvas, generatedDate: String): Float {
        canvas.drawText(
            "Plan semanal de comidas",
            MARGIN,
            42f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = BRAND_GREEN
                textSize = 18f
                isFakeBoldText = true
            }
        )
        canvas.drawText(
            "Continuacion - $generatedDate",
            MARGIN,
            60f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = BRAND_MUTED
                textSize = 10f
            }
        )
        drawDivider(canvas, 76f)
        return 98f
    }

    private fun drawSummary(canvas: Canvas, slots: List<MealPlanSlot>) {
        val assigned = slots.count { it.idMeal.isNotBlank() }
        val total = weekDays.size * mealTimes.size
        val daysWithMeals = slots.map { it.dia }.distinct().size
        val cards = listOf(
            "Platillos asignados" to "$assigned/$total",
            "Dias con plan" to "$daysWithMeals/${weekDays.size}",
            "Pendientes" to "${total - assigned}"
        )
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_MUTED
            textSize = 10f
        }
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_INK
            textSize = 20f
            isFakeBoldText = true
        }
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BRAND_CREAM }

        var x = MARGIN
        cards.forEach { (label, value) ->
            canvas.drawRoundRect(RectF(x, 150f, x + 160f, 218f), 14f, 14f, cardPaint)
            canvas.drawText(label, x + 14f, 176f, labelPaint)
            canvas.drawText(value, x + 14f, 203f, valuePaint)
            x += 174f
        }
    }

    private fun drawDaySection(
        canvas: Canvas,
        startY: Float,
        day: String,
        slots: List<MealPlanSlot>
    ): Float {
        val sectionWidth = PAGE_WIDTH - (MARGIN * 2)
        val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BRAND_GREEN }
        val rowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LIGHT_ROW }
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 15f
            isFakeBoldText = true
        }
        val mealTimePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_GREEN
            textSize = 11f
            isFakeBoldText = true
        }
        val dishPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_INK
            textSize = 11f
            isFakeBoldText = true
        }
        val commentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_MUTED
            textSize = 9f
        }

        canvas.drawRoundRect(RectF(MARGIN, startY, MARGIN + sectionWidth, startY + 32f), 12f, 12f, sectionPaint)
        canvas.drawText(day, MARGIN + 14f, startY + 22f, titlePaint)

        var y = startY + 42f
        mealTimes.forEach { mealTime ->
            val slot = slots.firstOrNull { it.dia == day && it.tiempo == mealTime }
            val dish = slot?.nombre?.ifBlank { "Sin asignar" } ?: "Sin asignar"
            val comments = slot?.comentarios.orEmpty().ifBlank { "Sin comentarios" }

            canvas.drawRoundRect(RectF(MARGIN, y, MARGIN + sectionWidth, y + 48f), 10f, 10f, rowPaint)
            canvas.drawText(mealTime, MARGIN + 12f, y + 20f, mealTimePaint)
            drawWrappedText(canvas, dish, MARGIN + 108f, y + 19f, 290f, dishPaint, 13f, 2)
            drawWrappedText(canvas, "Comentarios: $comments", MARGIN + 108f, y + 35f, 365f, commentPaint, 11f, 1)
            y += 58f
        }
        return y
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        maxWidth: Float,
        paint: Paint,
        lineHeight: Float,
        maxLines: Int
    ) {
        val words = text.split(" ")
        var line = ""
        var currentY = y
        var drawnLines = 0

        for (word in words) {
            val testLine = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(testLine) <= maxWidth) {
                line = testLine
            } else {
                canvas.drawText(line, x, currentY, paint)
                currentY += lineHeight
                drawnLines += 1
                line = word
            }
            if (drawnLines >= maxLines) return
        }

        if (line.isNotBlank() && drawnLines < maxLines) {
            canvas.drawText(line, x, currentY, paint)
        }
    }

    private fun drawDivider(canvas: Canvas, y: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFEADCCC.toInt()
            strokeWidth = 1.5f
        }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)
    }

    private fun drawFooter(canvas: Canvas, pageNumber: Int) {
        drawDivider(canvas, PAGE_HEIGHT - 38f)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_MUTED
            textSize = 9f
        }
        canvas.drawText("Recetario - Planeador semanal", MARGIN, PAGE_HEIGHT - 20f, paint)
        canvas.drawText("Pagina $pageNumber", PAGE_WIDTH - MARGIN - 44f, PAGE_HEIGHT - 20f, paint)
    }
}
