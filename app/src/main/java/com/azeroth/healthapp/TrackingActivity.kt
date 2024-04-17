package com.azeroth.healthapp

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.azeroth.healthapp.databinding.ActivityTrackingBinding
import com.azeroth.healthapp.fragments.ChartFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityTrackingBinding
    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var stepsToday = 0
    private val locationPermissionCode = 2
    private var currentLatLng: LatLng? = null
    private var stepCount = 0

    private val polyList: MutableList<LatLng> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment == null) {
            Toast.makeText(this, "Null mapFragment", Toast.LENGTH_SHORT).show()
        }
        mapFragment?.getMapAsync(this)

        setFragment(ChartFragment())
        stepsToday = getValueForToday(this@TrackingActivity, 0)
//        calculateDistance()

        retrieveValue(this@TrackingActivity)

        countNewSteps()
        getLocationWithGPS()
        getCurrentLocation()

    }

    private fun getValueForToday(constext: Context, i: Int): Int {

        return 0
    }

    private fun calculateCalorie() {

    }

    private fun countNewSteps() {
        stepsToday++
    }


    override fun onStop() {
        saveStepsValue(stepsToday, this@TrackingActivity)

        super.onStop()
    }

    private fun retrieveValue(context: Context) {
        // Retrieve values for the last 6 days
//        val valuesForLast6Days = DailyDataManager.getValuesForLastNDays(context, 7)

    }

    private fun getLocationWithGPS() {
        locationManager =
            applicationContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@TrackingActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this)
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            Log.d(ContentValues.TAG, "onLocation:$location")

            if (location != null) {

                if (location.accuracy >= 5) {
                    Toast.makeText(
                        applicationContext,
                        "Launch App in Open Area\nFor Better Accuracy",
                        Toast.LENGTH_LONG
                    ).show()
                }

                currentLatLng = LatLng(location.latitude, location.longitude)


//                polyList.add(currentLatLng!!)
//                if (mainMarker == null) {
//                    val m = MarkerOptions().position(currentLatLng!!).title("Current Location")
//                        .icon(
//                            BitmapDescriptorFactory.fromResource(R.drawable.sherlock_holmes_1)
//                        )
//                    mainMarker = mMap?.addMarker(m)
//                }
//
//                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 18f))
//                updateInitialMarkers(currentLatLng!!)
            }

        } else {
            Log.d(ContentValues.TAG, "getCurrentLocation: ")
        }
    }

    private fun saveStepsValue(steps: Int, context: Context) {
//        DailyDataManager.saveValueForToday(context, steps)
    }

    private fun setFragment(fragment: Fragment) {
        if (!supportFragmentManager.isDestroyed) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayoutMain, fragment)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth in km
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a =
            sin(dLat / 2) * sin(dLat / 2) + cos(deg2rad(lat1)) * cos(
                deg2rad(lat2)
            ) * sin(
                dLon / 2
            ) * sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))
        val d = R * c * 1000 // Distance in meters
        return d
    }

    private fun calculateSteps(mutableList: MutableList<LatLng>): String {
        try {
            val currentLat = mutableList[mutableList.size - 1]
            val oldLat = mutableList[mutableList.size - 2]

            val steps = calculateDistance(
                currentLat.latitude,
                currentLat.longitude,
                oldLat.latitude,
                oldLat.longitude,
            )

            Log.d(ContentValues.TAG, "calculateSteps: $steps")
            return (Math.round(steps)).toString()
        } catch (exc: ArrayIndexOutOfBoundsException) {
            return "0"
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    override fun onLocationChanged(p0: Location) {


        Log.d(ContentValues.TAG, "onLocationResult: $p0")

//        if (p0.accuracy <= 9) {


        currentLatLng = LatLng(p0.latitude, p0.longitude)


        Log.d(ContentValues.TAG, "onLocationChanged: $currentLatLng")
//        polyList.add(currentLatLng!!)
//
//        drawPolyLineOnMap(polyList, mMap!!)
//
//        updateCurrentMarkerPosition(currentLatLng!!)
        val stepsChanged: Int = calculateSteps(polyList).toInt()
        stepCount += stepsChanged
        binding.txtStepsCount.text = stepCount.toString()

//        val dis = landmarkXY?.latitude?.let {
//            landmarkXY?.longitude?.let { it1 ->
//                calculateDistance(
//                    currentLatLng!!.latitude,
//                    currentLatLng!!.longitude,
//                    it,
//                    it1
//                )
//            }
//        }

//        Log.d(ContentValues.TAG, "onLocationResult Distance: $dis")

//        if (dis != null) {
//            if (dis < 6) {
//                updateLandmarkMarker()
//            }
//        }

    }
}