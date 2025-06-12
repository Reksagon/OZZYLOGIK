package com.korniienko.ozzylogik.data.repository

import com.google.android.gms.maps.model.LatLngBounds
import com.korniienko.ozzylogik.data.local.LocalDataSource
import com.korniienko.ozzylogik.domain.model.MapResult
import com.korniienko.ozzylogik.domain.model.Station
import com.korniienko.ozzylogik.domain.repository.StationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationRepositoryImpl(
    private val local: LocalDataSource
) : StationRepository {

    override suspend fun getDataForMap(bounds: LatLngBounds, zoom: Float): MapResult {
        return when {
            zoom < 8f -> {
                val clusters = local.fetchClustersInBounds(bounds, 1.0)
                MapResult.Clusters(clusters)
            }
            zoom < 12f -> {
                val clusters = local.fetchClustersInBounds(bounds, 0.2)
                MapResult.Clusters(clusters)
            }
            else -> {
                val stations = local.fetchStationsInBounds(bounds, 1000)
                MapResult.Stations(stations)
            }
        }
    }
}
