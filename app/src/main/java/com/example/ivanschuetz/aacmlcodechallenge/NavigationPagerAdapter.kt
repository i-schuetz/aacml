package com.example.ivanschuetz.aacmlcodechallenge

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager

/**
 * Created by ivanschuetz on 05.07.17.
 */
class NavigationPagerAdapter(private val navigation: Navigation, private val onClickItem: (NavigationPagerAdapter,
                                                                                           NavigationTreeNode) ->
Unit) :
RecyclerView.Adapter<NavigationPagerAdapter.ViewHolder>() {

	var pages: List<List<NavigationTreeNode>> = listOf(navigation.items)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationPagerAdapter.ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.nav_page, parent, false)
		return ViewHolder(itemView)
	}

	override fun onBindViewHolder(viewHolder: NavigationPagerAdapter.ViewHolder?, position: Int) {

		viewHolder ?: return

		val items = pages[position]

		val context = viewHolder.itemView.context

		val layoutManager = LinearLayoutManager(
			context,
			LinearLayoutManager.VERTICAL,
			false
		)

		viewHolder.itemsRecyclerView.layoutManager = layoutManager
		val adapter = NavigationItemsAdapter(items = items) { item ->
			onClickItem(this, item)
		}

		// This isn't working well in the pager - when navigating back it increases space between cells. Android bug?
//		val dividerItemDecoration = DividerItemDecoration(viewHolder.itemView.context, DividerItemDecoration.VERTICAL)
//		viewHolder.itemsRecyclerView.addItemDecoration(dividerItemDecoration)

		viewHolder.itemsRecyclerView.adapter = adapter
		viewHolder.itemsRecyclerView.setBackgroundColor(Color.WHITE)
	}

	override fun getItemCount(): Int {
		return pages.size
	}

	fun addPage(items: List<NavigationTreeNode>) {
		pages += listOf(items)
		notifyDataSetChanged()
	}

	fun removePagesAfter(page: Int) {
		if (page >= pages.size) return

		pages = pages.take(page + 1)
		notifyDataSetChanged()
	}

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val itemsRecyclerView: RecyclerView = view.findViewById(R.id.nav_items)
	}
}