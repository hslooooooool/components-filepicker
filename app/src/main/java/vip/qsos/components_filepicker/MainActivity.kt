package vip.qsos.components_filepicker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import vip.qsos.components_filepicker.lib.FileConverters
import vip.qsos.components_filepicker.lib.FilePicker

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 拍照
        take_image.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setType(FilePicker.DEVICE)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToBitmap(this@MainActivity, it.data[0]) {
                            it?.let { take_image_result.setImageBitmap(it) }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 单选图片选择
        take_image_choose.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setMulti(false)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToBitmap(this@MainActivity, it.data[0]) {
                            it?.let { take_image_choose_result.setImageBitmap(it) }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 拍照或图片选择
        take_image_chooser.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setMulti(true)
                .setType(FilePicker.CHOOSER)
                .create()
                .picker {
                    MainScope().launch {
                        it.data.forEachIndexed { index, uri ->
                            FileConverters.uriToBitmap(this@MainActivity, uri) {
                                it?.let {
                                    when (index) {
                                        0 -> take_image_chooser_result1.setImageBitmap(it)
                                        1 -> take_image_chooser_result2.setImageBitmap(it)
                                    }
                                }
                            }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 多选图片选择
        take_image_multi.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setMulti(true)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                    MainScope().launch {
                        it.data.forEachIndexed { index, uri ->
                            FileConverters.uriToBitmap(this@MainActivity, uri) {
                                it?.let {
                                    when (index) {
                                        0 -> take_image_multi_result1.setImageBitmap(it)
                                        1 -> take_image_multi_result2.setImageBitmap(it)
                                        2 -> take_image_multi_result3.setImageBitmap(it)
                                    }
                                }
                            }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 视频拍摄
        take_video.setOnClickListener {
            FilePicker.VideoBuilder(supportFragmentManager)
                .setType(FilePicker.DEVICE)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToFile(this@MainActivity, it.data[0]) {
                            it?.let { take_video_result.text = it.path }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 单选视频选择
        take_video_choose.setOnClickListener {
            FilePicker.VideoBuilder(supportFragmentManager)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToFile(this@MainActivity, it.data[0]) {
                            it?.let { take_video_choose_result.text = it.path }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 音频录制
        take_audio.setOnClickListener {
            FilePicker.AudioBuilder(supportFragmentManager)
                .setType(FilePicker.DEVICE)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToFile(this@MainActivity, it.data[0]) {
                            it?.let { take_audio_result.text = it.path }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 单选音频选择
        take_audio_choose.setOnClickListener {
            FilePicker.AudioBuilder(supportFragmentManager)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToFile(this@MainActivity, it.data[0]) {
                            it?.let { take_audio_choose_result.text = it.path }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 音频录制或选择
        take_audio_chooser.setOnClickListener {
            FilePicker.AudioBuilder(supportFragmentManager)
                .setType(FilePicker.CHOOSER)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToFile(this@MainActivity, it.data[0]) {
                            it?.let { take_audio_chooser_result.text = it.path }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 单选文件选择
        take_file.setOnClickListener {
            FilePicker.FileBuilder(supportFragmentManager)
                .create()
                .picker {
                    MainScope().launch {
                        FileConverters.uriToFile(this@MainActivity, it.data[0]) {
                            it?.let { take_file_result.text = it.absolutePath }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
        // 多选文件选择
        take_file_multi.setOnClickListener {
            FilePicker.FileBuilder(supportFragmentManager)
                .setMulti(true)
                .create()
                .picker {
                    MainScope().launch {
                        it.data.forEachIndexed { index, uri ->
                            FileConverters.uriToFile(this@MainActivity, uri) { file ->
                                file?.path?.let {
                                    when (index) {
                                        0 -> take_file_multi_result1.text = it
                                        1 -> take_file_multi_result2.text = it
                                        2 -> take_file_multi_result3.text = it
                                    }
                                }
                            }
                        }
                    }
                }
                .onFailed { _, msg ->
                    toast(msg)
                }
                .commit()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
