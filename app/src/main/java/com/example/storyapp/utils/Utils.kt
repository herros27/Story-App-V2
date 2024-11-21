package com.example.storyapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import com.example.storyapp.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

private const val MAXIMAL_SIZE = 1000000 
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

@SuppressLint("StringFormatMatches")
@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(context: Context, createdAt: String?): String {
    if (createdAt.isNullOrEmpty()) return context.getString(R.string.time_is_not_available)
    
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val instant = Instant.from(formatter.parse(createdAt))
    val now = Instant.now()
    val duration = java.time.Duration.between(instant, now)
    return when {
        duration.toDays() > 0 -> context.getString(R.string.days_a_go, duration.toDays())
        duration.toHours() > 0 -> context.getString(R.string.hours_a_go, duration.toHours())
        duration.toMinutes() > 0 -> context.getString(R.string.minute_a_go, duration.toMinutes())
        else -> context.getString(R.string.now)
    }
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun getCircularBitmap(bitmap: Bitmap): Bitmap {
    val size = Math.min(bitmap.width, bitmap.height)
    val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint()
    val rect = Rect(0, 0, size, size)
    val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.shader = shader
    paint.isAntiAlias = true

    canvas.drawCircle((size / 2).toFloat(), (size / 2).toFloat(), (size / 2).toFloat(), paint)
    return output
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

fun isNetworkError(error: String, context: Context): Boolean {
    return error.contains(context.getString(R.string.unable_to_resolve_host), ignoreCase = true) ||
            error.contains(context.getString(R.string.network_error), ignoreCase = true)
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun View.show(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}
