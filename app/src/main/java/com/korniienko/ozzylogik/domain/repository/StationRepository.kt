package com.korniienko.ozzylogik.domain.repository

import com.google.android.gms.maps.model.LatLngBounds
import com.korniienko.ozzylogik.domain.model.MapResult


interface StationRepository {
    suspend fun getDataForMap(bounds: LatLngBounds, zoom: Float): MapResult
}
