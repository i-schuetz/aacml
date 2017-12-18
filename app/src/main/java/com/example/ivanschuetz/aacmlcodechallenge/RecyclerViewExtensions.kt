package com.example.ivanschuetz.aacmlcodechallenge

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Provides the index of the first completely visible cell in indle state.
 * When used in a pager recycler view this is basically "page focused" event.
 *
 * Created by ivanschuetz on 18.12.17.
 */
fun RecyclerView.addFirstIdleCellListener(layoutManager: LinearLayoutManager, listener: (Int) -> Unit) {

	addOnScrollListener(object : RecyclerView.OnScrollListener() {

		override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

			super.onScrollStateChanged(recyclerView, newState)

			if (newState == RecyclerView.SCROLL_STATE_IDLE) {

				val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
				if (firstCompletelyVisibleItemPosition >= 0) {
					listener(firstCompletelyVisibleItemPosition)
				}
			}
		}
	})
}