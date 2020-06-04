package com.studio29.gharibyan.peoplearoundmemap.repositry.services.algolia

import com.algolia.search.saas.Client

class Search {

    val client = Client("15CIZT3FLN","f733248fb9327880bc95322590b4d4d1")
    val index = client.getIndex("people_around_me_MAP")

}