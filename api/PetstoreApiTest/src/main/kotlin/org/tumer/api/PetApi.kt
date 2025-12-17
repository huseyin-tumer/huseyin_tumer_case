package org.tumer.api

import io.qameta.allure.Step
import io.restassured.RestAssured.given
import io.restassured.response.Response
import org.tumer.model.Pet
import java.io.File

class PetApi {

    @Step("Create a new pet: {pet}")
    fun createPet(pet: Pet): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .body(pet)
            .`when`()
            .post("/pet")
    }

    @Step("Update an existing pet: {pet}")
    fun updatePet(pet: Pet): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .body(pet)
            .`when`()
            .put("/pet")
    }

    @Step("Get pet by id: {petId}")
    fun getPetById(petId: Long): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .`when`()
            .get("/pet/$petId")
    }

    @Step("Delete pet by id: {petId}")
    fun deletePet(petId: Long): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .`when`()
            .delete("/pet/$petId")
    }

    @Step("Find pets by status: {status}")
    fun findPetsByStatus(status: String): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .queryParam("status", status)
            .`when`()
            .get("/pet/findByStatus")
    }

    @Step("Upload image for pet id={petId} with metadata={additionalMetadata}")
    fun uploadImage(petId: Long, additionalMetadata: String, file: File): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .contentType("multipart/form-data")
            .pathParam("petId", petId)
            .formParam("additionalMetadata", additionalMetadata)
            .multiPart("file", file)
            .`when`()
            .post("/pet/{petId}/uploadImage")
    }
}
