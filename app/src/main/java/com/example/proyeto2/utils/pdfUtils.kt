package com.example.proyeto2.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.proyeto2.R
import com.example.proyeto2.models.planner.MealPlanSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.Locale

object PdfUtils {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f
    private const val BRAND_BROWN = 0xFF543D31.toInt()
    private const val BRAND_PEACH = 0xFFFCEADE.toInt()
    private const val BRAND_LINE = 0xFFD7CCC8.toInt()
    private const val BRAND_BG = 0xFFFFFFFF.toInt()

    private val weekDays = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
    private val mealTimes = listOf("Almuerzo", "Comida", "Cena")

    suspend fun crearReportePlaneadorSemanal(
        context: Context,
        nombreArchivo: String,
        slots: List<MealPlanSlot>
    ) = withContext(Dispatchers.IO) {
        // Pre-load images
        val imageMap = mutableMapOf<String, Bitmap>()
        val imageLoader = context.imageLoader

        slots.forEach { slot ->
            if (slot.imagen.isNotBlank() && !imageMap.containsKey(slot.imagen)) {
                try {
                    val request = ImageRequest.Builder(context)
                        .data(slot.imagen)
                        .allowHardware(false) // Required for PDF drawing
                        .build()
                    val result = imageLoader.execute(request)
                    if (result is SuccessResult) {
                        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                        if (bitmap != null) {
                            imageMap[slot.imagen] = bitmap
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val pdfDocument = PdfDocument()
        val page = newPage(pdfDocument, 1)
        val canvas = page.canvas

        // Background
        canvas.drawColor(BRAND_BG)

        drawBohoHeader(canvas, context)

        val cardWidth = 168f
        val cardHeight = 215f
        val spacing = 10f
        val startY = 110f

        // Grid layout for 7 days
        weekDays.forEachIndexed { index, day ->
            val col = index % 3
            val row = index / 3
            val x = MARGIN + (col * (cardWidth + spacing))
            val y = startY + (row * (cardHeight + spacing))

            drawDayCard(canvas, x, y, cardWidth, cardHeight, day, slots.filter { it.dia == day }, imageMap)
        }

        // Draw Watermark Logo in the corner with cropped effect
        drawWatermarkLogo(canvas, context)

        pdfDocument.finishPage(page)
        withContext(Dispatchers.Main) {
            guardarPdfEnDescargas(context, nombreArchivo, pdfDocument)
        }
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

    private fun drawBohoHeader(canvas: Canvas, context: Context) {
        // Draw Logo
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
            if (logoBitmap != null) {
                val imgSize = 60f
                val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, imgSize.toInt(), imgSize.toInt(), true)
                
                canvas.save()
                val path = Path().apply {
                    addCircle(MARGIN + (imgSize / 2), 20f + (imgSize / 2), imgSize / 2, Path.Direction.CW)
                }
                canvas.clipPath(path)
                canvas.drawBitmap(scaledLogo, MARGIN, 20f, null)
                canvas.restore()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_BROWN
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        }
        canvas.drawText("Planificador Semanal", MARGIN + 80f, 65f, paint)

        // Wavy decorative line
        val wavyPath = Path().apply {
            moveTo(MARGIN + 100f, 75f)
            cubicTo(MARGIN + 180f, 60f, MARGIN + 320f, 90f, MARGIN + 420f, 75f)
        }
        val wavyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_LINE
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
        }
        canvas.drawPath(wavyPath, wavyPaint)

        // Simple decorative lines
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_LINE
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(MARGIN, 92f, PAGE_WIDTH - MARGIN, 92f, linePaint)
    }

    private fun drawWatermarkLogo(
        canvas: Canvas,
        context: Context
    ) {
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
            if (logoBitmap != null) {
                // Larger size for the cropped corner effect
                val imgSize = 350f
                val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, imgSize.toInt(), imgSize.toInt(), true)
                
                // Positioned so it shows more of the logo in the bottom-right corner
                val x = PAGE_WIDTH - 250f
                val y = PAGE_HEIGHT - 250f
                val centerX = x + (imgSize / 2)
                val centerY = y + (imgSize / 2)
                
                canvas.save()
                
                // Apply rotation
                canvas.rotate(15f, centerX, centerY)
                
                // Circular clip to remove background (only keep the middle)
                val path = Path().apply {
                    addCircle(centerX, centerY, imgSize / 2.2f, Path.Direction.CW)
                }
                canvas.clipPath(path)
                
                // Draw as a watermark (with transparency)
                val paint = Paint().apply {
                    alpha = 35 // Subtle transparency
                }
                canvas.drawBitmap(scaledLogo, x, y, paint)
                
                canvas.restore()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawDayCard(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        day: String,
        daySlots: List<MealPlanSlot>,
        imageMap: Map<String, Bitmap>
    ) {
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BRAND_PEACH }
        canvas.drawRoundRect(RectF(x, y, x + width, y + height), 8f, 8f, cardPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_BROWN
            textSize = 14f
            isFakeBoldText = true
        }
        canvas.drawText(day.uppercase(), x + 12f, y + 25f, textPaint)
        canvas.drawLine(x + 12f, y + 32f, x + width - 12f, y + 32f, Paint().apply { color = BRAND_BROWN; strokeWidth = 1f })

        var currentY = y + 55f
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_BROWN
            textSize = 10f
        }
        val dishPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BRAND_BROWN
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }

        mealTimes.forEach { time ->
            val label = if (time == "Almuerzo") "Desayuno:" else "$time:"
            canvas.drawText(label, x + 12f, currentY, labelPaint)
            
            val slot = daySlots.find { it.tiempo == time }
            if (slot != null && slot.nombre.isNotBlank()) {
                val imageBitmap = imageMap[slot.imagen]
                val textXOffset = if (imageBitmap != null) {
                    val imgSize = 32f
                    val scaledImg = Bitmap.createScaledBitmap(imageBitmap, imgSize.toInt(), imgSize.toInt(), true)
                    canvas.drawBitmap(scaledImg, x + 12f, currentY + 5f, null)
                    imgSize + 8f
                } else {
                    0f
                }
                
                drawWrappedText(canvas, slot.nombre, x + 12f + textXOffset, currentY + 14f, width - 24f - textXOffset, dishPaint, 10f, 1)
            }
            
            drawDashedLine(canvas, x + 12f, currentY + 20f, x + width - 12f, currentY + 20f)
            currentY += 45f
        }
    }

    private fun drawDashedLine(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float) {
        val paint = Paint().apply {
            color = BRAND_LINE
            strokeWidth = 1f
            style = Paint.Style.STROKE
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(2f, 2f), 0f)
        }
        canvas.drawLine(x1, y1, x2, y2, paint)
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
}
