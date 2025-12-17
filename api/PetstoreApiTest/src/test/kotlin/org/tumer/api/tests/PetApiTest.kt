package org.tumer.api.tests

import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.tumer.api.infrastructure.BaseTest
import org.tumer.model.Category
import org.tumer.model.Pet
import org.tumer.model.Tag
import org.tumer.model.ApiResponse
import java.io.File
import kotlin.random.Random

@Epic("Petstore API")
@Feature("Pet Endpoint Operations")
class PetApiTest : BaseTest() {

    private lateinit var testPet: Pet

    @BeforeEach
    fun setup() {
        val randomId = Random.nextLong(10000, 99999)
        testPet = Pet(
            id = randomId,
            category = Category(id = 1, name = "Dogs"),
            name = "Rex_$randomId",
            photoUrls = listOf("url1", "url2"),
            tags = listOf(Tag(id = 1, name = "tag1")),
            status = "available"
        )
    }

    @Test
    @Story("Create Pet")
    @Description("Verify that a new pet can be added to the store")
    fun `should create a new pet`() {
        val response = petApi.createPet(testPet)
        assertThat(response.statusCode).isEqualTo(200)
        val createdPet = response.`as`(Pet::class.java)
        assertThat(createdPet).isEqualTo(testPet)
    }

    @Test
    @Story("Get Pet")
    @Description("Verify that a pet can be retrieved by ID")
    fun `should get pet by id`() {
        petApi.createPet(testPet)
        val response = petApi.getPetById(testPet.id!!)
        assertThat(response.statusCode).isEqualTo(200)
        val fetchedPet = response.`as`(Pet::class.java)
        assertThat(fetchedPet).isEqualTo(testPet)

    }

    @Test
    @Story("Update Pet")
    @Description("Verify that an existing pet can be updated")
    fun `should update an existing pet`() {
        petApi.createPet(testPet)

        val updatedPet = testPet.copy(name = "Rex_Updated", status = "sold")
        val petBeforeUpdate = petApi.updatePet(updatedPet)
        assertThat(petBeforeUpdate.statusCode).isEqualTo(200)
        
        val returnedPet = petBeforeUpdate.`as`(Pet::class.java)
        assertThat(returnedPet.name).isEqualTo("Rex_Updated")
        assertThat(returnedPet.status).isEqualTo("sold")

        val petAfterUpdate = petApi.getPetById(testPet.id!!)
        assertThat(petAfterUpdate.statusCode).isEqualTo(200)
        val fetchedPet = petAfterUpdate.`as`(Pet::class.java)

        assertThat(fetchedPet).isNotEqualTo(testPet)
        assertThat(fetchedPet).isEqualTo(updatedPet)
    }

    @Test
    @Story("Find Pets")
    @Description("Verify that pets can be found by status")
    fun `should find pets by status`() {
        val response = petApi.findPetsByStatus("available")
        
        assertThat(response.statusCode).isEqualTo(200)
        
        val pets = response.jsonPath().getList("", Pet::class.java)
        assertThat(pets).isNotEmpty
    }

    @Test
    @Story("Delete Pet")
    @Description("Verify that a pet can be deleted AND verify negative case: Get deleted pet")
    fun `should delete a pet`() {
        petApi.createPet(testPet)

        val deleteResponse = petApi.deletePet(testPet.id!!)
        assertThat(deleteResponse.statusCode).isEqualTo(200)

        val getResponse = petApi.getPetById(testPet.id!!)
        assertThat(getResponse.statusCode).isEqualTo(404)
        
        val errorMessage = getResponse.jsonPath().getString("message")
        assertThat(errorMessage).isEqualTo("Pet not found")
    }

    @Test
    @Story("Negative Tests")
    @Description("Verify behavior when retrieving a non-existent pet")
    fun `should return 404 for non-existent pet`() {
        val nonExistentId = 99999999999
        val response = petApi.getPetById(nonExistentId)
        
        assertThat(response.statusCode).isEqualTo(404)
        assertThat(response.jsonPath().getString("message")).isEqualTo("Pet not found")
    }

    @Test
    @Story("Upload Image")
    @Description("Verify that an image can be uploaded for a pet")
    fun `should upload image for a pet`() {
        petApi.createPet(testPet)

        val file = File.createTempFile("test-image", ".png")
        file.writeBytes(ByteArray(10) { 0 }) // Dummy content
        file.deleteOnExit()

        val additionalMetadata = "Test Metadata"

        val response = petApi.uploadImage(testPet.id!!, additionalMetadata, file)

        assertThat(response.statusCode).isEqualTo(200)

        val apiResponse = response.`as`(ApiResponse::class.java)

        SoftAssertions.assertSoftly {
            assertThat(apiResponse.code).isEqualTo(200)
            assertThat(apiResponse.message).contains(additionalMetadata)
            assertThat(apiResponse.message).contains("File uploaded to")
        }
    }

    
    
}
