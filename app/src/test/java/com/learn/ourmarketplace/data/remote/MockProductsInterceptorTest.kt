package com.learn.ourmarketplace.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MockProductsInterceptorTest {
    @Test
    fun returnsTwoHundredProductsThroughRetrofit() = runBlocking {
        val server = MockWebServer()
        server.start()
        try {
            val gson = GsonBuilder().create()
            val client = OkHttpClient.Builder()
                .addInterceptor(MockProductsInterceptor(gson))
                .build()
            val api = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ProductsApi::class.java)

            val products = api.getProducts()

            assertEquals(200, products.size)
            assertEquals((1..200).toList(), products.map { it.id })
            assertTrue(products.all { it.title.isNotBlank() && it.image.startsWith("https://") })
        } finally {
            server.shutdown()
        }

        @Test
        fun keepsCreatedProductInApiListForTheCurrentProcess() = runBlocking {
            val server = MockWebServer()
            server.start()
            try {
                val gson = GsonBuilder().create()
                val client = OkHttpClient.Builder()
                    .addInterceptor(MockProductsInterceptor(gson))
                    .build()
                val api = Retrofit.Builder()
                    .baseUrl(server.url("/"))
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(ProductsApi::class.java)
                val created = ProductDto(
                    id = -1,
                    title = "Local item",
                    description = "Created while offline",
                    price = 12.5,
                    image = "file:///data/user/0/example/files/item.image"
                )

                val response = api.createProduct(created)
                val retryResponse = api.createProduct(created)
                val products = api.getProducts()

                assertEquals(created, response)
                assertEquals(created, retryResponse)
                assertEquals(201, products.size)
                assertEquals(created, products.last())
            } finally {
                server.shutdown()
            }
        }
    }
}
