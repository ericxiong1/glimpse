package com.example.glimpse.face

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.VectorDistanceType
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import java.io.File

/*
FR20 - Recognition.Upload
FR21 - Recognition.Widget
Database models.
 */

@Entity
data class Person(
    @Id var id: Long = 0,
    var name: String = "",
    var information: String = ""
) {
    @Backlink(to = "person")
    lateinit var faces: ToMany<Face>

    fun delete() {
        // Delete all associated faces
        faces.forEach { face ->
            face.delete()
        }

        // Now delete the person
        ObjectBox.store.boxFor(Person::class.java).remove(this)
    }
}

@Entity
data class Face(
    @Id var id: Long = 0,
    var photoPath: String? = null,
    @HnswIndex(
        dimensions = 512,
        distanceType = VectorDistanceType.COSINE
    ) var faceEmbedding: FloatArray = floatArrayOf()
) {
    lateinit var person: ToOne<Person>

    fun delete() {
        photoPath?.let {
            val file = File(it)
            if (file.exists()) {
                file.delete()
            }
        }
        ObjectBox.store.boxFor(Face::class.java).remove(this)
    }

    fun getPhotoUri(context: Context): Uri? {
        photoPath?.let { path ->
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                File(path)
            )
        }
        return null
    }

}

