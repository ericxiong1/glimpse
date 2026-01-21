package com.example.glimpse.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glimpse.BuildConfig
import com.example.glimpse.SharedViewModel
import com.example.glimpse.getCurrentLocation
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.common.MapboxOptions
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.formatter.UnitType
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.utils.internal.toPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.mapbox.navigation.tripdata.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.tripdata.maneuver.model.Maneuver
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/*
FR17 - Get.Next.Turn
FR18 - Update.Route
    Functions to display next turn when navigation widget activated
 */

// Get coordinates of the destination entered by user
suspend fun getDestinationCoordinates(dest: String): Point? {
    return suspendCancellableCoroutine { continuation ->
        val client = MapboxGeocoding.builder()
            .accessToken(BuildConfig.MAPBOX_DOWNLOADS_TOKEN)
            .query(dest)
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
            .build()

        client.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: Response<GeocodingResponse>
            ) {
                val results = response.body()?.features()
                if (!results.isNullOrEmpty()) {
                    val first = results[0].center()
                    continuation.resumeWith(Result.success(first))
                } else {
                    continuation.resumeWith(Result.success(null))
                }
            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                t.printStackTrace()
                continuation.resumeWith(Result.success(null))
            }
        })
    }
}

fun requestRoute(mapboxNavigation: MapboxNavigation, origin: Point, destination: Point) {
    val routeOptions = RouteOptions.builder()
        .applyDefaultNavigationOptions()
        .coordinatesList(listOf(origin, destination))
        .profile(DirectionsCriteria.PROFILE_WALKING)
        .alternatives(false)
        .build()

    mapboxNavigation.requestRoutes(routeOptions,
        object : NavigationRouterCallback {
            override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                Log.e("FAILURE:", reasons[0].message)
            }

            @SuppressLint("MissingPermission")
            override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                mapboxNavigation.setNavigationRoutes(routes)
                mapboxNavigation.startTripSession()
            }
        }
    )
}

@SuppressLint("MissingPermission")
@Composable
fun NavigationWidget(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    var originPoint by remember { mutableStateOf<Point?>(null) }
    var destCoords by remember { mutableStateOf<Point?>(null) }
    var currentManeuver by remember { mutableStateOf<Maneuver?>(null) }
    var currentInstruction by remember { mutableStateOf<String?>("") }
    val currentIcon = remember { mutableStateOf<Int?>(null) }
    var arrived by remember { mutableStateOf(false) }
    MapboxOptions.accessToken = BuildConfig.MAPBOX_DOWNLOADS_TOKEN

    // Define distance formatter options for the turn-by-turn instructions
    val distanceFormatter: DistanceFormatterOptions by lazy {
        DistanceFormatterOptions.Builder(context).unitType(UnitType.METRIC).roundingIncrement(10)
            .build()
    }
    // Create an instance of the Maneuver API to gather step-by-step directions
    val maneuverApi: MapboxManeuverApi by lazy {
        MapboxManeuverApi(
            MapboxDistanceFormatter(
                distanceFormatter
            )
        )
    }
    val mapboxNavigation = remember { MapboxNavigationApp.current() }
    if (mapboxNavigation == null) {
        return
    }

    LaunchedEffect(sharedViewModel.destination) {
        if (sharedViewModel.destination == null) {
            // Destination was cleared
            destCoords = null
            currentInstruction = "No destination set"
            currentIcon.value = null
            currentManeuver = null
            arrived = false
            mapboxNavigation.stopTripSession()
            maneuverApi.cancel()
            mapboxNavigation.setNavigationRoutes(emptyList())
        }

        // Get the current position of the user
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val origin = getCurrentLocation(fusedLocationClient)
        if (origin != null) {
            originPoint = Point.fromLngLat(origin.longitude, origin.latitude)
        }

        // Get the coordinates of the destination
        destCoords = sharedViewModel.destination?.formattedAddress?.let { getDestinationCoordinates(it) }
        currentInstruction = if (destCoords == null) {
            "No destination set"
        } else {
            "Finding route to ${sharedViewModel.destination?.formattedAddress}"
        }

        // Request the navigation route once departure and destination coordinates are known
        originPoint?.let {
            destCoords?.let { it1 -> requestRoute(mapboxNavigation, it, it1) }
        }
    }

    DisposableEffect(Unit) {
        // Initialize listeners to watch the location of the user during navigation
        val locationObserver = object : LocationObserver {
            override fun onNewRawLocation(rawLocation: Location) {
                originPoint = rawLocation.toPoint()
                if (destCoords != null) {
                    // Check if the user has reached the destination
                    val distance = calculateDistance(originPoint!!, destCoords!!)
                    if (distance < 30) {
                        arrived = true
                        mapboxNavigation.stopTripSession()
                        maneuverApi.cancel()
                        mapboxNavigation.setNavigationRoutes(emptyList())
                        originPoint = null
                    }
                }
            }

            override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {}
        }

        mapboxNavigation.registerLocationObserver(locationObserver)
        onDispose { mapboxNavigation.unregisterLocationObserver(locationObserver) }
    }

    DisposableEffect(Unit) {
        // Initialize the listener that monitors where the user is along the navigation route
        val routeProgressObserver =
            RouteProgressObserver { routeProgress ->
                try {
                    currentManeuver = maneuverApi.getManeuvers(routeProgress).value!!.first()
                    currentInstruction =
                        "${currentManeuver?.primary?.text} in ${currentManeuver?.stepDistance?.distanceRemaining?.roundToInt()} m"
                    val resourceID =
                        "direction_${currentManeuver!!.primary.type}_${currentManeuver!!.primary.modifier}".replace(
                            " ",
                            "_"
                        )
                    currentIcon.value =
                        context.resources.getIdentifier(resourceID, "drawable", context.packageName)
                } catch (e: Exception) {
                    Log.e("ERROR:", "Error getting maneuvers: ${e.message}")
                }
            }

        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        onDispose { mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver) }
    }

    // Display the current direction of the navigation route
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (arrived) {
                Text(
                    text = "You have arrived at your destination",
                    style = TextStyle(
                        color = sharedViewModel.hudForegroundColor,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = sharedViewModel.selectedFont.fontResource,
                    )
                )
            } else {
                currentIcon.value?.let { id ->
                    if (id != 0) {
                        Image(
                            painter = painterResource(id = id),
                            contentDescription = "Direction icon",
                            colorFilter = ColorFilter.tint(
                                sharedViewModel.hudForegroundColor,
                                BlendMode.SrcIn
                            ),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                currentInstruction?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = sharedViewModel.hudForegroundColor,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = sharedViewModel.selectedFont.fontResource,
                        )
                    )
                }
            }
        }
    }
}

// Calculate the rough distance between two coordinates on Earth.
// Used to determine when the user is at their destination.
// Implements Haversine formula.
fun calculateDistance(point1: Point, point2: Point): Double {
    val lat1 = Math.toRadians(point1.latitude())
    val lon1 = Math.toRadians(point1.longitude())
    val lat2 = Math.toRadians(point2.latitude())
    val lon2 = Math.toRadians(point2.longitude())

    val dlon = lon2 - lon1
    val dlat = lat2 - lat1

    val a = sin(dlat / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val earthRadiusKm = 6371.0
    return earthRadiusKm * c * 1000 // Return in meters
}