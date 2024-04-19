package com.azeroth.healthapp

import android.Manifest
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.azeroth.healthapp.databinding.ActivityTrackingBinding
import com.azeroth.healthapp.utils.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.sin
import kotlin.math.sqrt

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    SensorEventListener {

    private var distanceCount: Double = 0.0
    private var calorieCount: Double = 0.0
    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityTrackingBinding
    private var mapFragment: SupportMapFragment? = null
    private var stepsToday = 0
    private var currentLatLng: LatLng? = null
    private val polyList: MutableList<LatLng> = arrayListOf()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isNewInstance: Boolean = true
    private var clueCount = 0

    private val locationPermissionCode = 2
    private var locationListGlobal: MutableList<LatLng> = arrayListOf()
    private var mMap: GoogleMap? = null
    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 100
    private val boundList: MutableList<LatLng> = arrayListOf()
    private var landmarkXY: LatLng? = null
    var locationCallback: LocationCallback? = null
    private var mainMarker: Marker? = null
    private var landmarkMarker: Marker? = null
    private var txtSteps: TextView? = null
    private var imgRefresh: ImageView? = null
    private lateinit var sensorManager: SensorManager
    private lateinit var stepCounterSensor: Sensor
    private var stepCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment == null) {
            Toast.makeText(this, "Null mapFragment", Toast.LENGTH_SHORT).show()
        }
        mapFragment?.getMapAsync(this)


//        stepsToday = getValueForToday(this@TrackingActivity, 0)
//        todo calculateDistance()

        retrieveValue(this@TrackingActivity)

//        countNewSteps()
        locationManager =
            this@TrackingActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        txtSteps = findViewById(R.id.txtSteps)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@TrackingActivity)

        getLocationWithGPS()


        //24.920801,67.046995
