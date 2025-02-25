package com.example.volunteermanagementapp

import com.google.firebase.Timestamp


data class Event(

    var id: String = "",
    var event_description: String ?= null,
    var event_end: String ?= null,
    var event_location: String ?= null,
    var event_name: String ?= null,
    var event_organizer: String ?= null,
    var event_start: String ?= null,
    var event_date: String ?= null,
    var image_url: String ?= null

)

