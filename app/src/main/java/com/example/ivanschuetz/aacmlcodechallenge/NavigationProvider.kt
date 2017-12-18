package com.example.ivanschuetz.aacmlcodechallenge

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Created by ivanschuetz on 18.12.17.
 */
class NavigationProvider {

	fun getNavigationEntries(callback: (Result<Navigation>) -> Unit) {

		val client = OkHttpClient()

		val request = Request.Builder()
			.url("https://mytoysiostestcase1.herokuapp.com/api/navigation")
			.header("x-api-key", "hz7JPdKK069Ui1TRxxd1k8BQcocSVDkj219DVzzD")
			.build()

		client.newCall(request).enqueue(object : Callback {

			override fun onFailure(call: Call, e: IOException) {
				callback(Error("Couldn't retrieve navigation: $e"))
			}

			override fun onResponse(call: Call, response: Response) {
				val payload = response.body()?.string()

				val gsonBuilder = GsonBuilder()
				gsonBuilder.registerTypeAdapter(Navigation::class.java, NavigationDeserializer())

				val gson = gsonBuilder.create()
				val type = object : TypeToken<Navigation>(){}.type
				val fromJson = gson.fromJson<Navigation>(payload, type)

				callback(Success(fromJson))
			}
		})
	}
}


class NavigationDeserializer : JsonDeserializer<Any> {
	@Throws(JsonParseException::class)

	// Unexpected types don't crash the app, but are logged - idea here is to have a logger that uploads error reports
	// so they don't go unnoticed
	private fun deserializeNavigationItem(jsonObj: JsonObject): NavigationTreeNode? {

		// The field names should be constants, or maybe something more clever via enums/sealed classes.

		val objectTypeStr = jsonObj.get("type").asString
		val label = jsonObj.get("label").asString

		return when (objectTypeStr) {
			"section" -> {
				val jsonChildren = jsonObj.getAsJsonArray("children")
				NavigationSection(label = label, children = jsonChildren.mapNotNull { deserializeNavigationItem(it
					.asJsonObject) })
			}
			"node" -> {
				val jsonChildren = jsonObj.getAsJsonArray("children")
				NavigationNode(label = label, children = jsonChildren.mapNotNull { deserializeNavigationItem(it.asJsonObject) })
			}
			"link" ->  {
				val url = jsonObj.getAsJsonPrimitive("url").asString
				NavigationLink(label = label, url = url)
			}
			else -> {
				Log.e("Error", "Not handled item type: $objectTypeStr. Ignoring")
				null
			}
		}
	}

	override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): Navigation {

		val jsonData = je.asJsonObject.getAsJsonArray("navigationEntries")

		val items = jsonData.mapNotNull { navigationEntry ->
			deserializeNavigationItem(navigationEntry.asJsonObject)
		}

		return Navigation(items = items)
	}
}