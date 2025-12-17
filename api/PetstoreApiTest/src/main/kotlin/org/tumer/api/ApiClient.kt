package org.tumer.api

import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification

object ApiClient {
    private const val BASE_URI = "https://petstore.swagger.io/v2"

    val requestSpec: RequestSpecification = RequestSpecBuilder()
        .setBaseUri(BASE_URI)
        .setContentType(ContentType.JSON)
        .addFilter(io.qameta.allure.restassured.AllureRestAssured())
        .log(LogDetail.ALL)
        .build()
}
