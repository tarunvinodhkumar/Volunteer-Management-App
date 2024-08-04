package com.example.volunteermanagementapp

import android.os.Parcel
import android.os.Parcelable

data class Event(
    var id: String = "",
    var event_description: String? = null,
    var event_end: String? = null,
    var event_location: String? = null,
    var event_name: String? = null,
    var event_organizer: String? = null,
    var event_start: String? = null,
    var event_date: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(event_description)
        parcel.writeString(event_end)
        parcel.writeString(event_location)
        parcel.writeString(event_name)
        parcel.writeString(event_organizer)
        parcel.writeString(event_start)
        parcel.writeString(event_date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}
