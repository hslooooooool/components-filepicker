# Android原生文件选择封装

TAG = [![](https://www.jitpack.io/v/hslooooooool/components-filepicker.svg)](https://www.jitpack.io/#hslooooooool/components-filepicker)

依赖：
```groovy
	allprojects {
		repositories {
			maven { url 'https://www.jitpack.io' }
		}
	}
```

```groovy
	dependencies {
	        implementation 'com.github.hslooooooool:components-filepicker:Tag'
	}
```

## 使用说明

### 图片拍照
```kotlin
            FilePicker.ImageBuilder(supportFragmentManager)
                .setType(FilePicker.DEVICE)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { error, msg ->
                    // 失败回调
                }
                .commit()
```
### 图片选择，单选
```kotlin
            FilePicker.ImageBuilder(supportFragmentManager)
                .setMulti(false)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 图片多选
```kotlin
            FilePicker.ImageBuilder(supportFragmentManager)
                .setMulti(true)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                     //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 图片选择或拍照，选择时可以多选
```kotlin
            FilePicker.ImageBuilder(supportFragmentManager)
                .setMulti(true)
                .setType(FilePicker.CHOOSER)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 视频拍摄
```kotlin
            FilePicker.VideoBuilder(supportFragmentManager)
                .setType(FilePicker.DEVICE)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 视频选择,单选
```kotlin
            FilePicker.VideoBuilder(supportFragmentManager)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 视频拍摄或选择
```kotlin
            FilePicker.VideoBuilder(supportFragmentManager)
                .setType(FilePicker.CHOOSER)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 音频录制
```kotlin
            FilePicker.AudioBuilder(supportFragmentManager)
                .setType(FilePicker.DEVICE)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 音频选择,单选
```kotlin
            FilePicker.AudioBuilder(supportFragmentManager)
                .setType(FilePicker.CHOOSE)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 音频录制或选择
```kotlin
            FilePicker.AudioBuilder(supportFragmentManager)
                .setType(FilePicker.CHOOSER)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 文件选择,单选
```kotlin
            FilePicker.FileBuilder(supportFragmentManager)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```
### 文件选择,多选
```kotlin
            FilePicker.FileBuilder(supportFragmentManager)
                .setMulti(true)
                .create()
                .picker {
                    //  结果回调
                }
                .onFailed { _, msg ->
                    // 失败回调
                }
                .commit()
```

## 选择配置

可配置项见 [FilePicker](/lib/src/main/java/vip/qsos/components_filepicker/lib/FilePicker.kt) 下 `Builder`
```kotlin
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
```