package com.example.graph

import android.health.connect.datatypes.units.Temperature

data class Data (
    var temperature: Double,
    var humidity: Double,
    var carbonMonoxide: Double,
    var smoke: Boolean,
    var lpg: Boolean,
    var hydrogen: Double

)