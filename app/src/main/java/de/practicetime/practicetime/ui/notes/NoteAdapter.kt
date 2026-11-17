/*
 * This software is licensed under the MIT license
 */

package de.practicetime.practicetime.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.practicetime.practicetime.R
import de.practicetime.practicetime.database.entities.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val notes: List<Note>,
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Boolean
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.noteTitle)
        val contentPreview: TextView = itemView.findViewById(R.id.noteContentPreview)
        val date: TextView = itemView.findViewById(R.id.noteDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        
        holder.title.text = note.title
        holder.contentPreview.text = note.content.ifEmpty { "(No content)" }
        holder.date.text = "Modified: ${dateFormat.format(Date(note.modifiedAt * 1000))}"

        holder.itemView.setOnClickListener { onClick(note) }
        holder.itemView.setOnLongClickListener { onLongClick(note) }
    }

    override fun getItemCount(): Int = notes.size
}
