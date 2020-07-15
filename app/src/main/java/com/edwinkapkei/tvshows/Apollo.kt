package com.edwinkapkei.tvshows

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.builder()
        .serverUrl("http://192.168.43.40:4000")
        .build()