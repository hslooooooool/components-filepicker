package vip.qsos.components_filepicker.lib

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**媒体文件获取，提供系统的相机拍照、录制，系统的录音，系统的文件选取等功能的实现
 * @author : 华清松
 */
class PickerFragment(private val fm: FragmentManager) : Fragment() {

    companion object {
        const val TAG = "PickerFragment"
    }

    /**拍摄存储 URI*/
    private var cameraFileUri: Uri? = null

    /**默认限制录制时长为 20 秒*/
    private var pickerLimitTime: Int = 20

    @FilePicker.Type
    private var pickerType: Int = FilePicker.CHOOSER

    @FilePicker.FileType
    private var pickerFileType: Int = FilePicker.FILE
    private var pickerMimeTypes: Array<String> = arrayOf("*/*")

    /**选择界面标题。pickerType == CHOOSER 时生效*/
    private var pickerTitle: String = "文件选择"

    private var result: ((PickResult) -> Unit)? = null
    private var failed: ((Boolean, String) -> Unit)? = null

    /**是否为多选*/
    private var pickerMulti: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (isAdded) {
            startPick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            /**选择结果回调*/
            when (requestCode) {
                1 -> {
                    postCamera()
                }
                2 -> {
                    handleFileResult(data)
                }
                3 -> {
                    if (isCamera(data)) {
                        postCamera()
                    } else {
                        handleFileResult(data)
                    }
                }
            }
        } else {
            onFailed("取消选择")
        }
    }

    override fun onDestroy() {
        deleteUri()
        this.cameraFileUri = null
        this.result = null
        this.failed = null
        super.onDestroy()
    }

    /**设置选择结果执行代码*/
    fun picker(result: (PickResult) -> Unit): PickerFragment {
        this.result = result
        return this
    }

    /**设置选择失败执行代码*/
    fun onFailed(failed: (Boolean, String) -> Unit): PickerFragment {
        this.failed = failed
        return this
    }

    /**开始选择*/
    fun commit() {
        if (fm.findFragmentByTag(TAG) == null) {
            fm.beginTransaction()
                .add(this, TAG)
                .commitAllowingStateLoss()
        } else {
            startPick()
        }
    }

    private fun initConfig() {
        this.cameraFileUri = null
        this.arguments?.also {
            this.pickerType = it.getInt("pickerType", pickerType)
            this.pickerFileType = it.getInt("pickerFileType", pickerFileType)
            this.pickerLimitTime = it.getInt("pickerLimitTime", pickerLimitTime)
            this.pickerMulti = it.getBoolean("pickerMulti", pickerMulti)
            this.pickerTitle = it.getString("pickerTitle", pickerTitle)
            this.pickerMimeTypes = it.getStringArray("pickerMimeTypes") ?: pickerMimeTypes
        }
    }

    /**是否为相机*/
    private fun isCamera(data: Intent?): Boolean {
        return data == null || data.data == null && data.clipData == null
    }

    /**文件选取方式判断*/
    private fun startPick() {
        /**请求授权*/
        if (!checkPermission()) {
            return
        }
        // 初始化配置
        initConfig()

        val chooseIntent: Intent?
        var requestCode: Int
        when (pickerFileType) {
            /**图片*/
            FilePicker.IMAGE -> {
                when (pickerType) {
                    FilePicker.DEVICE -> {
                        requestCode = 1
                        cameraFileUri = createImageUri()
                        chooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                            requestCode = 1
                            it.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri)
                            grantWritePermission(context!!, it, cameraFileUri!!)
                        }
                    }
                    FilePicker.CHOOSE -> {
                        requestCode = 2
                        chooseIntent = createFilePickIntent()
                        chooseIntent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                    }
                    else -> {
                        requestCode = 3
                        chooseIntent = createImageChooserIntent()
                    }
                }
            }
            /**视频*/
            FilePicker.VIDEO -> {
                when (pickerType) {
                    FilePicker.DEVICE -> {
                        requestCode = 1
                        cameraFileUri = createVideoUri()
                        chooseIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                            this.putExtra(MediaStore.EXTRA_DURATION_LIMIT, pickerLimitTime)
                            this.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri)
                            grantWritePermission(context!!, this, cameraFileUri!!)
                        }
                    }
                    FilePicker.CHOOSE -> {
                        requestCode = 2
                        chooseIntent = createFilePickIntent()
                        chooseIntent.setDataAndType(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            "video/*"
                        )
                    }
                    else -> {
                        requestCode = 3
                        chooseIntent = createVideoChooserIntent()
                    }
                }
            }
            /**音频*/
            FilePicker.AUDIO -> {
                when (pickerType) {
                    FilePicker.DEVICE -> {
                        requestCode = 2
                        chooseIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
                    }
                    FilePicker.CHOOSE -> {
                        requestCode = 2
                        chooseIntent = createFilePickIntent()
                        chooseIntent.setDataAndType(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            "audio/*"
                        )
                    }
                    else -> {
                        requestCode = 3
                        chooseIntent = createAudioChooserIntent()
                    }
                }
            }
            /**文件*/
            else -> {
                requestCode = 2
                chooseIntent = createFilePickIntent()
            }
        }
        activity?.packageManager?.let {
            chooseIntent.resolveActivity(it)?.let {
                startActivityForResult(chooseIntent, requestCode)
            }
        }
    }

    /**构建文件选择器 Intent*/
    private fun createFilePickIntent(): Intent {
        val pictureChooseIntent = Intent(Intent.ACTION_GET_CONTENT)
        pictureChooseIntent.type = "*/*"
        pictureChooseIntent.putExtra(Intent.EXTRA_MIME_TYPES, pickerMimeTypes)
        pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, pickerMulti)
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return pictureChooseIntent
    }

    /**构建图片选择器 Intent*/
    private fun createImageChooserIntent(): Intent {
        cameraFileUri = createImageUri()
        val intents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = context!!.packageManager
        val resolves =
            packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (res in resolves) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(packageName, res.activityInfo.name)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, pickerMimeTypes)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, pickerMulti)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri)
            grantWritePermission(context!!, intent, cameraFileUri!!)
            intents.add(intent)
        }
        return Intent.createChooser(createFilePickIntent(), pickerTitle).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
        }
    }

    /**构建视频选择器 Intent*/
    private fun createVideoChooserIntent(): Intent {
        cameraFileUri = createVideoUri()
        val intents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        captureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri)
        captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120000)
        grantWritePermission(context!!, captureIntent, cameraFileUri!!)
        val packageManager = context!!.packageManager
        val resolves =
            packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (res in resolves) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(packageName, res.activityInfo.name)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, pickerMimeTypes)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, pickerLimitTime)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, pickerMulti)
            intents.add(intent)
        }
        Intent.createChooser(createFilePickIntent(), pickerTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
            return it
        }
    }

    /**构建音频选择器Intent*/
    private fun createAudioChooserIntent(): Intent {
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        val packageManager = context!!.packageManager
        val camList =
            packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(packageName, res.activityInfo.name)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, pickerMimeTypes)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, pickerMulti)
            cameraIntents.add(intent)
        }
        Intent.createChooser(createFilePickIntent(), pickerTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
            return it
        }
    }

    /**创建拍照保存路径*/
    private fun createImageUri(): Uri? {
        context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp: String =
            "IMAGE-" + SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(Date())
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    /**创建录像保存路径*/
    private fun createVideoUri(): Uri? {
        val timeStamp: String =
            "VIDEO-" + SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(Date())
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Video.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cv)
    }

    /**删除无效 Uri */
    private fun deleteUri() {
        cameraFileUri?.let {
            val contentResolver = activity!!.contentResolver
            contentResolver.delete(it, null, null)
        }
    }

    /**申请文件读写权限*/
    private fun checkPermission(): Boolean {
        var pass = true
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        for (p in permissions) {
            activity?.let {
                pass = ContextCompat.checkSelfPermission(it, p) == PackageManager.PERMISSION_GRANTED
            }
            if (!pass) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, 0)
                }
                break
            }
        }
        return pass
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        var pass = true
        grantResults.forEach {
            if (PackageManager.PERMISSION_GRANTED != it) {
                pass = false
            }
        }
        if (pass) {
            startPick()
        } else {
            Toast.makeText(activity, "授权失败，无法使用此功能", Toast.LENGTH_LONG).show()
        }
    }

    /**申请文件读写权限*/
    private fun grantWritePermission(context: Context, intent: Intent, uri: Uri) {
        val resInfoList =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    /**获取选择结果*/
    private fun handleFileResult(data: Intent?) {
        deleteUri()
        if (pickerMulti) {
            val imageUris = ArrayList<Uri>()
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    imageUris.add(clipData.getItemAt(i).uri)
                }
            } else {
                data?.data?.let { imageUris.add(it) }
            }
            postMultiFiles(imageUris)
        } else {
            data?.data?.let {
                postSingleFile(it)
            } ?: onFailed("获取文件失败")
        }
    }

    /**传递拍摄文件*/
    private fun postCamera() {
        cameraFileUri?.let {
            postSingleFile(it)
        } ?: onFailed("照片获取失败")
    }

    /**传递选择的单文件*/
    private fun postSingleFile(uri: Uri) {
        postMultiFiles(arrayListOf(uri))
    }

    /**传递选择的多文件*/
    private fun postMultiFiles(uris: List<Uri>) {
        if (uris.isEmpty()) {
            onFailed("未获取到文件")
        } else {
            this.result?.invoke(PickResult(uris))
        }
    }

    private fun onFailed(msg: String) {
        deleteUri()
        this.failed?.invoke(true, msg)
    }

}
