package com.example.ecotrack

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class CommunityActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnSelectImage: Button
    private lateinit var imgPreview: ImageView
    private lateinit var recyclerView: RecyclerView

    private lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference

    private var imageUri: Uri? = null
    private val reportList = mutableListOf<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        etCity = findViewById(R.id.etReportCity)
        etDescription = findViewById(R.id.etReportDescription)
        btnSubmit = findViewById(R.id.btnSubmitReport)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        imgPreview = findViewById(R.id.imgPreview)
        recyclerView = findViewById(R.id.recyclerReports)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ReportAdapter(reportList)

        database = FirebaseDatabase.getInstance().getReference("CommunityReports")
        storageRef = FirebaseStorage.getInstance().reference.child("report_images")

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 101)
        }

        btnSubmit.setOnClickListener {

            val city = etCity.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (city.isEmpty() || description.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Fill all fields & select image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImage(city, description)
        }

        loadReports()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imgPreview.setImageURI(imageUri)
        }
    }

    private fun uploadImage(city: String, description: String) {

        val fileName = UUID.randomUUID().toString()
        val fileRef = storageRef.child("$fileName.jpg")

        fileRef.putFile(imageUri!!)
            .addOnSuccessListener {

                fileRef.downloadUrl.addOnSuccessListener { uri ->

                    val report = Report(
                        city,
                        description,
                        uri.toString(),
                        System.currentTimeMillis()
                    )

                    database.push().setValue(report)

                    Toast.makeText(this, "Report Uploaded", Toast.LENGTH_SHORT).show()

                    etCity.text.clear()
                    etDescription.text.clear()
                    imgPreview.setImageResource(0)
                    imageUri = null
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadReports() {

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                reportList.clear()

                for (data in snapshot.children) {
                    val report = data.getValue(Report::class.java)
                    if (report != null) {
                        reportList.add(report)
                    }
                }

                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}