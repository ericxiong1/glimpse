package com.example.glimpse

import android.app.Application
import com.example.glimpse.face.FaceNet
import com.example.glimpse.face.FacialRecognition
import com.example.glimpse.face.ObjectBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GlimpseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.Default).launch {
            ObjectBox.init(this@GlimpseApp)
            FaceNet.getInstance(this@GlimpseApp)
            FacialRecognition.getInstance(this@GlimpseApp)
        }
    }


}
