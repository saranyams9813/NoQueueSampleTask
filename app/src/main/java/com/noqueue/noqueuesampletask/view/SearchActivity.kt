package com.noqueue.noqueuesampletask.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.noqueue.noqueuesampletask.R
import com.noqueue.noqueuesampletask.adapter.SearchAdapter
import com.noqueue.noqueuesampletask.adapter.SearchAdapter.Companion.RECENT_TOP_SEARCH
import com.noqueue.noqueuesampletask.adapter.SearchAdapter.Companion.SEARCH
import com.noqueue.noqueuesampletask.databinding.ActivitySearchBinding
import com.noqueue.noqueuesampletask.viewmodel.SearchViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    companion object{
        const val RECENT_SEARCH_KEY="recentSerches"
    }
    lateinit var viewModel: SearchViewmodel
    var binding: ActivitySearchBinding?=null
    var actionBarDrawerToggle: ActionBarDrawerToggle?=null
    val searchResultAdapter : SearchAdapter by lazy { SearchAdapter(ArrayList<String>(), { result -> onItemClicked(result) }) }
    val recentSearchAdapter : SearchAdapter by lazy { SearchAdapter(ArrayList<String>(), { result -> onItemClicked(result) }) }
    val topSearchAdapter : SearchAdapter by lazy { SearchAdapter(ArrayList<String>(), { result -> onItemClicked(result) }) }

    var handlerThread= HandlerThread("custom")
    var handler : Handler?=null
    var searchRunnable :Runnable?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("uuuuuuu","oncreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding?.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(SearchViewmodel::class.java)
        setUpData()
        setUpUI()
        setUpClickListeners()
    }

    override fun onResume() {
        super.onResume()
        Log.d("uuuuuuu","onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("uuuuuuu","ondestroy")
        searchRunnable?.let{if(handler!=null)
            handler?.removeCallbacks(it)
        }
        handlerThread?.quit()
    }

    private fun setUpData() {
        viewModel.createTopSearchList()
        viewModel.getRecentSearchList()
    }

    private fun setUpClickListeners() {
        binding?.apply {
            searchbar.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    cardLayout?.visibility= View.GONE
                        binding?.noResult?.visibility= GONE
                }
                override fun afterTextChanged(editable: Editable?) {
                    if (handler == null) {
                        handlerThread.start()
                        handler = Handler(handlerThread.looper)
                    }
                    searchRunnable?.let { handler?.removeCallbacks(it) }
                    searchRunnable = Runnable {
                        editable?.let {
                            var searchKey = it.toString().trim()
                            if (searchKey.isBlank()) {
                                viewModel?.searchList?.value?.clear()
                                runOnUiThread{
                                    binding?.progress?.visibility= GONE
                                    updateSearchList(viewModel.searchList.value)
                                }
                                return@let
                            }
                            observeSearchResult(searchKey);
                        }
                    }
                    searchRunnable?.let { handler?.postDelayed(it,1000) }
                    binding?.progress?.visibility=VISIBLE
                }

            })

        }
    }

    private fun observeSearchResult(key: String) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel?.searchList?.removeObservers(this@SearchActivity)
            viewModel?.searchList?.observe(this@SearchActivity, Observer {
                var handlerHasCallbacks =if(searchRunnable!=null){  handler?.hasCallbacks(searchRunnable!!)?:false } else false
                 if(it.isEmpty() && !handlerHasCallbacks )
                     binding?.noResult?.visibility= VISIBLE
                 else
                     binding?.noResult?.visibility= GONE
                updateSearchList(it)
                binding?.progress?.visibility=GONE
            })
        }
        viewModel?.getPlaces(this, key)
    }

    private fun updateSearchList(it: MutableList<String>?) {
        Log.d("UIIIII", "update RV")

        it?.let{searchResultAdapter?.updateData(it as ArrayList<String>,SEARCH)
            binding?.searchRV?.visibility= View.VISIBLE
        }
    }


    private fun setUpUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(binding?.toolbar)
        binding?.hamburgerIcon?.setOnClickListener {
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
            hideKeyboard(binding?.searchbar)
        }
        binding?.searchRV?.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchResultAdapter
        }
        binding?.recentSearchesRV?.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = recentSearchAdapter
        }
        binding?.topSearchesRV?.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = topSearchAdapter
        }
        topSearchAdapter.updateData(viewModel.topSearchList, RECENT_TOP_SEARCH)
        updateRecentSearchesView(viewModel.recentSearchList)
    }
    private fun onItemClicked(result: String){

        viewModel.storeToRecentList(result)
        var newList= ArrayList<String>()
        newList.addAll(viewModel.recentSearchList)
        updateRecentSearchesView(newList)
        hideKeyboard(binding?.searchbar)
        binding?.searchbar?.text?.clear()
        binding?.searchRV?.visibility= View.GONE
        binding?.cardLayout?.visibility= View.VISIBLE
        binding?.cardname?.text=result
        Log.d("resssss", "recent search" + viewModel?.recentSearchList?.toString())
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }
    fun hideKeyboard(view: View?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun updateRecentSearchesView(list: ArrayList<String>) {
        recentSearchAdapter.updateData(list, RECENT_TOP_SEARCH)
        if(list.isNullOrEmpty()){
            binding?.recentSearchesRV?.visibility= View.GONE
            binding?.noRecentSearches?.visibility= View.VISIBLE
        }else {
            binding?.recentSearchesRV?.visibility= View.VISIBLE
            binding?.noRecentSearches?.visibility= View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle?.onOptionsItemSelected(item)?:false) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        binding?.apply {
            if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
                return
            }
        }
        super.onBackPressed()
    }
}