/*
 * This software is licensed under the MIT license
 *
 * Copyright (c) 2022, Javier Carbone, author Michael Prommersberger
 * Additions and modifications, author Matthias Emde 
 */

package de.practicetime.practicetime.ui.notes

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.practicetime.practicetime.R

class NotesFragment : Fragment(R.layout.fragment_notes) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup toolbar with back button
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.notesToolbar)
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_nav_back)
        }
        
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}
