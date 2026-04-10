package Basic;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class UserRegistration {

    static String authToken;
    static String userId;
    static String registeredEmail;
    static String userRole;
    static String userName;
    static String baseURL = "https://ndosiautomation.co.za";
    @Test
    public void adminLoginTest() {

        //log in as admin
        String apiPath = "/APIDEV/login";
        String payload = "{\n" +
                "    \"email\": \"spare@admin.com\",\n" +
                "    \"password\": \"@12345678\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payload)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");
        authToken = response.jsonPath().getString("data.token");

    }

    @Test(priority = 2)
    public void registerUser(){

        String apiPath = "/APIDEV/register";
        registeredEmail = Faker.instance().internet().emailAddress();
        String payload = String.format( "{\n" +
                "    \"firstName\": \"Kat\",\n" +
                "    \"lastName\": \"Test\",\n" +
                "    \"email\": \"%s\",\n" +
                "    \"password\": \"Kat@1234\",\n" +
                "    \"confirmPassword\": \"Kat@1234\",\n" +
                "    \"phone\": \"\",\n" +
                "    \"groupId\": \"1deae17a-c67a-4bb0-bdeb-df0fc9e2e526\"\n" +
                "}", registeredEmail);

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(payload)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 201, "Status code should be 201");
        userId = response.jsonPath().getString("data.id");
        System.out.println("Registered User ID: " + userId);
    }

    @Test(priority = 3)
    public void approveUserRegistration(){

        String apiPath = "/APIDEV/admin/users/"+userId+"/approve";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .put().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");

    }

    @Test(priority = 4)
    public void updateUserRole(){

        String apiPath = "/APIDEV/admin/users/"+userId+"/role";
        String payload = "{\n" +
                "    \"role\": \"admin\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(payload)
                .log().all()
                .put().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");
        userRole = response.jsonPath().getString("data.role");
        System.out.println("Updated User Role: " + userRole);

    }

    @Test(priority = 5)
    public void userLoginTest() {

        String apiPath = "/APIDEV/login";
        String payload = String.format( "{\n" +
                "    \"email\": \"%s\",\n" +
                "    \"password\": \"Kat@1234\"\n" +
                "}", registeredEmail);

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payload)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");
        userName = response.jsonPath().getString("data.user.firstName");
        System.out.println("Logged in User Name: " + userName);

    }

    @AfterClass
    public void deleteUser(){

        String apiPath = "/APIDEV/admin/users/"+userId;

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .delete().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");
        System.out.println("User with ID " + userId + " has been deleted.");

    }

}
