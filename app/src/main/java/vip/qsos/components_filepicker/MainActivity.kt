package vip.qsos.components_filepicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import vip.qsos.components_filepicker.lib.FileConverters
import vip.qsos.components_filepicker.lib.FilePicker
import vip.qsos.components_filepicker.lib.PickResult

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        take_image.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setSize(1)
                .setType(FilePicker.CHOOSER)
                .create()
                .picker {
                    getBitmap(it)
                }
                .onFailed { _, _ ->

                }
                .commit()
        }
        take_photo.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setSize(1)
                .setType(FilePicker.DEVICE)
                .create()
                .picker {

                }
                .onFailed { _, _ ->

                }
                .commit()
        }
        take_album.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setSize(1)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {

                }
                .onFailed { _, _ ->

                }
                .commit()
        }
        take_album_multi.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager)
                .setSize(3)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {

                }
                .onFailed { _, _ ->

                }
                .commit()
        }
    }

    private fun getBitmap(result: PickResult) {
        CoroutineScope(Job()).launch {
            FileConverters.uriToBitmap(this@MainActivity, result.data[0]) {
                println(it == null)
            }
        }

    }
}
