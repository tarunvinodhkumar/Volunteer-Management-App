package com.example.volunteermanagementapp

data class Volunteer(

    var id: String = "",
    var volunteer_available_date: String ?= null,
    var volunteer_available_from: String ?= null,
    var volunteer_available_till: String ?= null,
    var volunteer_email: String ?= null,
    var volunteer_name: String ?= null,
    var volunteer_phone: String ?= null,
    var created_by: String? = null

)
