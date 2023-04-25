/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;

public class AuthenticationStepsDefs extends StepsBase {

    LoginPage page = new LoginPage(driver);

    @Given("I am on {string} page")
    public void iAmOnPage(String pageName) {
        switch (pageName){
            case "login":
                driver.get(SpringStepsDefs.baseUrl + "/login");
                break;
            case "register":
                driver.get(SpringStepsDefs.baseUrl + "/register");
                break;
            default:
                driver.get(SpringStepsDefs.baseUrl + pageName);
        }
    }

    @Then("I should see {string} keyword in element with id {string}")
    public void iShouldSeeKeyword(String keyword, String webElemId)
    {
        Assert.assertTrue(page.waitFor(driver.findElement(By.id(webElemId))).getText().contains(keyword));
    }

    @Then("I should see {string} error message")
    public void iShouldSeeErrorMessage(String errorMessage) {
        Assert.assertEquals(errorMessage, page.waitFor(page.errorMessage).getText());
    }

    @When("I login as user {string} with password {string}")
    public void iLoginAsUserWithPassword(String username, String password) {
        page.doLogin(username, password);
    }

    @Then("I should see {string} page")
    public void iShouldSeePage(String pagePath) {
        Assert.assertEquals(SpringStepsDefs.baseUrl + pagePath, driver.getCurrentUrl());
    }

    @Then("I should see {string} parameter in page URL")
    public void iShouldSeeParameterInPageUrl(String urlParams) {
        Assert.assertTrue(driver.getCurrentUrl().contains(urlParams));
    }



}
