package com.example.volunteermanagementapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VolunteerAdapter(
    private val volunteerList: ArrayList<Volunteer>,
    private val onEdit: (Volunteer) -> Unit,
    private val onDelete: (Volunteer) -> Unit
) : RecyclerView.Adapter<VolunteerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_volunteer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val volunteer: Volunteer = volunteerList[position]
        holder.volunteer_available_date.text = volunteer.volunteer_available_date
        holder.volunteer_available_from.text = volunteer.volunteer_available_from
        holder.volunteer_available_till.text = volunteer.volunteer_available_till
        holder.volunteer_email.text = volunteer.volunteer_email
        holder.volunteer_name.text = volunteer.volunteer_name
        holder.volunteer_phone.text = volunteer.volunteer_phone

        holder.volunteer_email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${volunteer.volunteer_email}")
            }
            holder.itemView.context.startActivity(intent)
        }

        // Make phone number clickable
        holder.volunteer_phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${volunteer.volunteer_phone}")
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.buttonMenu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.buttonMenu)
            popup.menuInflater.inflate(R.menu.cardview_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEdit(volunteer)
                        true
                    }
                    R.id.action_delete -> {
                        showDeleteConfirmationDialog(holder.itemView.context, volunteer)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return volunteerList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val volunteer_available_date: TextView = itemView.findViewById(R.id.availableDate)
        val volunteer_available_from: TextView = itemView.findViewById(R.id.availableStart)
        val volunteer_available_till: TextView = itemView.findViewById(R.id.availableEnd)
        val volunteer_email: TextView = itemView.findViewById(R.id.volunteerEmail)
        val volunteer_name: TextView = itemView.findViewById(R.id.volunteerName)
        val volunteer_phone: TextView = itemView.findViewById(R.id.volunteerNumber)
        val buttonMenu: ImageButton = itemView.findViewById(R.id.button_menu)
    }

    private fun showDeleteConfirmationDialog(context: Context, volunteer: Volunteer) {
        AlertDialog.Builder(context).apply {
            setTitle("Delete Confirmation")
            setMessage("Are you sure you want to delete this from the list?")
            setPositiveButton("Yes") { dialog, _ ->
                onDelete(volunteer)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}
