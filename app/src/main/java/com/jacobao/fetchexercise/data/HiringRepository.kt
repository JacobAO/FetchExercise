package com.jacobao.fetchexercise.data

import android.content.Context
import arrow.core.Either
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.jacobao.fetchexercise.R
import java.io.InputStreamReader

class HiringRepository {
    /**
     * Parses the hiring items JSON and returns a map of list IDs to lists of hiring items
     * with that list ID.
     */
    fun getHiringItemMap(context: Context): Either<LoadError, Map<Int, List<HiringItem>>> {
        return try {
            // open and parse the hiring JSON
            val inputStream = context.resources.openRawResource(R.raw.hiring)
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val listType = object : TypeToken<List<HiringItem>>() {}.type
            val list = gson.fromJson<List<HiringItem>>(reader, listType)

            // map each item by list ID and use SortedMap to keep keys sorted
            val map = sortedMapOf<Int, MutableList<HiringItem>>()
            list.forEach {
                // exclude values with no name
                if (!it.name.isNullOrBlank()) {
                    if (map[it.listId] == null) {
                        map[it.listId] = mutableListOf()
                    }
                    map[it.listId]?.add(it)
                }
            }

            // Sort each list's items by name.
            //
            // Although each item's name is just "Item <ID>" and it may appear that they are out of
            // order based on the number, I'm treating the name as any arbitrary string and simply
            // sorting in string order.
            map.keys.forEach { listId ->
                map[listId]?.sortBy { it.name }
            }

            Either.Right(map)

        } catch(e: JsonParseException) {
            Either.Left(LoadError("Failed to parse hiring items from JSON"))
        }
    }
}