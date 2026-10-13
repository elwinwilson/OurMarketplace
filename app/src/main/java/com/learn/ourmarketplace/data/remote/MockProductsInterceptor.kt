package com.learn.ourmarketplace.data.remote

import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class MockProductsInterceptor(
    private val gson: Gson
) : Interceptor {
    private val products = (1..PRODUCT_COUNT).map(::mockProduct).toMutableList()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath != "/products") {
            return response(request, 404, "Not Found", """{"error":"Not found"}""")
        }

        return synchronized(products) {
            when (request.method) {
                "GET" -> response(request, 200, "OK", gson.toJson(products))
                "POST" -> createProduct(request, gson)
                else -> response(request, 405, "Method Not Allowed", """{"error":"Method not allowed"}""")
            }
        }
    }

    private fun createProduct(request: okhttp3.Request, gson: Gson): Response {
        val body = request.body ?: return response(
            request,
            400,
            "Bad Request",
            """{"error":"Product body is required"}"""
        )
        val buffer = Buffer()
        body.writeTo(buffer)
        val product = try {
            gson.fromJson(buffer.readUtf8(), ProductDto::class.java)
        } catch (_: JsonParseException) {
            null
        } ?: return response(request, 400, "Bad Request", """{"error":"Invalid product"}""")

        val existingProduct = products.firstOrNull { it.id == product.id }
        if (existingProduct != null && existingProduct != product) {
            return response(request, 409, "Conflict", """{"error":"Product already exists"}""")
        }
        if (existingProduct == null) products.add(product)
        val status = if (existingProduct == null) 201 else 200
        val message = if (existingProduct == null) "Created" else "OK"
        return response(request, status, message, gson.toJson(product))
    }

    private fun response(
        request: okhttp3.Request,
        code: Int,
        message: String,
        body: String
    ) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message(message)
        .header("Content-Type", "application/json")
        .body(body.toResponseBody(JSON_MEDIA_TYPE))
        .build()

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
