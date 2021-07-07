package com.noqueue.noqueuesampletask.Utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreference {
    companion object{
         fun getSharedPref(): SharedPreferences? {
            return ApplicationCommon.context?.getSharedPreferences("TEACHNEXTPREF", Context.MODE_PRIVATE)
         }
         fun storeStringSet(key:String,set:Set<String>){
             var editor = getSharedPref()?.edit()
             editor?.putStringSet(key, set)
             editor?.commit()
         }
    }
}