import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.glimpse.ActionCard
import com.example.glimpse.face.ObjectBox
import com.example.glimpse.face.Person
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

/*
FR20 - Recognition.Upload
UI components for viewing database of people and their faces as well as removal of users.
*/

@Composable
fun PersonListScreen() {
    val persons =
        remember { mutableStateListOf(*ObjectBox.store.boxFor(Person::class.java).all.toTypedArray()) }
    val context = LocalContext.current
    LazyColumn {
        items(persons) { person ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Show the first face image (if available)
                    person.faces.firstOrNull()?.let { face ->
                        face.getPhotoUri(context)?.let { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    // Person details
                    Column(
                        modifier = Modifier.weight(1f) // Take up remaining space
                    ) {
                        Text(person.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(person.information)
                    }

                    // Delete Button (X Icon)
                    IconButton(
                        onClick = {
                            person.delete()
                            persons.remove(person) // Update UI after deletion
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete"+person.name,
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ManagerScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionCard("Upload New Face", Icons.Default.Face) {
            navController.navigate("face_upload_screen")
        }

        ActionCard("View Existing Users", Icons.AutoMirrored.Filled.List) {
            navController.navigate("person_list_screen")
        }
    }
}
