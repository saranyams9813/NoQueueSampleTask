package com.noqueue.noqueuesampletask.Repositoty

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream

object PlacesRepository {
    var citiesList:ArrayList<String>?=null



    suspend fun getPlaces(context: Context?,input: String):ArrayList<String>{
        if(citiesList==null) {
            var jsonString = context?.run { getJsonFromAssets(context, "cities.json") }
            val gson = GsonBuilder().create()
            citiesList = gson.fromJson<ArrayList<String>>(jsonString, object : TypeToken<ArrayList<String>>() {}.type)
        }
        var filteredList = citiesList?.filter { it.contains(input,true) }
        return filteredList as ArrayList<String>
    }
    fun getJsonFromAssets(context: Context, fileName: String?): String? {
        val jsonString: String
        jsonString = try {
            val `is`: InputStream = context.assets.open(fileName!!)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }
}