package org.tumer.api

import io.restassured.RestAssured.given
import io.restassured.response.Response
import org.tumer.model.Pet

class PetApi {

    fun createPet(pet: Pet): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .body(pet)
            .`when`()
            .post("/pet")
    }

    fun updatePet(pet: Pet): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .body(pet)
            .`when`()
            .put("/pet")
    }

    fun getPetById(petId: Long): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .`when`()
            .get("/pet/$petId")
    }

    fun deletePet(petId: Long): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .`when`()
            .delete("/pet/$petId")
    }

    fun findPetsByStatus(status: String): Response {
        return given()
            .spec(ApiClient.requestSpec)
            .queryParam("status", status)
            .`when`()
            .get("/pet/findByStatus")
    }

    fun uploadImage(petId: Long, additionalMetadata: String, file: java.io.File): Response {
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
