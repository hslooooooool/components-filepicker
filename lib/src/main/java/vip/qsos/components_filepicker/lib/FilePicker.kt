package vip.qsos.components_filepicker.lib

import androidx.annotation.IntDef
import androidx.fragment.app.FragmentManager

class FilePicker {

    @IntDef(value = [DEVICE, CHOOSE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {
        /**设备*/
        const val DEVICE: Int = 1

        /**选择*/
        const val CHOOSE: Int = 2

        /**获取 PickerFragment 实例*/
        private fun getInstance(fm: FragmentManager): PickerFragment {
            var rxImagePickerFragment = fm.findFragmentByTag(PickerFragment.TAG) as PickerFragment?
            if (rxImagePickerFragment == null) {
                rxImagePickerFragment = PickerFragment()
                fm.beginTransaction().add(rxImagePickerFragment, PickerFragment.TAG).commit()
            }
            rxImagePickerFragment.arguments

            return rxImagePickerFragment
        }
    }

    interface Builder {
        val fm: FragmentManager
        val size: Int
        fun setType(@Type type: Int): Builder
        fun create(): PickerFragment
    }

    class ImageBuilder(override val fm: FragmentManager, override val size: Int = 1) : Builder {
        private var type: Int = PickerSource.ONE

        override fun setType(@Type type: Int): ImageBuilder {
            this.type = type
            return this
        }

        override fun create(): PickerFragment {
            return getInstance(fm)
        }

    }

    class VideoBuilder(override val fm: FragmentManager, override val size: Int = 1) : Builder {
        private var type: Int = PickerSource.ONE

        override fun setType(@Type type: Int): VideoBuilder {
            this.type = type
            return this
        }

        override fun create(): PickerFragment {
            return getInstance(fm)
        }
    }

    class AudioBuilder(override val fm: FragmentManager, override val size: Int = 1) : Builder {
        private var type: Int = PickerSource.ONE

        override fun setType(@Type type: Int): AudioBuilder {
            this.type = type
            return this
        }

        override fun create(): PickerFragment {
            return getInstance(fm)
        }
    }

    class FileBuilder(override val fm: FragmentManager, override val size: Int = 1) : Builder {
        private var type: Int = PickerSource.ONE

        override fun setType(@Type type: Int): FileBuilder {
            this.type = type
            return this
        }

        override fun create(): PickerFragment {
            return getInstance(fm)
        }
    }


}