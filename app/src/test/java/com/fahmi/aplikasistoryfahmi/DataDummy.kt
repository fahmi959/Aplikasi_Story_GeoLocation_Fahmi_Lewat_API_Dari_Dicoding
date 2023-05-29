package com.fahmi.aplikasistoryfahmi

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoryResponse> {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val story = StoryResponse(
                i.toString(),
                "name $i",
                "description $i",
                "photoUrl $i",
                "createdAt $i",
                i.toFloat(),
                i.toFloat()
            )
            items.add(story)
        }
        return items
    }
}
