package vip.qsos.components_filepicker.lib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.InputStream

/**图片 URI 转 File 或 Bitmap 工具类
 * @author : 华清松
 */
object FileConverters {

    /** Uri 转为 File */
    @SuppressLint("Recycle")
    suspend fun uriToFile(context: Context, uri: Uri, result: (File) -> Unit) {
        flow {
            try {
                var filePath: String? = null
                context.contentResolver.query(
                    uri, arrayOf(MediaStore.Images.Media.DATA),
                    null, null, null
                )?.let { cursor ->
                    cursor.moveToFirst()
                    cursor.getColumnIndex(MediaStore.Images.Media.DATA).let {
                        filePath = cursor.getString(it)
                    }
                    cursor.close()
                }
                filePath?.let { File(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }?.let {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                result.invoke(it)
            }
    }

    /** Uri 转为 File */
    suspend fun uriCopyToFile(context: Context, uri: Uri, file: File, result: (File) -> Unit) {
        flow {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                file.copyInputStreamToFile(inputStream!!)
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }?.let {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                result.invoke(it)
            }
    }

    /** Uri 转为 Bitmap */
    suspend fun uriToBitmap(context: Context, uri: Uri, result: (Bitmap) -> Unit) {
        flow {
            try {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }?.let {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                result.invoke(it)
            }
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

}
