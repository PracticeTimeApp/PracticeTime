/*
 * This software is licensed under the MIT license
 */

package de.practicetime.practicetime.ui.notes

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.practicetime.practicetime.PracticeTime
import de.practicetime.practicetime.R
import de.practicetime.practicetime.database.entities.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesFab: FloatingActionButton
    private lateinit var notesEmptyText: TextView
    private var noteAdapter: NoteAdapter? = null
    private var notesList: List<Note> = emptyList()

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

        // Initialize views
        notesRecyclerView = view.findViewById(R.id.notesRecyclerView)
        notesFab = view.findViewById(R.id.notesFab)
        notesEmptyText = view.findViewById(R.id.notesEmptyText)

        // Setup RecyclerView
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Setup FAB
        notesFab.setOnClickListener {
            showNoteDialog(null)
        }

        // Load notes
        loadNotes()
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            notesList = PracticeTime.noteDao.getAll()
            updateUI()
        }
    }

    private fun updateUI() {
        if (notesList.isEmpty()) {
            notesRecyclerView.visibility = View.GONE
            notesEmptyText.visibility = View.VISIBLE
            notesEmptyText.text = getString(R.string.notes_empty_hint)
        } else {
            notesRecyclerView.visibility = View.VISIBLE
            notesEmptyText.visibility = View.GONE
            
            noteAdapter = NoteAdapter(
                notesList,
                onClick = { note ->
                    showNoteDialog(note)
                },
                onLongClick = { note ->
                    showDeleteDialog(note)
                    true
                }
            )
            notesRecyclerView.adapter = noteAdapter
        }
    }

    private fun showNoteDialog(existingNote: Note?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_note_edit, null)

        val titleEditText = dialogView.findViewById<EditText>(R.id.noteTitleEditText)
        val contentEditText = dialogView.findViewById<EditText>(R.id.noteContentEditText)
        val charCountText = dialogView.findViewById<TextView>(R.id.noteCharCount)

        // Set existing note data or defaults
        existingNote?.let {
            titleEditText.setText(it.title)
            contentEditText.setText(it.content)
        }

        // Update character count
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                charCountText.text = "${s?.length ?: 0}/${Note.MAX_CONTENT_LENGTH}"
            }
        }
        contentEditText.addTextChangedListener(textWatcher)
        charCountText.text = "${contentEditText.text.length}/${Note.MAX_CONTENT_LENGTH}"

        val defaultTitleKeyListener = titleEditText.keyListener
        val defaultContentKeyListener = contentEditText.keyListener

        // Create dialog
        val isNewNote = existingNote == null
        val dialogTitle = if (isNewNote) R.string.note_create_title else R.string.note_view_title

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton(R.string.note_save) { _, _ ->
                saveNote(existingNote, titleEditText, contentEditText)
            }
            .setNegativeButton(R.string.note_cancel, null)

        if (!isNewNote) {
            builder.setNeutralButton(R.string.note_edit, null)
        }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

            fun applyEditMode(editing: Boolean) {
                if (editing) {
                    titleEditText.keyListener = defaultTitleKeyListener
                    contentEditText.keyListener = defaultContentKeyListener
                    titleEditText.isFocusable = true
                    titleEditText.isFocusableInTouchMode = true
                    titleEditText.isCursorVisible = true
                    contentEditText.isFocusable = true
                    contentEditText.isFocusableInTouchMode = true
                    contentEditText.isCursorVisible = true

                    dialog.setTitle(if (isNewNote) R.string.note_create_title else R.string.note_edit_title)
                    positiveButton.visibility = View.VISIBLE
                    positiveButton.isEnabled = validateNote(titleEditText, contentEditText, showError = !isNewNote)
                    neutralButton?.visibility = View.GONE
                } else {
                    titleEditText.keyListener = null
                    contentEditText.keyListener = null
                    titleEditText.isFocusable = false
                    titleEditText.isFocusableInTouchMode = false
                    titleEditText.isCursorVisible = false
                    contentEditText.isFocusable = false
                    contentEditText.isFocusableInTouchMode = false
                    contentEditText.isCursorVisible = false

                    dialog.setTitle(R.string.note_view_title)
                    positiveButton.visibility = View.GONE
                    neutralButton?.visibility = View.VISIBLE
                }
            }

            applyEditMode(isNewNote)

            neutralButton?.setOnClickListener {
                applyEditMode(true)
                titleEditText.requestFocus()
            }

            titleEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    positiveButton.isEnabled = validateNote(titleEditText, contentEditText, showError = true)
                }
            })

            contentEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    positiveButton.isEnabled = validateNote(titleEditText, contentEditText, showError = true)
                }
            })
        }

        dialog.show()
    }

    private fun validateNote(
        titleEditText: EditText,
        contentEditText: EditText,
        showError: Boolean = true
    ): Boolean {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString()

        if (title.isEmpty()) {
            if (showError) {
                titleEditText.error = getString(R.string.note_title_required)
            }
            return false
        }

        if (title.length > Note.MAX_TITLE_LENGTH) {
            if (showError) {
                titleEditText.error = getString(R.string.note_max_title_length, Note.MAX_TITLE_LENGTH)
            }
            return false
        }

        if (content.length > Note.MAX_CONTENT_LENGTH) {
            if (showError) {
                contentEditText.error = getString(R.string.note_max_content_length, Note.MAX_CONTENT_LENGTH)
            }
            return false
        }

        titleEditText.error = null
        contentEditText.error = null
        return true
    }

    private fun saveNote(existingNote: Note?, titleEditText: EditText, contentEditText: EditText) {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString()

        lifecycleScope.launch {
            try {
                if (existingNote == null) {
                    val note = Note(title = title, content = content)
                    PracticeTime.noteDao.insert(note)
                    Toast.makeText(requireContext(), "Note created", Toast.LENGTH_SHORT).show()
                } else {
                    existingNote.title = title
                    existingNote.content = content
                    PracticeTime.noteDao.update(existingNote)
                    Toast.makeText(requireContext(), "Note updated", Toast.LENGTH_SHORT).show()
                }
                
                // Refresh the list
                loadNotes()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error saving note: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.note_delete_title)
            .setMessage(R.string.note_delete_message)
            .setPositiveButton(R.string.note_delete_confirm) { _, _ ->
                lifecycleScope.launch {
                    try {
                        PracticeTime.noteDao.delete(note)
                        Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
                        loadNotes()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error deleting note: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton(R.string.note_cancel, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}

private typealias RecyclerView = androidx.recyclerview.widget.RecyclerView
