package com.learn.ourmarketplace.data.remote

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockProductsInterceptor(
    gson: Gson
) : Interceptor {
    private val responseJson = gson.toJson((1..PRODUCT_COUNT).map(::mockProduct))

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath != "/products") {
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(404)
                .message("No mock response for ${request.url.encodedPath}")
                .body("""{"error":"Not found"}""".toResponseBody(JSON_MEDIA_TYPE))
                .build()
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .header("Content-Type", "application/json")
            .body(responseJson.toResponseBody(JSON_MEDIA_TYPE))
            .build()
    }

    private fun mockProduct(id: Int) = ProductDto(
        id = id,
        title = PRODUCT_NAMES[(id - 1) % PRODUCT_NAMES.size] + " #$id",
        description = "A carefully selected marketplace item, made for everyday use. Item $id includes quality materials and thoughtful details.",
        price = ((id * 137) % 9000 + 999) / 100.0,
        image = "https://picsum.photos/seed/marketplace-$id/640/640"
    )

    private companion object {
        const val PRODUCT_COUNT = 200
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        val PRODUCT_NAMES = listOf(
            "Everyday Backpack",
            "Wireless Headphones",
            "Ceramic Coffee Set",
            "Classic Sneakers",
            "Desk Lamp",
            "Travel Bottle",
            "Cotton Tote",
            "Minimal Watch",
            "Knit Sweater",
            "Leather Wallet"
        )
    }
}
