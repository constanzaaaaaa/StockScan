package com.example.stockscan.ui.view

// Obtiene la ubicación actual y la muestra en un mapa Mapbox, siguiendo el ejemplo GeoMapbox del profesor.

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.Marker
import kotlinx.coroutines.tasks.await

@OptIn(MapboxExperimental::class)
@SuppressLint("MissingPermission")
@Composable
fun UbicacionView(navController: NavController) {
    val context = LocalContext.current

    // Inicializa el cliente de ubicación, igual que en GetLocationScreen del profesor.
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Guarda las coordenadas obtenidas y el mensaje que se muestra mientras se solicita ubicación.
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var locationMessage by remember { mutableStateOf("Solicitando permisos de ubicación...") }
    var permisosConcedidos by remember { mutableStateOf(false) }
    var recarga by remember { mutableIntStateOf(0) }

    // Obtiene una ubicación actual con prioridad alta, como en el ejemplo del profesor.
    suspend fun fetchCurrentLocation() {
        locationMessage = "Buscando ubicación..."
        try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            if (location != null) {
                userLocation = Pair(location.latitude, location.longitude)
                locationMessage = "Ubicación obtenida exitosamente."
            } else {
                locationMessage = "No se pudo obtener la ubicación. Intente de nuevo."
            }
        } catch (e: Exception) {
            locationMessage = "Error: ${e.message ?: "No se pudo obtener la ubicación."}"
        }
    }

    // Solicita los permisos de ubicación fina y aproximada mediante el launcher de Compose.
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permisosConcedidos =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (!permisosConcedidos) {
                locationMessage = "Permisos de ubicación denegados."
            }
        }
    )

    // Solicita permisos al entrar en la pantalla.
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Obtiene o actualiza la ubicación cuando se conceden permisos o se pulsa actualizar.
    LaunchedEffect(permisosConcedidos, recarga) {
        if (permisosConcedidos) {
            fetchCurrentLocation()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Mi ubicación")

        userLocation?.let { (latitude, longitude) ->
            MapboxMap(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                mapViewportState = rememberMapViewportState {
                    setCameraOptions {
                        zoom(18.0)
                        center(Point.fromLngLat(longitude, latitude))
                        pitch(0.0)
                        bearing(0.0)
                    }
                }
            ) {
                Marker(
                    point = Point.fromLngLat(longitude, latitude)
                )
            }
        } ?: Text(text = locationMessage)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (permisosConcedidos) {
                    recarga++
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        ) {
            Text(text = "Actualizar ubicación / solicitar permisos")
        }

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Volver")
        }
    }
}
