// com.example.appodp.navigation.Routes.kt
package com.example.appodp.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val CONFIGURATION = "configuration"
    const val ACTIVE_REGISTRATIONS = "active_registrations"
    const val REGISTERED_VEHICLES = "registered_vehicles"
    const val VEHICLE_REGISTRATION_REQUESTS = "vehicle_registration_requests"
    const val FAVORITE_REGISTERED_VEHICLES = "favorite_registered_vehicles_screen"
    const val ACTIVE_REGISTRATIONS_GRAPH = "active_registrations_graph_screen"

    // Ruta za detalje VehicleRegistrationRequestResponse
    const val VEHICLE_REQUEST_DETAILS = "vehicle_request_details/" +
            "registrationPlace={registrationPlace}&" +
            "permanentRegistration={permanentRegistration}&" +
            "firstTimeRequestsTotal={firstTimeRequestsTotal}&" +
            "renewalRequestsTotal={renewalRequestsTotal}&" +
            "ownershipChangesTotal={ownershipChangesTotal}&" +
            "deregisteredTotal={deregisteredTotal}"

    fun vehicleRequestDetailsRoute(
        registrationPlace: String,
        permanentRegistration: Int,
        firstTimeRequestsTotal: Int,
        renewalRequestsTotal: Int,
        ownershipChangesTotal: Int,
        deregisteredTotal: Int
    ): String {
        val encodedPlace = URLEncoder.encode(registrationPlace, StandardCharsets.UTF_8.toString())
        return "vehicle_request_details/" +
                "registrationPlace=${encodedPlace}&" +
                "permanentRegistration=${permanentRegistration}&" +
                "firstTimeRequestsTotal=${firstTimeRequestsTotal}&" +
                "renewalRequestsTotal=${renewalRequestsTotal}&" +
                "ownershipChangesTotal=${ownershipChangesTotal}&" +
                "deregisteredTotal=${deregisteredTotal}"
    }


}