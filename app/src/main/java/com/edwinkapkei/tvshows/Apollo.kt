package com.edwinkapkei.tvshows

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.builder()
        .serverUrl("http://192.168.2.101:4000")
        .build()