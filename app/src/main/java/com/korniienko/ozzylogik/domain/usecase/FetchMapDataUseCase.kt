package com.korniienko.ozzylogik.domain.usecase

import com.google.android.gms.maps.model.LatLngBounds
import com.korniienko.ozzylogik.domain.model.MapResult
import com.korniienko.ozzylogik.domain.repository.StationRepository

class FetchMapDataUseCase(
    private val repo: StationRepository
) {
    suspend operator fun invoke(bounds: LatLngBounds, zoom: Float): MapResult {
        return repo.getDataForMap(bounds, zoom)
    }
}
