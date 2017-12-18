package com.example.ivanschuetz.aacmlcodechallenge

/**
 * Sealed - this way type matches are restricted(no need of else/default clause)
 * initally also contained functionality to retrieve the depth of the tree but this turned to not be necessary.
 *
 * Created by ivanschuetz on 05.07.17.
 */
sealed class NavigationTreeNode

data class NavigationSection (
	val label: String,
	val children: List<NavigationTreeNode>
): NavigationTreeNode()

data class NavigationNode (
	val label: String,
	val children: List<NavigationTreeNode>
): NavigationTreeNode()

data class NavigationLink (
	val label: String,
	val url: String
): NavigationTreeNode()

data class Navigation (
	val items: List<NavigationTreeNode>
)