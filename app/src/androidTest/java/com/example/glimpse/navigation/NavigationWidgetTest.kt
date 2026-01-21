package com.example.glimpse.navigation

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.core.MapboxNavigation
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Test

class NavigationWidgetTest {
    @Test
    fun testGetDestinationCoordinates() = runBlocking {
        val mockGeocoding = mockk<MapboxGeocoding>(relaxed = true)
        val mockResponse = mockk<GeocodingResponse>()
        val point = Point.fromLngLat(-77.050157, 38.889264)
        every { mockResponse.features() } returns listOf(mockk {
            every { center() } returns point
        })

        coEvery { mockGeocoding.enqueueCall(any()) } answers {
            val callback = arg<retrofit2.Callback<GeocodingResponse>>(0)
            callback.onResponse(mockk(), retrofit2.Response.success(mockResponse))
        }

        val coordinates = getDestinationCoordinates("2 Lincoln Memorial Circle SW")
        assertNotNull(coordinates)
        assertEquals(coordinates, point)
    }

    @Test
    fun testGetRoute() {
        val mapboxNavigation = mockk<MapboxNavigation>(relaxed = true)
        val origin = Point.fromLngLat(-113.5302589, 53.5270692)
        val destination = Point.fromLngLat(-113.52968209821846, 53.5265708)
        val callbackSlot = slot<NavigationRouterCallback>()

        every {
            mapboxNavigation.requestRoutes(any(), capture(callbackSlot))
        } answers {
            val fakeRoute = mockk<NavigationRoute>()
            callbackSlot.captured.onRoutesReady(listOf(fakeRoute), "")
            1L
        }

        requestRoute(mapboxNavigation, origin, destination)

        verify { mapboxNavigation.requestRoutes(any(), any()) }
        verify {
            mapboxNavigation.setNavigationRoutes(any())
            mapboxNavigation.startTripSession()
        }
    }

    @Test
    fun testCalculateDistance() {
        val point1 = Point.fromLngLat(-113.4937, 53.5461) // Edmonton
        val point2 = Point.fromLngLat(-114.0719, 51.0447) // Calgary

        val distance = calculateDistance(point1, point2)

        val expectedDistance = 280000.0
        assertEquals(expectedDistance, distance, 5000.0)
    }
}
