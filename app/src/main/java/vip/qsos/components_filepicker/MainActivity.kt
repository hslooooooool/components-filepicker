package vip.qsos.components_filepicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import vip.qsos.components_filepicker.lib.FilePicker
import vip.qsos.components_filepicker.lib.PickerSource

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        take_photo.setOnClickListener {
            FilePicker.ImageBuilder(supportFragmentManager, 1)
                .setType(PickerSource.ONE)
                .create()
                .start {

                }
                .onFailed {

                }
        }
    }

}
