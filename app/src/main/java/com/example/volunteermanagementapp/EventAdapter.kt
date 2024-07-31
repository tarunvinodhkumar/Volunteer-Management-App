package com.example.volunteermanagementapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class EventAdapter(private val eventList: ArrayList<Event>):
    RecyclerView.Adapter<EventAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,
            parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventAdapter.MyViewHolder, position: Int) {

        val event : Event = eventList[position]
        holder.event_date.text = event.event_date
        holder.event_start.text = event.event_start
        holder.event_name.text = event.event_name
        holder.event_location.text = event.event_location
        holder.event_organizer.text = event.event_organizer


    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        val event_date : TextView = itemView.findViewById(R.id.eventDate)
        val event_start : TextView = itemView.findViewById(R.id.eventTime)
        val event_name : TextView = itemView.findViewById(R.id.eventName)
        val event_location : TextView = itemView.findViewById(R.id.eventLocation)
        val event_organizer : TextView = itemView.findViewById(R.id.eventOrganizer)





    }
}