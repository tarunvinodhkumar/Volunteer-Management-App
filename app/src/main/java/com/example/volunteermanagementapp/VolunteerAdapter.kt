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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VolunteerAdapter(
    private val volunteerList: ArrayList<Volunteer>,
    private val context: Context,
    private val onEdit: (Volunteer) -> Unit,
    private val onDelete: (Volunteer) -> Unit
) : RecyclerView.Adapter<VolunteerAdapter.MyViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
            context.startActivity(intent)
        }

        holder.volunteer_phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${volunteer.volunteer_phone}")
            }
            context.startActivity(intent)
        }

        holder.buttonMenu.setOnClickListener {
            val popup = PopupMenu(context, holder.buttonMenu)
            popup.menuInflater.inflate(R.menu.cardview_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        // Start EditVolunteer activity
                        val intent = Intent(context, EditVolunteer::class.java).apply {
                            putExtra("volunteer_id", volunteer.id) // Pass the volunteer ID
                            putExtra("volunteer_name", volunteer.volunteer_name)
                            putExtra("volunteer_email", volunteer.volunteer_email)
                            putExtra("volunteer_phone", volunteer.volunteer_phone)
                            putExtra("volunteer_available_date", volunteer.volunteer_available_date)
                            putExtra("volunteer_available_from", volunteer.volunteer_available_from)
                            putExtra("volunteer_available_till", volunteer.volunteer_available_till)
                        }
                        context.startActivity(intent)
                        true
                    }
                    R.id.action_delete -> {
                        showDeleteConfirmationDialog(context, volunteer)
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
            setMessage("Are you sure you want to delete this volunteer?")
            setPositiveButton("Yes") { dialog, _ ->
                db.collection("volunteer_details").document(volunteer.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Volunteer deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error deleting volunteer: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
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
