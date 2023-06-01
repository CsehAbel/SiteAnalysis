package com.example.siteanalysis

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class SiteAnalysisApplication

fun main(args: Array<String>) {
    runApplication<SiteAnalysisApplication>(*args)
}
