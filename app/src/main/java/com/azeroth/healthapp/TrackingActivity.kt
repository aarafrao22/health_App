package com.azeroth.healthapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.azeroth.healthapp.databinding.ActivityTrackingBinding
import com.azeroth.healthapp.fragments.ChartFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityTrackingBinding
    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null

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


    }

    private fun setFragment(fragment: Fragment) {
        if (!supportFragmentManager.isDestroyed) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayoutMain, fragment)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }
}