package com.example.ivanschuetz.aacmlcodechallenge

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		val toggle = ActionBarDrawerToggle(
			this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		PagerSnapHelper().attachToRecyclerView(nav_pager)

		// I'd probably change these variable names to camel case. Using defaults for now.
		nav_view.setNavigationItemSelectedListener(this)

		webview.webViewClient = WebViewClient()
		webview.loadUrl("https://www.mytoys.de")

		NavigationProvider().getNavigationEntries {
			runOnUiThread {
				when (it) {
					is Success -> initNavigation(it.value)
					is Error -> Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	private fun initNavigation(navigation: Navigation) {

		val uiNavigation = toUINavigation(navigation)

		var nodeStack = listOf<NavigationNode>()
		val adapter = NavigationPagerAdapter(uiNavigation) { adapter, item ->
			when (item) {
				is NavigationSection -> {}
				is NavigationNode -> {
					nodeStack += item
					adapter.addPage(item.children)
					nav_pager.smoothScrollToPosition(adapter.pages.size - 1)
				}
				is NavigationLink -> {
					webview.loadUrl(item.url)
					drawer_layout.closeDrawers()
				}
			}
		}

		nav_pager.adapter = adapter

		val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
			false)
		nav_pager.layoutManager = layoutManager

		nav_pager.addFirstIdleCellListener(layoutManager) { focusedPage ->
			// Ensure current page is always the last
			adapter.removePagesAfter(focusedPage)
			// Update current node label
			nodeStack = nodeStack.take(focusedPage)
			nav_title.text = nodeStack.lastOrNull()?.label ?: ""
			// Update visiblity of back button
			nav_back.visibility = if (focusedPage == 0) View.GONE else View.VISIBLE

			// Note: Wich more time I'd probably change the implementation a little to keep the node and the
			// respective page in one single object which would be used as model of both nav_title and the page view
			// instead of having 2 separate states (nodeStack and the pages in the adapter) like now.
		}

		nav_back.setOnClickListener {
			val currentPage = layoutManager.findFirstCompletelyVisibleItemPosition()
			if (currentPage > 0) {
				nav_pager.smoothScrollToPosition(currentPage - 1)
			}
		}

		nav_close.setOnClickListener {
			drawer_layout.closeDrawers()
		}
	}

	/**
	 * Reorganize items such that the structure matches the pager structure
	 * i.e. The items belonging to each section are in the same level, below the section.
	 * The section keeps also its references to the children, in case this becomes necessary.
	 */
	private fun toUINavigation(navigation: Navigation): Navigation =
		Navigation(items = flattenSections(navigation.items))

	private fun flattenSections(items: List<NavigationTreeNode>): List<NavigationTreeNode> {
		var objects: List<NavigationTreeNode> = mutableListOf()
		for (item in items) {
			when (item) {
				is NavigationSection -> {
					objects += item
					objects += flattenSections(item.children)
				}
				is NavigationLink ->
					objects += NavigationLink(item.label, item.url)
				is NavigationNode ->
					objects += NavigationNode(item.label, flattenSections(item.children))
			}
		}
		return objects
	}

	override fun onBackPressed() {
		if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
			drawer_layout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = true

	override fun onNavigationItemSelected(item: MenuItem): Boolean = true
}
