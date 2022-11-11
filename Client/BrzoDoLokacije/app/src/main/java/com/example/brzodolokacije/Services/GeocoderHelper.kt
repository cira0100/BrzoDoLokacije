package com.example.brzodolokacije.Services

import org.osmdroid.bonuspack.location.GeocoderNominatim

object GeocoderHelper {
    private var geocoder:GeocoderNominatim?=null
    private val userAgent="Mozilla/5.0 (Linux; Android 11; SM-A326BR Build/RP1A.200720.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/100.0.4896.88 Mobile Safari/537.36"
    public fun getInstance(): GeocoderNominatim? {
        if(geocoder==null){
            geocoder= GeocoderNominatim(userAgent)
        }
        return geocoder
    }
}