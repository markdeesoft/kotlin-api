package org.deesoft.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*

// สร้าง Extension Function บน Route เพื่อแยกกลุ่ม API
fun Route.mainRouting() {

    route("/api/v1") {

        get {
            call.respondText("🚀 Welcome to Deesoft Portfolio API on Kotlin!")
        }

        taskRouting()
    }

}