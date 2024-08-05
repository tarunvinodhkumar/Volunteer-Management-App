package com.example.volunteermanagementapp
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
 import com.example.volunteermanagementapp.Event

 class EventAdapter(
     private val eventList: ArrayList<Event>,
     private val onEdit: (Event) -> Unit,
     private val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_event, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event: Event = eventList[position]
        holder.event_date.text = event.event_date
        holder.event_start.text = event.event_start
        holder.event_end.text = event.event_end
        holder.event_name.text = event.event_name
        holder.event_location.text = event.event_location
        holder.event_organizer.text = event.event_organizer

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(event.image_url)
            .into(holder.eventImage)

        // Handle item click
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
            intent.putExtra("event_id", event.id)  // Assuming `Event` has an `id` property
            holder.itemView.context.startActivity(intent)
        }

        holder.buttonMenu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.buttonMenu)
            popup.menuInflater.inflate(R.menu.cardview_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEdit(event)
                        true
                    }
                    R.id.action_delete -> {
                        // Show confirmation dialog before deleting
                        val builder = AlertDialog.Builder(holder.itemView.context)
                        builder.setMessage("Are you sure you want to delete this event?")
                            .setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->
                                onDelete(event)
                            }
                            .setNegativeButton("No") { dialog, id ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val event_date: TextView = itemView.findViewById(R.id.eventDate)
        val event_start: TextView = itemView.findViewById(R.id.eventStart)
        val event_end: TextView = itemView.findViewById(R.id.eventEnd)
        val event_name: TextView = itemView.findViewById(R.id.eventName)
        val event_location: TextView = itemView.findViewById(R.id.eventLocation)
        val event_organizer: TextView = itemView.findViewById(R.id.eventOrganizer)
        val eventImage: ImageView = itemView.findViewById(R.id.eventImage) // ImageView for event image
        val buttonMenu: ImageButton = itemView.findViewById(R.id.button_menu)
    }
}