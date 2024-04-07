package com.azeroth.healthapp

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.azeroth.healthapp.adapters.TipsAdapter
import com.azeroth.healthapp.databinding.ActivityTipsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TipsActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    private lateinit var binding: ActivityTipsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("tips")

        // Read data from Firebase
        readDataFromFirebase()
    }

    private fun showLoadingDialog(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissLoadingDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun readDataFromFirebase() {

        showLoadingDialog(this@TipsActivity)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dismissLoadingDialog()

                val value = dataSnapshot.value as? ArrayList<String>
                if (value != null) {
                    Log.d("Firebase", "Value is: $value")

                    val list: ArrayList<String> = ArrayList()
                    value.forEach { item ->
                        if (!item.isNullOrEmpty()) {
                            list.add(item)
                        }
                    }
                    initRecyclerView(list)
                } else {
                    Log.w("Firebase", "Data snapshot value is null or not of expected type")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Failed to read value.", error.toException())
                dismissLoadingDialog()
            }
        })
    }


    private fun initRecyclerView(data: ArrayList<String>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TipsActivity)
            adapter = TipsAdapter(data)
        }
    }
}
