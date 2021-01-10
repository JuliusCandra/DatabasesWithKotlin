package com.dev_candra.mynotesapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev_candra.mynotesapp.CustomOnItemClickListener
import com.dev_candra.mynotesapp.R
import com.dev_candra.mynotesapp.activity.NoteAddUpdateActivity
import com.dev_candra.mynotesapp.databinding.ItemNoteBinding
import com.dev_candra.mynotesapp.entity.Note

class NoteAdapter(private val activity: Activity) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    var listNotes = ArrayList<Note>()
        set(listNotes) {
            if (listNotes.size > 0) {
                this.listNotes.clear()
            }
            this.listNotes.addAll(listNotes)

            notifyDataSetChanged()
        }

    fun addItem(note: Note) {
        this.listNotes.add(note)
        notifyItemInserted(this.listNotes.size - 1)
    }

    fun updateItem(position: Int, note: Note) {
        this.listNotes[position] = note
        notifyItemChanged(position, note)
    }

    fun removeItem(position: Int) {
        this.listNotes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listNotes.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder {
        // Your Code
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        )
    }

    override fun getItemCount(): Int {
        // Your Code
        return this.listNotes.size
    }

    override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
        // Your Code
        holder.bind(listNotes[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemNoteBinding.bind(itemView)

        fun bind(note: Note) {
            binding.tvItemTitle.text = note.title
            binding.tvItemDate.text = note.date
            binding.tvItemDescription.text = note.description
            binding.cvItemNote.setOnClickListener(CustomOnItemClickListener(adapterPosition,object : CustomOnItemClickListener.OnItemClickCallback{
                override fun onItemClicked(view: View, position: Int) {
                    // Your Code
                    val intent = Intent(activity,NoteAddUpdateActivity::class.java)
                    intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION,position)
                    intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE,note)
                    activity.startActivityForResult(intent,NoteAddUpdateActivity.REQUEST_UPDATE)
                }

            }))
        }

    }
}