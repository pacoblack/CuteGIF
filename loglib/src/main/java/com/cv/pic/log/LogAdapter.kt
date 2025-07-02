package com.cv.pic.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class LogAdapter : ListAdapter<LogEntry, LogAdapter.LogViewHolder>(DiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
    return LogViewHolder(view)
  }

  override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(entry: LogEntry) {
      itemView.findViewById<TextView>(R.id.timestamp).text = entry.timestamp
      itemView.findViewById<TextView>(R.id.tag).text = entry.tag
      itemView.findViewById<TextView>(R.id.message).text = entry.message
    }
  }

  class DiffCallback : DiffUtil.ItemCallback<LogEntry>() {
    override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry) =
      oldItem === newItem

    override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry) =
      oldItem == newItem
  }
}