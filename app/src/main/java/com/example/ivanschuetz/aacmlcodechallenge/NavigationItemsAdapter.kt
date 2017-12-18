package com.example.ivanschuetz.aacmlcodechallenge

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by ivanschuetz on 18.12.17.
 */
class NavigationItemsAdapter(
	val items: List<NavigationTreeNode>,
	private val onClickItem: (NavigationTreeNode) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private val sectionType = 0
	private val nodeType = 1
	private val linkType = 2

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		when (viewType) {
			sectionType -> {
				val view = LayoutInflater.from(parent.context).inflate(R.layout.navigation_header, parent, false)
				val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
				view.layoutParams = lp
				return SectionViewHolder(view)
			}
			nodeType -> {
				val view = LayoutInflater.from(parent.context).inflate(R.layout.navigation_node, parent, false)
				val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
				view.layoutParams = lp
				return NodeViewHolder(view)
			}
			linkType -> {
				val view = LayoutInflater.from(parent.context).inflate(R.layout.navigation_link, parent, false)
				val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
				view.layoutParams = lp
				return LinkViewHolder(view)
			}
			else -> throw RuntimeException("Not handled type: $viewType")
		}
	}

	override fun getItemViewType(position: Int): Int =
		when (items[position]) {
			is NavigationSection -> sectionType
			is NavigationNode -> nodeType
			is NavigationLink -> linkType
		}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

		holder ?: return

		val item = items[position]

		when {
			holder is SectionViewHolder && item is NavigationSection -> holder.titleTextView.text = item.label
			holder is NodeViewHolder && item is NavigationNode -> {
				holder.titleTextView.text = item.label
			}
			holder is LinkViewHolder && item is NavigationLink -> holder.urlTextView.text = item.label
			else -> {
				throw RuntimeException("Illegal combination! holder: $holder, item: $item")
			}
		}

		holder.itemView.setOnClickListener {
			onClickItem(item)
		}
	}

	override fun getItemCount(): Int = items.size

	inner class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val titleTextView: TextView = view.findViewById(R.id.title_text_view)
	}

	inner class NodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val titleTextView: TextView = view.findViewById(R.id.label_button)
	}

	inner class LinkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val urlTextView: TextView = view.findViewById(R.id.url_button)
	}
}