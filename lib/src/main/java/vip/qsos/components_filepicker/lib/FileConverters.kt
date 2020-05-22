package vip.qsos.components_filepicker.lib

import android.content.Context
import android.database.Cursor
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
    suspend fun uriToFile(context: Context, uri: Uri, result: (File?) -> Unit) {
        flow {
            try {
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? =
                    context.contentResolver.query(uri, filePathColumn, null, null, null)
                cursor?.moveToFirst()
                var filePath: String? = null
                cursor?.getColumnIndex(filePathColumn[0])?.let {
                    filePath = cursor.getString(it)
                }
                cursor?.close()
                filePath?.let { File(it) }
            } catch (e: Exception) {
                null
            }?.let {
                emit(it)
            }
        }
            .flowOn(Dispatchers.IO)
            .collect {
                result.invoke(it)
            }
    }

    /** Uri 转为 File */
    suspend fun uriCopyToFile(context: Context, uri: Uri, file: File, result: (File?) -> Unit) {
        flow {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                file.copyInputStreamToFile(inputStream!!)
                file
            } catch (e: Exception) {
                null
            }?.let {
                emit(it)
            }
        }
            .flowOn(Dispatchers.IO)
            .collect {
                result.invoke(it)
            }
    }

    /** Uri 转为 Bitmap */
    suspend fun uriToBitmap(context: Context, uri: Uri, result: (Bitmap?) -> Unit) {
        flow<Bitmap?> {
            try {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } catch (e: Exception) {
                null
            }?.let {
                emit(it)
            }
        }
            .flowOn(Dispatchers.IO)
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
