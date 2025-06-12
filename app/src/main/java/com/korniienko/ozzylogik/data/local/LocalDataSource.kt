package com.korniienko.ozzylogik.data.local

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLngBounds
import com.korniienko.ozzylogik.domain.model.ClusterItem
import com.korniienko.ozzylogik.domain.model.Station


class LocalDataSource(context: Context) {
    private val db = StationsDbHelper(context).readableDatabase

    fun fetchClustersInBounds(bounds: LatLngBounds, tileSize: Double): List<ClusterItem> {
        val sql = """
            SELECT 
                ROUND(lat / ?) * ? AS cluster_lat, 
                ROUND(lon / ?) * ? AS cluster_lon, 
                COUNT(*) as count
            FROM cell_data
            WHERE lat BETWEEN ? AND ? AND lon BETWEEN ? AND ?
            GROUP BY cluster_lat, cluster_lon
            LIMIT 1000
        """.trimIndent()

        val args = arrayOf(
            tileSize, tileSize, tileSize, tileSize,
            bounds.southwest.latitude, bounds.northeast.latitude,
            bounds.southwest.longitude, bounds.northeast.longitude
        ).map { it.toString() }.toTypedArray()

        val out = mutableListOf<ClusterItem>()
        db.rawQuery(sql, args).use { cur ->
            while (cur.moveToNext()) {
                val lat   = cur.getDouble(0)
                val lon   = cur.getDouble(1)
                val count = cur.getInt(2)
                out += ClusterItem(lat, lon, count)
            }
        }
        return out
    }

    fun fetchStationsInBounds(bounds: LatLngBounds, limit: Int = 1000): List<Station> {
        val sql = """
            SELECT mcc, mnc, lac, cellId, psc, rat, lat, lon
            FROM cell_data
            WHERE lat BETWEEN ? AND ? AND lon BETWEEN ? AND ?
            LIMIT $limit
        """.trimIndent()
        val args = arrayOf(
            bounds.southwest.latitude, bounds.northeast.latitude,
            bounds.southwest.longitude, bounds.northeast.longitude
        ).map { it.toString() }.toTypedArray()

        val out = mutableListOf<Station>()
        db.rawQuery(sql, args).use { cur ->
            while (cur.moveToNext()) {
                out += Station(
                    mcc    = cur.getInt(0),
                    mnc    = cur.getInt(1),
                    lac    = cur.getInt(2),
                    cellId = cur.getInt(3),
                    psc    = cur.getInt(4),
                    rat    = cur.getString(5),
                    lat    = cur.getDouble(6),
                    lon    = cur.getDouble(7)
                )
            }
        }
        return out
    }
}