//        requestLocationPermission()


        sensorManager =
            this@TrackingActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!

    }

    private fun retrieveValue(ctx: Context) {

        Log.d(TAG, "retrieveValue: ")
    }


    private fun countNewSteps() {
        stepCount++
        binding.txtStepsCount.text = stepCount.toString()

        countCalorie()
        countDistance()
    }

    private fun countDistance() {
        calorieCount = 0.1 * stepCount

        binding.txtDistanceCount.text = String.format("%.1f", calorieCount)
    }

    private fun countCalorie() {
        distanceCount = 0.7 * stepCount
        binding.txtCaloriesCount.text = String.format("%.1f", distanceCount)

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        LocationUtils.stepsCount += stepCount
    }

    override fun onStop() {
        super.onStop()

        uploadTOFirebase()
    }

    private fun uploadTOFirebase() {

        LocationUtils.stepsCount = stepCount
//        stepCount
    }

    fun saveToSharedPreferences(context: Context, key: String, value: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    // Function to retrieve a value from SharedPreferences

    private fun getLocationWithGPS() {

        //Location only once

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this@TrackingActivity,
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


    override fun onPause() {
        super.onPause()
        isNewInstance = false
        mainMarker?.remove()
        mainMarker = null
    }


    private fun displayMetrics(dialog: Dialog): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        dialog.window!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }


    private fun addMarkers(list: List<LatLng>) {
        val locationList: MutableList<LatLng> = arrayListOf()
        for (l in list) {

            //here to add markers
            val location = LatLng(l.latitude, l.longitude)
            locationList.add(location)

        }
//        addDestinationMarkers(locationList)
    }


    private fun findNearestLatLng(latlngList: List<LatLng>, targetLatLng: LatLng): LatLng? {
        var nearestLatLng: LatLng? = null
        var nearestDistance = Double.MAX_VALUE

        for (latLng in latlngList) {
            val distance = distanceBetweenLatLngs(latLng, targetLatLng)

            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestLatLng = latLng
            }
        }

        return nearestLatLng
    }

    private fun distanceBetweenLatLngs(latLng1: LatLng, latLng2: LatLng): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        val lat1 = Math.toRadians(latLng1.latitude)
        val lat2 = Math.toRadians(latLng2.latitude)
        val lng1 = Math.toRadians(latLng1.longitude)
        val lng2 = Math.toRadians(latLng2.longitude)

        val dLat = lat2 - lat1
        val dLng = lng2 - lng1

        val a = sin(dLat / 2) * sin(dLat / 2) +
                kotlin.math.cos(lat1) * kotlin.math.cos(lat2) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }


    private fun setBounds(boundList: MutableList<LatLng>) {
        val builder = LatLngBounds.Builder()

        for (l in boundList) {
            builder.include(l)
        }

        val bounds = builder.build()
        try {
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            val padding = (width * 0.38).toInt()


            if (mainMarker == null) {
                val m = MarkerOptions().position(currentLatLng!!).title("Current Location")
                    .icon(
                        BitmapDescriptorFactory.defaultMarker()
                    )
                mainMarker = mMap?.addMarker(m)
            }
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
            mMap!!.animateCamera(cu)

        } catch (i: java.lang.IllegalStateException) {

        }


    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth in km
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * kotlin.math.cos(
                deg2rad(lat2)
            ) * Math.sin(
                dLon / 2
            ) * sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))
        val d = R * c * 1000 // Distance in meters
        return d
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }


    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@TrackingActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@TrackingActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {

        // Continues Location

        if (ContextCompat.checkSelfPermission(
                this@TrackingActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Log.d(TAG, "onLocation:$location")

            if (location != null) {

                if (location.accuracy >= 4) {
                    Toast.makeText(
                        this@TrackingActivity,
                        "Launch App in Open Area\n" +
                                "For Better Accuracy",
                        Toast.LENGTH_LONG
                    ).show()
                }

                currentLatLng = LatLng(location.latitude, location.longitude)


                polyList.add(currentLatLng!!)
                if (mainMarker == null) {
                    val m = MarkerOptions().position(currentLatLng!!).title("Current Location")
                        .icon(
                            BitmapDescriptorFactory.defaultMarker()
                        )
                    mainMarker = mMap?.addMarker(m)
                }

                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 20f))
                updateInitialMarkers(currentLatLng!!)
            }

        } else {
            Log.d(TAG, "getCurrentLocation: ")
        }
    }

    private fun updateInitialMarkers(currentLatLng: LatLng) {
//
//        val response = ServiceBuilder.buildService(ApiInterface::class.java)
//
//        Log.d(ContentValues.TAG, "Current LatLng: $currentLatLng")
//
//        val latLng = currentLatLng.latitude.toString() + "," + currentLatLng.longitude.toString()
//
//        response.sendReq(latLng, "200", getString(R.string.EVERY_API_KEY))
//            .enqueue(object : Callback<PlacesApiResponse> {
//                override fun onResponse(
//                    call: Call<PlacesApiResponse>, response: Response<PlacesApiResponse>
//                ) {
//
//                    Log.d(ContentValues.TAG, "onResponse: " + response.body()!!.status)
//
//                    if (response.body()!!.status == "OK") {
//
//                        val responseFromApi = response
//                        Log.d(ContentValues.TAG, "ResponseJSON: " + responseFromApi.body().toString())
//
//                        val list = responseFromApi.body()!!.results
//
//                        addMarkers(list)
//
//
//                    } else {
//                        Toast.makeText(
//                            this@TrackingActivity, response.body()!!.status, Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<PlacesApiResponse>, t: Throwable) {
//                    Toast.makeText(this@TrackingActivity, t.toString(), Toast.LENGTH_LONG).show()
//                }
//
//            })
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, perform your action here
                    getLocationWithGPS()
                } else {
                    // Permission denied, handle the situation here
                    Toast.makeText(
                        this@TrackingActivity,
                        "Location permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun level2() {

//        val dialog = Dialog(this@TrackingActivity)
//        dialog.setContentView(R.layout.clue_1_layout)
//        dialog.setCancelable(false)
//        val height: Int = displayMetrics(dialog).heightPixels
//        val width: Int = displayMetrics(dialog).widthPixels
//        dialog.window!!.setLayout((width), (height))
//        val txtNext: TextView = dialog.findViewById(R.id.txtNext)
//
//        txtNext.setOnClickListener {
//
        val latLng = landmarkXY
        landmarkMarker = mMap?.addMarker(
            MarkerOptions().position(latLng!!)
                .icon(BitmapDescriptorFactory.defaultMarker())
        )

//            saveLandMarkInMemory()
//            dialog.dismiss()
//
//            showNextLandmark()
//
//        }
//
//        dialog.show()

    }

    private fun level3() {

//        val dialog = Dialog(this@TrackingActivity)
//        dialog.setContentView(R.layout.clue_2_layout)
//        dialog.setCancelable(false)
//        val height: Int = displayMetrics(dialog).heightPixels
//        val width: Int = displayMetrics(dialog).widthPixels
//        dialog.window!!.setLayout((width), (height))
//        val txtNext: TextView = dialog.findViewById(R.id.txtNext)
//
//        txtNext.setOnClickListener {

        if (landmarkXY != null) {
            val latLng = landmarkXY
            landmarkMarker = mMap?.addMarker(
                MarkerOptions().position(latLng!!)
                    .icon(BitmapDescriptorFactory.defaultMarker())
            )
        }

//
//            saveLandMarkInMemory()
//            dialog.dismiss()
//
////            showQAAlert()
//
//        }
//
//        dialog.show()

    }

//    private fun showQAAlert() {
//
//        //How much vegetables and fruits should you consume daily?
//
//        val dialog = Dialog(this@TrackingActivity)
//        dialog.setContentView(R.layout.clue_2_qa_layout)
//        dialog.setCancelable(false)
//        val height: Int = displayMetrics(dialog).heightPixels
//        val width: Int = displayMetrics(dialog).widthPixels
//        dialog.window!!.setLayout((width), (height))
//        val btn50: Button = dialog.findViewById(R.id.btn50)
//        val btn500: Button = dialog.findViewById(R.id.btn500)
//
//        btn500.setOnClickListener {
//
//            showCorrectAlert()
//            dialog.cancel()
//
//        }
//
//        btn50.setOnClickListener {
//            btn50.backgroundTintList =
//                ContextCompat.getColorStateList(this@TrackingActivity, R.color.red)
//        }
//
//        dialog.show()
//
//    }


    private fun saveLandMarkInMemory() {


    }

    private fun updateLandmarkMarker() {

        landmarkMarker?.remove()
        val latLng = landmarkXY
        landmarkMarker = mMap?.addMarker(
            MarkerOptions().position(latLng!!)
                .icon(BitmapDescriptorFactory.defaultMarker())
        )
        clueCount++

        when (clueCount) {
            1 -> {
                level2()
            }

            2 -> {
                level3()
                //replacing level3 with initial

            }

            3 -> {
                level4Congrates()
            }
        }
    }

    private fun level4Congrates() {

//        val dialog = Dialog(this@TrackingActivity)
//        dialog.setContentView(R.layout.congratulations_alert)
//        dialog.setCancelable(false)
//
//        val txtNext: ImageView = dialog.findViewById(R.id.imgCancel)
//        val txtYouFound: TextView = dialog.findViewById(R.id.txtYouFound)
//        txtYouFound.text = "You:\nfound Out 1 Clue\nWalked $stepCount steps"
//
//        txtNext.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()

    }


    private fun showNextLandmark() {

        removeOldLandMark()
        val nearestLtlng: LatLng = findNearestLatLng(locationListGlobal, currentLatLng!!)!!

        landmarkMarker = mMap?.addMarker(
            MarkerOptions().position(nearestLtlng)
                .icon(BitmapDescriptorFactory.defaultMarker())
        )
        landmarkXY = nearestLtlng

        boundList.add(nearestLtlng)
        setBounds(boundList)

    }

    private fun removeOldLandMark() {
        locationListGlobal.remove(landmarkXY)
    }

    private fun updateCurrentMarkerPosition(currentLatLng: LatLng) {
        if (mainMarker != null)
            mainMarker!!.position = currentLatLng
    }

    private fun drawPolyLineOnMap(list: List<LatLng?>?, mMap: GoogleMap) {
        try {
            val polyOptions = PolylineOptions()
            polyOptions.color(resources.getColor(R.color.blue))
            polyOptions.width(8f)
            polyOptions.addAll(list!!)
            mMap.addPolyline(polyOptions)


        } catch (e: java.lang.IllegalStateException) {

        }

    }

    override fun onSensorChanged(p0: SensorEvent?) {

        if (p0?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            countNewSteps()


        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
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

            Log.d(TAG, "calculateSteps: $steps")
            return (Math.round(steps)).toString()
        } catch (exc: ArrayIndexOutOfBoundsException) {
            return "0"
        }

    }

    override fun onLocationChanged(p0: Location) {


        Log.d(TAG, "onLocationResult: $p0")

        if (p0.accuracy <= 5) {


            currentLatLng = LatLng(p0.latitude, p0.longitude)


            Log.d(TAG, "onLocationChanged: $currentLatLng")
            polyList.add(currentLatLng!!)
            drawPolyLineOnMap(polyList, mMap!!)

            countNewSteps()
            updateCurrentMarkerPosition(currentLatLng!!)
            val stepsChanged: Int = calculateSteps(polyList).toInt()
            stepCount += stepsChanged
            binding.txtStepsCount.text = stepCount.toString()

            val dis = landmarkXY?.latitude?.let {
                landmarkXY?.longitude?.let { it1 ->
                    calculateDistance(
                        currentLatLng!!.latitude,
                        currentLatLng!!.longitude,
                        it,
                        it1
                    )
                }
            }

            Log.d(TAG, "onLocationResult Distance: $dis")

            if (dis != null) {
                if (dis < 6) {
                    updateLandmarkMarker()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        saveToSharedPreferences(this@TrackingActivity, "steps", LocationUtils.stepsCount)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0


        getCurrentLocation()

        binding.txtStepsCount.text = "0"
        binding.txtDistanceCount.text = "0"
        binding.txtCaloriesCount.text = "0"
    }

}