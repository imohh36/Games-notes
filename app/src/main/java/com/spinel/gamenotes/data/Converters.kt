package com.spinel.gamenotes.data

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromTodoList(list: List<TodoItem>?): String {
        if (list.isNullOrEmpty()) return "[]"
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("text", item.text)
                put("isDone", item.isDone)
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toTodoList(jsonString: String?): List<TodoItem> {
        if (jsonString.isNullOrBlank()) return emptyList()
        val list = mutableListOf<TodoItem>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TodoItem(
                        id = obj.optString("id", i.toString()),
                        text = obj.optString("text", ""),
                        isDone = obj.optBoolean("isDone", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return "[]"
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String> {
        if (jsonString.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    @TypeConverter
    fun fromNoteTagList(list: List<NoteTag>?): String {
        if (list.isNullOrEmpty()) return "[]"
        val array = JSONArray()
        list.forEach { tag ->
            val obj = JSONObject().apply {
                put("id", tag.id)
                put("name", tag.name)
                put("colorHex", tag.colorHex)
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toNoteTagList(jsonString: String?): List<NoteTag> {
        if (jsonString.isNullOrBlank()) return emptyList()
        val list = mutableListOf<NoteTag>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    NoteTag(
                        id = obj.optString("id", i.toString()),
                        name = obj.optString("name", ""),
                        colorHex = obj.optString("colorHex", "#10B981")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    @TypeConverter
    fun fromInternalTabList(list: List<NoteInternalTab>?): String {
        return NoteInternalTab.listToJson(list ?: emptyList())
    }

    @TypeConverter
    fun toInternalTabList(jsonString: String?): List<NoteInternalTab> {
        return NoteInternalTab.jsonToList(jsonString)
    }
}
