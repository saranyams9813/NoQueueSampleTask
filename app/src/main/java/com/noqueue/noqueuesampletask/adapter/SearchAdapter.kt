package com.noqueue.noqueuesampletask.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.noqueue.noqueuesampletask.databinding.ItemRecentTopSearchBinding
import com.noqueue.noqueuesampletask.databinding.ItemSearchLayoutBinding


class SearchAdapter(var searchList: ArrayList<String>, var onItemClicked: (result: String) -> Unit?):RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    var viewType:Int=1
    companion object{
        const val SEARCH=1
        const val RECENT_TOP_SEARCH=2
    }

   inner class SearchViewHolder : RecyclerView.ViewHolder {
       var searchBinding: ItemSearchLayoutBinding?=null
       var recentTopSearchBinding: ItemRecentTopSearchBinding?=null

        constructor(binding: ItemSearchLayoutBinding) : super(binding.root) {
           searchBinding=binding
        }

        constructor(binding: ItemRecentTopSearchBinding) : super(binding.root) {
            recentTopSearchBinding=binding
        }
    }


    fun updateData(updatedList: ArrayList<String>, type: Int){
        Log.d("UIIII", updatedList.toString())
        searchList =updatedList
        viewType=type
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        Log.d("UIIII ", "onCreateViewHolder")
        when(viewType){
            SEARCH -> {
                val binding = ItemSearchLayoutBinding.inflate(LayoutInflater.from(parent.context))
                return SearchViewHolder(binding)
            }
            RECENT_TOP_SEARCH -> {
                val binding = ItemRecentTopSearchBinding.inflate(LayoutInflater.from(parent.context))
                return SearchViewHolder(binding)
            }
            else ->{
                val binding = ItemSearchLayoutBinding.inflate(LayoutInflater.from(parent.context))
                return SearchViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        Log.d("UIIII ", "OnBindVH")
      var searchItem = searchList.get(position)
        Log.d("UIIII ", searchItem)
        when(viewType){
            SEARCH -> {
                holder?.searchBinding?.searchText?.apply {
                    text = searchItem
                }
                holder?.searchBinding?.parentLyt?.apply {
                    setOnClickListener {
                        onItemClicked.invoke(searchItem)
                    }
                }
            }
            RECENT_TOP_SEARCH -> {

                holder?.recentTopSearchBinding?.searchText?.apply { text = searchItem }
                holder?.recentTopSearchBinding?.parentLyt?.apply {

                    setOnClickListener { onItemClicked.invoke(searchItem) }
                }
                val params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 10, 10, 10)
                holder.recentTopSearchBinding?.parentLyt?.setLayoutParams(params)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("UIIII ", "OnBindVH")
       return searchList.size
    }
    override fun getItemViewType(position: Int): Int = viewType

}