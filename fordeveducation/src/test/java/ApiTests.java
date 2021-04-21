import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import entities.SuccessfulResponse;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addPet_test() throws InterruptedException {
        Category dogCategory = new Category(30, "Dogs");

        System.out.println("Test is in a process...");

        Pet PetToAdd = Pet.builder()
                .id(BigInteger.valueOf(new Random().nextInt(30)))
                .category(dogCategory)
                .name(RandomStringUtils.randomAlphabetic(5))
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("available")
                .build();

        System.out.println("Body to send: " + new Gson().toJson(PetToAdd));

        Response newPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(PetToAdd)
                .when()
                .post();

        Pet newPet = newPetResponse.as(Pet.class);

        System.out.println("response: " + newPetResponse.asString());

        BigInteger id = PetToAdd.getId();

        Response getNewPetInfo = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}");

        TimeUnit.SECONDS.sleep(4);
        Pet idPetResponse = getNewPetInfo.as(Pet.class);

        Assert.assertEquals("Name doesn't match", PetToAdd.getName(), newPet.getName());
    }


    @Test
    public void createUser_test() throws InterruptedException {
        User newUserToAdd = User.builder()
                .id(2)
                .username("Yulko")
                .firstName("Yulia")
                .lastName("Shatilova")
                .email("jshatilova92@gmail.com")
                .password("passWord1")
                .phone("0966716505")
                .userStatus(1)
                .build();

        System.out.println("Body to send: " + new Gson().toJson(newUserToAdd));

        Response newUserResponse = given()
                .baseUri(BASE_URL)
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(newUserToAdd)
                .when()
                .post();

        System.out.println("response: " + newUserResponse.asString());

        Assert.assertEquals("User is not found", 200, newUserResponse.getStatusCode());

        Response getNewUserEmail = given()
                .baseUri(BASE_URL)
                .pathParam("username", "Yulko")
                .when()
                .get("/user/{username}");

        TimeUnit.SECONDS.sleep(4);
        User newUserEmail = getNewUserEmail.as(User.class);

        Assert.assertEquals("User is not found", newUserToAdd.getEmail(), newUserEmail.getEmail());
    }

    @Test
    public void addPet_invalidID_test() throws InterruptedException {
        BigInteger b = new BigInteger("12345678901234567890");
        Category dogCategory = new Category(136, "Dogs");

        System.out.println("Test is in a process...");

        Pet petInvalidToAdd = Pet.builder()
                .id(b)
                .category(dogCategory)
                .name(RandomStringUtils.randomAlphabetic(5))
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("available")
                .build();

        System.out.println("Body to send: " + new Gson().toJson(petInvalidToAdd));

        Response newInvalidPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petInvalidToAdd)
                .when()
                .post();

        System.out.println("Response : " + newInvalidPetResponse.asString());

        TimeUnit.SECONDS.sleep(4);
        Assert.assertEquals("Status is wrong", 500, newInvalidPetResponse.getStatusCode());
        System.out.println(newInvalidPetResponse.getStatusLine());
        SuccessfulResponse notValid = newInvalidPetResponse.as(SuccessfulResponse.class);
        Assert.assertEquals("Message is wrong", "something bad happened", notValid.getMessage());

    }

    @Test
    public void addPet_andDelete_test() throws InterruptedException {
        Category mouseCategory = new Category(29, "Mouse");

        System.out.println("Test is in a process...");
        BigInteger petId = BigInteger.valueOf(new Random().nextInt(30));

        Pet petToAdd = Pet.builder()
                .id(petId)
                .category(mouseCategory)
                .name(RandomStringUtils.randomAlphabetic(5))
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("available")
                .build();

        System.out.println("Body to send: " + new Gson().toJson(petToAdd));

        Response newPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();

        Pet newPet = newPetResponse.as(Pet.class);

        System.out.println("response: " + newPetResponse.asString());

        Assert.assertEquals("Pet is not found", 200, newPetResponse.getStatusCode());

        BigInteger id = petToAdd.getId();

        Response deletePetResponse = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .delete("/pet/{petId}");

        TimeUnit.SECONDS.sleep(10);

        Assert.assertEquals("Delete operation was not successful", 200, deletePetResponse.getStatusCode());

        Response getDeletedPetIdResponse = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}");

        TimeUnit.SECONDS.sleep(10);

        Assert.assertEquals("Status doesn't match", 404, getDeletedPetIdResponse.getStatusCode());

    }

    @Test
    public void addUser_checkValidation_test() throws InterruptedException {
        User newUserToAdd = User.builder()
                .id(30)
                .username("user")
                .firstName("Vasiliy")
                .lastName("Petrov")
                .email("vasilij@gmail.com")
                .password("passWord1")
                .phone("0966716566")
                .userStatus(3)
                .build();

        System.out.println("Body to send: " + new Gson().toJson(newUserToAdd));

        Response newUserResponse = given()
                .baseUri(BASE_URL)
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(newUserToAdd)
                .when()
                .post();

        System.out.println("response: " + newUserResponse.asString());

        Assert.assertEquals("User is not found", 200, newUserResponse.getStatusCode());

        Response getNewUserResponse = given()
                .baseUri(BASE_URL)
                .pathParam("username", "user")
                .when()
                .get("/user/{username}")
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserSchema.json"))
                .extract()
                .response();

        TimeUnit.SECONDS.sleep(4);
        User newUser = getNewUserResponse.as(User.class);
    }


    @Test
    public void addPet_statusSold_test() throws InterruptedException {
        Category horseCategory = new Category(1, "Horse");

        System.out.println("Test is in a process...");
        BigInteger petId = new BigInteger("123");
        String petName = RandomStringUtils.randomAlphabetic(5);

        Pet petToAdd = Pet.builder()
                .id(petId)
                .category(horseCategory)
                .name(petName)
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("sold")
                .build();

        System.out.println("Body to send: " + new Gson().toJson(petToAdd));

        Response newPetResponse = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(petToAdd)
                .when()
                .post();


        Pet newPet = newPetResponse.as(Pet.class);

        System.out.println("response: " + newPetResponse.asString());

        Assert.assertEquals("Pet is not found", 200, newPetResponse.getStatusCode());

        TimeUnit.SECONDS.sleep(10);


        Response statusPetResponse = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/findByStatus/?status=sold");

      //  TimeUnit.SECONDS.sleep(10);

        List<Pet> petWithSoldStatus = Arrays.stream(statusPetResponse.as(Pet[].class))
                .filter(pet -> pet.getId().equals(petToAdd.getId()))
                .collect(Collectors.toList());


        Assert.assertEquals("Id is not unique", 1, petWithSoldStatus.size());
        Assert.assertEquals("There is no pet with such name", petToAdd.getName(), petWithSoldStatus.get(0).getName());

    }

    @Test
    public void getFreeId_test() {

        int freeId = 0;
        for (int i = 1; i <= 100; i++) {
            int responseAboutFreeId = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/pet/" + i)
                    .then().extract().statusCode();
            if (responseAboutFreeId != 200) {
                freeId++;
            }
        }
        System.out.println(freeId);
    }
}
