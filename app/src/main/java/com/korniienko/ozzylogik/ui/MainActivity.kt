package com.korniienko.ozzylogik.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.korniienko.ozzylogik.R
import com.korniienko.ozzylogik.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var vb: ActivityMainBinding
    private lateinit var map: GoogleMap
    private val vm: MapViewModel by viewModels()
    private val manualMarkers = mutableListOf<Marker>()
    private val clusterMarkers = mutableListOf<Marker>()
    private var lastMode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(vb.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        setContentView(vb.root)


        vm.isLoading.observe(this) { loading ->
            vb.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        (supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment)
            .getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnCameraIdleListener {
            val bounds = map.projection.visibleRegion.latLngBounds
            val zoom   = map.cameraPosition.zoom
            vm.load(bounds, zoom)
        }

        vm.clusters.observe(this) { clusters ->
            if (lastMode != "cluster") clearManualMarkers()
            lastMode = "cluster"
            clearClusterMarkers()
            clusters.forEach { cluster ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(cluster.lat, cluster.lon))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("${cluster.count} points")
                )
                marker?.let { clusterMarkers.add(it) }
            }
        }

        vm.stations.observe(this) { stations ->
            if (lastMode != "station") clearClusterMarkers()
            lastMode = "station"
            clearManualMarkers()
            stations.forEach { s ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(s.lat, s.lon))
                        .title("ID:${s.cellId}")
                        .snippet("MCC:${s.mcc} MNC:${s.mnc} RAT:${s.rat}")
                )
                marker?.let { manualMarkers.add(it) }
            }
        }
    }

    private fun clearManualMarkers() {
        manualMarkers.forEach { it.remove() }
        manualMarkers.clear()
    }
    private fun clearClusterMarkers() {
        clusterMarkers.forEach { it.remove() }
        clusterMarkers.clear()
    }
}
