import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addPet_test() {
        Category dogCategory = new Category(66, "Dogs");

        System.out.println("Test is in a process...");

        Pet PetToAdd = Pet.builder()
                .id(new Random().nextInt(3))
                .category(dogCategory)
                .name("Sharick")
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

        long id = PetToAdd.getId();

        Response getNewPetInfo = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}");


     //   Pet idPetResponse = getNewPetInfo.as(Pet.class);

        Assert.assertEquals("Name doesn't match", PetToAdd.getName(), newPet.getName());
    }


    @Test
    public void createUser_test() {
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

        User newUserEmail = getNewUserEmail.as(User.class);

        Assert.assertEquals("User is not found", newUserToAdd.getUsername(), newUserEmail.getUsername());
    }
}
