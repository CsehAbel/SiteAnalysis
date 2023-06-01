package com.example.siteanalysis.controller

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.http.HttpHeaders

@Component
class GetController() {

    fun sendRequestAndProcessResponse(ip:String,cidr:String) {
        val restTemplate = RestTemplate()
        val url = "http://localhost:8060/search_network"
        // Build the URL with query parameters
        val uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ip", "10.221.6.208")
                .queryParam("cidr", "29")
                .build()

        // Send the GET request and get the response
        val responseEntity: ResponseEntity<String> =
                restTemplate.exchange(uriBuilder.toUri(), HttpMethod.GET, null, String::class.java)

        // Process the response
        if (responseEntity.statusCode.is2xxSuccessful) {
            val responseBody = responseEntity.body
            // Continue working with the response body...
        } else {
            // Handle the error case...
        }
    }
}