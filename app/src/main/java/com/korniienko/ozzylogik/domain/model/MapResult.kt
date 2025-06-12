package com.korniienko.ozzylogik.domain.model

sealed class MapResult {
    data class Clusters(val items: List<ClusterItem>) : MapResult()
    data class Stations(val items: List<Station>) : MapResult()
}
