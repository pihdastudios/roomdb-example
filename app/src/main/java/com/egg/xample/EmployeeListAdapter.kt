package com.egg.xample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xample.R

class EmployeeListAdapter internal constructor(context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<EmployeeListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var employees = emptyList<Employee>() // Cached copy of words

    inner class WordViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val firstNameItemView: TextView = itemView.findViewById(R.id.textView)
        val lastNameItemView: TextView = itemView.findViewById(R.id.textView2)
        val roleNameItemView: TextView = itemView.findViewById(R.id.textView3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_word, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = employees[position]
        holder.firstNameItemView.text = current.firstName
        holder.lastNameItemView.text = current.lastName
        holder.roleNameItemView.text = current.role
    }

    internal fun setWords(employees: List<Employee>) {
        this.employees = employees
        notifyDataSetChanged()
    }

    fun getWordAtPosition(position: Int): Employee {
        return employees[position]
    }


    override fun getItemCount() = employees.size
}