package vip.qsos.components_filepicker.lib

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**图片 URI 转 File 或 Bitmap 工具类
 * @author : 华清松
 */
object FileConverters {

    /** Uri 转为 Bitmap */
    suspend fun getBitmap(context: Context, uri: Uri, result: (Bitmap) -> Unit) {
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

    /**根据Uri获取图片的绝对路径*/
    suspend fun getRealPathFromUri(context: Context, uri: Uri, result: (String) -> Unit) {
        flow {
            try {
                getRealPathFromUri(context, uri)
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

    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) {
                val divide = documentId.split(":").toTypedArray()
                val type = divide[0]
                val mediaUri: Uri?
                mediaUri = when (type) {
                    "image" -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> {
                        return null
                    }
                }
                val selection = BaseColumns._ID + "=?"
                val selectionArgs = arrayOf(divide[1])
                filePath = getDataColumn(context, mediaUri, selection, selectionArgs)
            } else if (isDownloadsDocument(uri)) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(documentId)
                )
                filePath = getDataColumn(context, contentUri, null, null)
            } else if (isExternalStorageDocument(uri)) {
                val split = documentId.split(":").toTypedArray()
                if (split.size >= 2) {
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        filePath =
                            Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                }
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {
            filePath = getDataColumn(context, uri, null, null)
        } else if (ContentResolver.SCHEME_FILE == uri.scheme) {
            filePath = uri.path
        } else {
            filePath = uri.path
        }
        return filePath
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var path: String? = null
        val projection = arrayOf("_data")
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e: java.lang.Exception) {
            cursor?.close()
        }
        return path
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }
}
