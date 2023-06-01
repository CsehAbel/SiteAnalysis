package com.example.siteanalysis.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.http.HttpHeaders

import com.example.siteanalysis.model.ResponseObject

@Component
class GetController() {

    fun sendRequestAndProcessResponse(ip:String,cidr:String): String? {
        val restTemplate = RestTemplate()
        val url = "http://localhost:8060/search_network"
        // Build the URL with query parameters
        val uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ip", ip)
                .queryParam("cidr", cidr)
                .build()

        // Send the GET request and get the response
        val responseEntity: ResponseEntity<String> =
                restTemplate.exchange(uriBuilder.toUri(), HttpMethod.GET, null, String::class.java)

        // Process the response
        if (responseEntity.statusCode.is2xxSuccessful) {
            val responseBody = responseEntity.body

            return responseBody
        } else {
            // Handle the error case...
            return ""
        }
    }
}