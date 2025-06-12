package com.korniienko.ozzylogik.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLngBounds
import com.korniienko.ozzylogik.data.local.LocalDataSource
import com.korniienko.ozzylogik.data.repository.StationRepositoryImpl
import com.korniienko.ozzylogik.domain.model.ClusterItem
import com.korniienko.ozzylogik.domain.model.MapResult
import com.korniienko.ozzylogik.domain.model.Station
import com.korniienko.ozzylogik.domain.usecase.FetchMapDataUseCase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StationRepositoryImpl(LocalDataSource(app))
    private val useCase = FetchMapDataUseCase(repo)

    private val _clusters = MutableLiveData<List<ClusterItem>>()
    val clusters: LiveData<List<ClusterItem>> = _clusters

    private val _stations = MutableLiveData<List<Station>>()
    val stations: LiveData<List<Station>> = _stations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun load(bounds: LatLngBounds, zoom: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = useCase(bounds, zoom)) {
                is MapResult.Clusters -> _clusters.postValue(result.items)
                is MapResult.Stations -> _stations.postValue(result.items)
            }
        }
    }
}
