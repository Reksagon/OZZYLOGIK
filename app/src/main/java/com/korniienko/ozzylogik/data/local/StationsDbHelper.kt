package com.korniienko.ozzylogik.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class StationsDbHelper(ctx: Context) :
    SQLiteOpenHelper(ctx, "stations.db", null, 1) {

    private val dbPath = ctx.getDatabasePath("stations.db").path

    init {
        if (!ctx.getDatabasePath("stations.db").exists()) {
            ctx.assets.open("stations.db").use { inp ->
                FileOutputStream(dbPath).use { out ->
                    inp.copyTo(out)
                }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) { /* ничего */ }
    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) { /* ничего */ }
}