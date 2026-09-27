package com.spinel.gamenotes.data

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class NoteInternalTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "الرئيسية",
    val content: String = "",
    val blocksJson: String = "[]"
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("content", content)
        put("blocksJson", blocksJson)
    }

    companion object {
        fun fromJson(obj: JSONObject): NoteInternalTab {
            return NoteInternalTab(
                id = obj.optString("id", UUID.randomUUID().toString()),
                title = obj.optString("title", "الرئيسية"),
                content = obj.optString("content", ""),
                blocksJson = obj.optString("blocksJson", "[]")
            )
        }

        fun listToJson(tabs: List<NoteInternalTab>): String {
            val array = JSONArray()
            tabs.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun jsonToList(json: String?): List<NoteInternalTab> {
            if (json.isNullOrBlank() || json == "[]") return emptyList()
            val list = mutableListOf<NoteInternalTab>()
            try {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }
    }
}
