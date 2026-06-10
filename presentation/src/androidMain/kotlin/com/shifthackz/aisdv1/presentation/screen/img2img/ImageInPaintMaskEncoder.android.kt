package com.shifthackz.aisdv1.presentation.screen.img2img

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Base64
import java.io.ByteArrayOutputStream

internal actual suspend fun encodeInPaintMaskBase64(
    imageBase64: String,
    strokes: List<InPaintStroke>,
): String? = runCatching {
    val raw = imageBase64.substringAfter("base64,", imageBase64)
    val imageBytes = Base64.decode(raw, Base64.DEFAULT)
    val source = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        ?: return@runCatching null
    val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    strokes.forEach { stroke ->
        paint.strokeWidth = stroke.brushSize *
            (source.width.toFloat() / stroke.canvasWidth.coerceAtLeast(1))
        canvas.drawPath(stroke.toAndroidPath(source.width, source.height), paint)
    }

    ByteArrayOutputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}.getOrNull()

private fun InPaintStroke.toAndroidPath(
    targetWidth: Int,
    targetHeight: Int,
): Path {
    val path = Path()
    val first = points.firstOrNull() ?: return path
    val scaleX = targetWidth.toFloat() / canvasWidth.coerceAtLeast(1)
    val scaleY = targetHeight.toFloat() / canvasHeight.coerceAtLeast(1)
    var previousX = first.x * scaleX
    var previousY = first.y * scaleY
    path.moveTo(previousX, previousY)
    points.drop(1).forEach { point ->
        val x = point.x * scaleX
        val y = point.y * scaleY
        path.quadTo(
            previousX,
            previousY,
            (previousX + x) / 2f,
            (previousY + y) / 2f,
        )
        previousX = x
        previousY = y
    }
    path.lineTo(previousX, previousY)
    return path
}
