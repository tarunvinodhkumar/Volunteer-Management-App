import java.io.Serializable

data class EventDetails(
    val event_id: String = "",
    val event_name: String = "",
    val event_organizer: String = "",
    val event_start: String = "",
    val event_end: String = "",
    val event_location: String = "",
    val event_description: String = "",
    val event_date: String =""
) : Serializable