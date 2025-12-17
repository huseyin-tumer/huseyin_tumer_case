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
        
        SoftAssertions.assertSoftly {
            assertThat(createdPet.id).isEqualTo(testPet.id)
            assertThat(createdPet.name).isEqualTo(testPet.name)
            assertThat(createdPet.status).isEqualTo(testPet.status)
        }
    }

    @Test
    @Story("Get Pet")
    @Description("Verify that a pet can be retrieved by ID")
    fun `should get pet by id`() {
        // Ensure pet exists
        petApi.createPet(testPet)

        val response = petApi.getPetById(testPet.id!!)
        
        assertThat(response.statusCode).isEqualTo(200)
        
        val fetchedPet = response.`as`(Pet::class.java)
        
        SoftAssertions.assertSoftly {
            assertThat(fetchedPet.id).isEqualTo(testPet.id)
            assertThat(fetchedPet.name).isEqualTo(testPet.name)
            assertThat(fetchedPet.status).isEqualTo(testPet.status)
            assertThat(fetchedPet.photoUrls).isEqualTo(testPet.photoUrls)
            assertThat(fetchedPet.tags).isEqualTo(testPet.tags)
            assertThat(fetchedPet.category).isEqualTo(testPet.category)
        }
    }

    @Test
    @Story("Update Pet")
    @Description("Verify that an existing pet can be updated")
    fun `should update an existing pet`() {
        // Ensure pet exists
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

        SoftAssertions.assertSoftly {
            assertThat(fetchedPet.id).isEqualTo(updatedPet.id)
            assertThat(fetchedPet.name).isEqualTo(updatedPet.name)
            assertThat(fetchedPet.status).isEqualTo(updatedPet.status)
            assertThat(fetchedPet.photoUrls).isEqualTo(updatedPet.photoUrls)
            assertThat(fetchedPet.tags).isEqualTo(updatedPet.tags)
            assertThat(fetchedPet.category).isEqualTo(updatedPet.category)
        }


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
}
