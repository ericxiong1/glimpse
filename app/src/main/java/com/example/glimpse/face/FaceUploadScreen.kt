import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.glimpse.face.Face
import com.example.glimpse.face.FacialRecognition
import com.example.glimpse.face.ObjectBox
import com.example.glimpse.face.Person
import com.example.glimpse.utility.uriToBitmap
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

/*
FR20 - Recognition.Upload
Form to upload person to database. Converts uploaded face to embedding and saves to the database
along with name and information. Does not let user submit a photo with more or less than one face.
 */

@Composable
fun FaceUploadScreen(navController: NavController, snackbarHostState: SnackbarHostState) {
    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var photo by remember { mutableStateOf<Bitmap?>(null) }
    var submitted by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Create file for image
    val file = remember {
        File(context.filesDir, "${UUID.randomUUID()}.jpg")
    }

    DisposableEffect(Unit) {
        onDispose {
            // Delete file if it was not submitted
            if (file.exists() && !submitted) {
                file.delete()
            }
        }
    }

    val cameraUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // Launcher for gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            copyImageToInternalStorage(context, it, file)
            imageUri = Uri.parse(
                Uri.fromFile(file).toString() + "?timestamp=${System.currentTimeMillis()}"
            )
            photo = uriToBitmap(context, imageUri!!)
        }
    }

    // Launcher for camera capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            imageUri = Uri.parse(cameraUri.toString() + "?timestamp=${System.currentTimeMillis()}")
            photo = uriToBitmap(context, imageUri!!)
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Upload Face", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Name Input
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        // Additional Information Input
        OutlinedTextField(
            value = info,
            onValueChange = { info = it },
            label = { Text("Additional Information") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            maxLines = 3
        )

        // Buttons for Selecting Image
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Choose from Gallery")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    cameraLauncher.launch(cameraUri)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Capture with Camera")
            }
        }

        // Show selected/captured image
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .testTag("imageTestTag")
            )
        }

        val scope = rememberCoroutineScope()
        // Submit Button
        Button(
            enabled = name.isNotEmpty() && imageUri != null && photo != null,
            onClick = {
                scope.launch {
                    val embeddingResult =
                        FacialRecognition.getInstance(context).processBitmap(photo!!)
                    when {
                        embeddingResult.embedding == null -> {
                            snackbarHostState.showSnackbar("No faces detected")
                        }

                        embeddingResult.multipleFaces -> {
                            snackbarHostState.showSnackbar("Multiple faces detected")
                        }

                        else -> {
                            // Face found, exit form and save to database
                            submitted = true

                            // Store person in database
                            val face = Face(
                                photoPath = file.toString(),
                                faceEmbedding = embeddingResult.embedding
                            )
                            ObjectBox.store.boxFor(Face::class).put(face)
                            val person = Person(name = name, information = info)
                            person.faces.add(face)
                            ObjectBox.store.boxFor(Person::class).put(person)

                            // Exit component
                            navController.popBackStack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Submit", color = Color.White)
        }
    }
}

private fun copyImageToInternalStorage(context: Context, uri: Uri, file: File) {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return
    file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
}