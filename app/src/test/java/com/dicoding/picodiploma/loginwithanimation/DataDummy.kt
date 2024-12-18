package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "id_$i",
                name = "User $i",
                description = "This is Description for story $i",
                photoUrl = "https://example.com/photo/$i.jpg",
                createdAt = "2023-10-13T00:00:00Z",
                lat = -6.0 + i,
                lon = 100.0 + i

            )
            items.add(story)
        }
        return items
    }
}