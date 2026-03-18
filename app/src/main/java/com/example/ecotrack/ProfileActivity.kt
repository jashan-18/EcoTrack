package com.jashan.ecotrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileActivity : Fragment(R.layout.activity_profile) {

    private lateinit var nameTv: TextView
    private lateinit var emailTv: TextView
    private lateinit var logoutBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTv = view.findViewById(R.id.profileName)
        emailTv = view.findViewById(R.id.profileEmail)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        val sharedPref = requireActivity()
            .getSharedPreferences("EcoTrackUser", android.content.Context.MODE_PRIVATE)

        val name = sharedPref.getString("name", "No Name")
        val email = sharedPref.getString("email", "No Email")

        nameTv.text = name
        emailTv.text = email

        logoutBtn.setOnClickListener {

            sharedPref.edit().clear().apply()

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}