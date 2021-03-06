package vip.qsos.components_filepicker.lib

import android.os.Bundle
import androidx.annotation.IntDef
import androidx.fragment.app.FragmentManager

class FilePicker {

    @IntDef(value = [DEVICE, CHOOSE, CHOOSER])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    @IntDef(value = [IMAGE, VIDEO, AUDIO, FILE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class FileType

    companion object {
        const val DEVICE: Int = 1
        const val CHOOSE: Int = 2
        const val CHOOSER: Int = 3

        const val IMAGE: Int = 0
        const val VIDEO: Int = 1
        const val AUDIO: Int = 2
        const val FILE: Int = 3

        /**获取 PickerFragment 实例*/
        private fun getInstance(fm: FragmentManager): PickerFragment {
            var pickerFragment = fm.findFragmentByTag(PickerFragment.TAG) as PickerFragment?
            if (pickerFragment == null) {
                pickerFragment = PickerFragment(fm)
            }
            return pickerFragment
        }
    }

    interface Builder {
        val fm: FragmentManager

        fun getFileType(): Int

        /**设置录制时长，秒。注意某些手机和系统版本不支持时长设置*/
        fun setLimitTime(limitTime: Int): Builder

        /**设置选择时多窗口标题*/
        fun setTitle(title: String): Builder

        /**设置可选文件mime类型*/
        fun setMimeTypes(mimeTypes: Array<String>): Builder

        /**设置是否为多选*/
        fun setMulti(multi: Boolean): Builder

        /**设置选择类型*/
        fun setType(@Type type: Int): Builder

        /**选择结果回调*/
        fun picker(result: (PickResult) -> Unit): Builder

        /**选择取消与失败回调*/
        fun onFailed(failed: (Boolean, String) -> Unit): Builder

        /**创建并开启选择*/
        fun create(): PickerFragment
    }

    abstract class AbsBuilder : Builder {
        private var pickerType: Int = CHOOSER
        private var pickerMimeTypes: Array<String> = arrayOf("*/*")
        private var pickerMulti: Boolean = false
        private var pickerTitle: String = "文件选择"
        private var pickerLimitTime: Int = 20

        private var result: (PickResult) -> Unit = {}
        private var failed: ((Boolean, String) -> Unit)? = null

        override fun picker(result: (PickResult) -> Unit): Builder {
            this.result = result
            return this
        }

        override fun onFailed(failed: (Boolean, String) -> Unit): Builder {
            this.failed = failed
            return this
        }

        override fun setTitle(title: String): Builder {
            this.pickerTitle = title
            return this
        }

        override fun setMulti(multi: Boolean): Builder {
            this.pickerMulti = multi
            return this
        }

        override fun setType(@Type type: Int): Builder {
            this.pickerType = type
            return this
        }

        override fun setMimeTypes(mimeTypes: Array<String>): Builder {
            this.pickerMimeTypes = mimeTypes
            return this
        }

        override fun setLimitTime(limitTime: Int): Builder {
            this.pickerLimitTime = limitTime
            return this
        }

        override fun create(): PickerFragment {
            val f = getInstance(fm)
            val bundle = Bundle()
            bundle.putInt("pickerType", pickerType)
            bundle.putInt("pickerLimitTime", pickerLimitTime)
            bundle.putInt("pickerFileType", getFileType())
            bundle.putBoolean("pickerMulti", pickerMulti)
            bundle.putString("pickerTitle", pickerTitle)
            bundle.putStringArray("pickerMimeTypes", pickerMimeTypes)
            f.arguments = bundle
            f.picker(result)
            failed?.let {
                f.onFailed(it)
            }
            return f
        }
    }

    class ImageBuilder(override val fm: FragmentManager) : AbsBuilder() {
        init {
            setTitle("图片选择")
            setMimeTypes(arrayOf("image/*"))
        }

        override fun getFileType(): Int = IMAGE

    }

    class VideoBuilder(override val fm: FragmentManager) : AbsBuilder() {
        init {
            setTitle("视频选择")
            setMimeTypes(arrayOf("video/*"))
        }

        override fun getFileType(): Int = VIDEO

    }

    class AudioBuilder(override val fm: FragmentManager) : AbsBuilder() {
        init {
            setTitle("音频选择")
            setMimeTypes(arrayOf("audio/*"))
        }

        override fun getFileType(): Int = AUDIO

    }

    class FileBuilder(override val fm: FragmentManager) : AbsBuilder() {
        init {
            setTitle("文件选择")
            setType(CHOOSE)
        }

        override fun getFileType(): Int = FILE

    }

}