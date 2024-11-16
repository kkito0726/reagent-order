package kkito.reagent_order.health

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HealthController {
    @GetMapping("/health")
    fun checkHealthApplication(): ResponseEntity<String> {
        return ResponseEntity.ok("This kotlin application is running🚀")
    }
}