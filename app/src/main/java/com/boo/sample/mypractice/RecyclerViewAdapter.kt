package com.boo.sample.mypractice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boo.sample.mypractice.databinding.RecyclerViewItemBinding
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    val mItems = arrayListOf<RecyclerItem>()
    private val mPublishSubject = PublishSubject.create<RecyclerItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mItems[position]
        holder.bind(currentItem)
        holder.getClickObserver(currentItem)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateItems(items : List<RecyclerItem>){
        mItems.addAll(items)
    }

    fun updateItem(item: RecyclerItem) {
        mItems.add(item)
    }

    fun getItemPublishSubject() = mPublishSubject


    inner class MyViewHolder(val binding: RecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        /*fun getClickObserver(item : RecyclerItem) = Observable.create<RecyclerItem> { emitter ->
            binding.root.setOnClickListener {
                emitter.onNext(item)
            }
        }*/

        fun getClickObserver(item: RecyclerItem) {
            binding.root.setOnClickListener {
                mPublishSubject.onNext(item)
            }
        }

        fun bind(item : RecyclerItem) {
            binding.item = item
        }

    }
}