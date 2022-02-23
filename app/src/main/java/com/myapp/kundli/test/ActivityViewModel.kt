package com.myapp.kundli.test

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class ActivityViewModel : ViewModel() {
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    var coveredDistanceField= ObservableField("0")
    var latlongField= ObservableField("0")
    var locationMovedField= ObservableField("0")
    var permissionErrorField = ObservableBoolean(true)
}