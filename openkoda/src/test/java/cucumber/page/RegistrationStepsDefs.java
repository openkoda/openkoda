/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package cucumber.page;

import cucumber.common.StepsBase;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class RegistrationStepsDefs extends StepsBase {


    RegisterPage registerPage = new RegisterPage(driver);


    @When("I fill registration form with {string},{string},{string},{string},{string}")
    public void iFillRegistrationFormWith(String firstName, String lastName, String login, String password, String confirmPassword) {
        registerPage.fillRegisterForm(firstName, lastName, login, password, confirmPassword);
    }

    @When("I fill registration form with {string},{string}")
    public void iFillRegistrationFormWith(String login, String password) {
        registerPage.fillRegisterForm(login, password);
    }

    @When("I click on {string} link")
    public void iClickOnLink(String linkName) {
        switch (linkName){
            case "forgotPassword":
                registerPage.forgotPasswordLink.click();
                break;
            case "login":
                registerPage.loginLink.click();
                break;
        }
    }

    @And("I submit registration")
    public void iClickRegistration() {
        registerPage.submitRegister();
    }

    @Then("I should see {string} message")
    public void iShouldSeeMessageOnPage(String message) {
        Assert.assertTrue(registerPage.waitFor(registerPage.registeredMessage).getText().contains(message));
    }

    @Then("I should see {string} button")
    public void iShouldSeeButton(String buttonState) {
        boolean submitIsEnabled = registerPage.enabledSubmit.isEnabled();
        switch (buttonState) {
            case "disabled":
                Assert.assertFalse("Button is " + submitIsEnabled, registerPage.enabledSubmit.isEnabled());
                break;
            case "enabled":
                Assert.assertTrue("Button is " + submitIsEnabled, registerPage.enabledSubmit.isEnabled());
                break;
        }
    }


}
