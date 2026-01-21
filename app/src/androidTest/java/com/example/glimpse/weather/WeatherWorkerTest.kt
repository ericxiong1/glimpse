import android.content.Context
import android.location.Location
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.glimpse.fetchLocation
import com.example.glimpse.weather.WeatherWorker
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherWorkerTest {
    private var context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.glimpse.LocationUtilsKt")
    }

    @Test
    fun testSuccessfulWeatherWorker() = runBlocking {
        mockkStatic("com.example.glimpse.LocationUtilsKt")
        coEvery { fetchLocation(any()) } returns Location("test").apply {
            latitude = 43.6548
            longitude = -79.3883
        }

        val worker = TestListenableWorkerBuilder<WeatherWorker>(context).build()
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        assertEquals("Downtown Toronto", prefs.getString("name", ""))
    }

    @Test
    fun testFailureWeatherWorker() = runBlocking {
        mockkStatic("com.example.glimpse.LocationUtilsKt")
        coEvery { fetchLocation(any()) } returns Location("test").apply {
            latitude = 350.00
            longitude = 350.00
        }

        val worker = TestListenableWorkerBuilder<WeatherWorker>(context).build()
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
