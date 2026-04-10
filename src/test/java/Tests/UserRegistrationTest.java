package Tests;

import RequestBuilder.requestBuilder;
import com.github.javafaker.Faker;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserRegistrationTest {

    static String registeredEmail;
    @Test (priority = 0)
    public void adminLoginTest(){

        requestBuilder.loginUserResponse("spare@admin.com", "@12345678")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "adminLoginTest")
    public void userRegistration(){
        registeredEmail = Faker.instance().internet().emailAddress();
        requestBuilder.registerUserResponse("Katlego","Test",registeredEmail,"Kat@1234", "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526")
                .then()
                .log().all()
                .assertThat()
                .statusCode(201)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "userRegistration")
    public void approveUserRegistration(){
        requestBuilder.approveUserRegistrationResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "userRegistration")
    public void updateUserRoleTest(){
        requestBuilder.updateUserRoleResponse("admin")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "approveUserRegistration")
    public void userLoginTest(){

        requestBuilder.loginUserResponse(registeredEmail, "Kat@1234")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test (dependsOnMethods = "userLoginTest")
    public void deleteUserTest(){
        requestBuilder.deleteUserResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }


}
