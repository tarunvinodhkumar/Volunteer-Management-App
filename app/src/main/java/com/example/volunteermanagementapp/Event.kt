package com.example.volunteermanagementapp

import com.google.firebase.Timestamp


data class Event(
    var event_description: String ?= null,
    var event_end: Timestamp ?= null,
    var event_location: String ?= null,
    var event_name: String ?= null,
    var event_organizer: String ?= null,
    var event_start: Timestamp ?= null

)

