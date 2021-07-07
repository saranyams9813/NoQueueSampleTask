package com.noqueue.noqueuesampletask.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noqueue.noqueuesampletask.Repositoty.PlacesRepository
import com.noqueue.noqueuesampletask.Utils.SharedPreference
import com.noqueue.noqueuesampletask.view.SearchActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class SearchViewmodel:ViewModel() {
    var searchList : MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()
    var  recentSearchList: ArrayList<String> = ArrayList<String>()
    var  topSearchList: ArrayList<String> = ArrayList<String>()

    fun getPlaces(context: Context?, input: String){
        CoroutineScope(IO).cancel()
        CoroutineScope(IO).launch {
        try{
            var response:Deferred<ArrayList<String>> = async{ PlacesRepository.getPlaces(context, input)}

             searchList.postValue(response.await())
            Log.d("Response ", "" + searchList.value.toString())
        }catch (exc: Exception){
            Log.d("Response failed", "" + exc.message)
        }
        }
    }

    fun storeToRecentList(recent: String) {
        if(recentSearchList.contains(recent))
            recentSearchList.remove(recent)
        recentSearchList?.add(0, recent)
        if((recentSearchList?.size?:0)>5){
            recentSearchList?.removeAt(5)
        }
        val stringset: Set<String> = HashSet(recentSearchList)
        SharedPreference.storeStringSet(SearchActivity.RECENT_SEARCH_KEY,stringset)
    }

    fun createTopSearchList() {
        if(topSearchList.isEmpty()) {
            topSearchList.add("Mumbai")
            topSearchList.add("Pune")
            topSearchList.add("Delhi")
            topSearchList.add("Bangalore")
            topSearchList.add("Hyderabad")

        }
    }

    fun getRecentSearchList() {
        var sharedPref = SharedPreference.getSharedPref()
        var search= sharedPref?.getStringSet(SearchActivity.RECENT_SEARCH_KEY,null)
        search?.let {
            recentSearchList.clear()
            recentSearchList.addAll(search)
        }
    }
}