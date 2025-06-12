package com.korniienko.ozzylogik.domain.model

data class Station(
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val cellId: Int,
    val psc: Int,
    val rat: String,
    val lat: Double,
    val lon: Double
)