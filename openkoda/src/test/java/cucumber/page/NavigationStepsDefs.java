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
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static cucumber.page.SpringStepsDefs.baseUrl;


public class NavigationStepsDefs extends StepsBase {

    AdminPage page = new AdminPage(driver);

    @Given("I am logged as user {string} with password {string}")
    public void i_am_logged_as_user_with_password(String username, String password) {
        driver.get(baseUrl + "/login");
        page.doLogin(username, password);
    }

    @When("I click {string} on the left menu")
    public void i_click_on_the_left_menu(String menuItemLabel) {
        page.waitFor(driver.findElement(By.linkText(menuItemLabel))).click();
    }

    @And("I click {string} in admin menu")
    public void i_click_in_admin_menu(String adminPageLinkLabel) {
        String xpathExpression = "//div[@id=\"collapseAdmin\"]//a[@data-sidebar-menu-item=\""+adminPageLinkLabel+"\"]";
        page.waitFor(driver.findElement(By.xpath(xpathExpression))).click();
    }

    @When("I click on a {string} button")
    public void iClickAButton(String buttonLabel) {
        page.waitFor(driver.findElement(By.partialLinkText(buttonLabel))).click();

    }


    @When("I enter {string} page")
//    NOT WORKING :|
    public void iEnterPage(String pageURL)
    {
        driver.get(baseUrl + pageURL);
    }

    @Then("I am logged as {string}")
    public void iAmLoggedAs(String userName) {
        WebElement user = page.waitFor(driver.findElement(By.id("userDropdown")));
        Assert.assertEquals(userName, user.getText());
    }

    @And("I see existing project named {string}")
    public void iSeeExistingProject(String projectName)
    {
        try {
            List<WebElement> element = driver.findElement(By.className("table-responsive")).findElements(By.linkText(projectName));
            Assert.assertTrue(0 < element.size());
        }
        catch (NoSuchElementException e)
        {
            Assert.assertTrue(false);
        }

    }

    @And("I remove project named {string}")
    public void iRemoveProject(String projectName)
    {
        WebElement button = page.waitFor(driver.findElement(By.xpath("//button[contains(@class, 'btn btn-sm btn-danger')]")));
        {
            page.click(button);
            Alert alert = driver.switchTo().alert();
            alert.accept();
        }

    }

    @Then("I should not see project named {string}")
    public void iShouldNotSeeProject(String projectName)
    {
        try {
            page.sleep(2000);
            List<WebElement> element = driver.findElement(By.className("table-responsive")).findElements(By.linkText(projectName));
            Assert.assertEquals(0, element.size());
        }
        catch (NoSuchElementException e)
        {
            Assert.assertTrue(true);
        }

    }


    @When("my limit is {int} projects")
    public void myProjectsLimit(int n)
    {
        List<WebElement> elements = driver.findElement(By.id("content")).findElements(By.cssSelector("span"));


        elements = elements.stream()
                .filter(elem -> elem.getText().contains("Max Projects"))
                .collect(Collectors.toList());

        String webElemText = elements.get(0).getText();
        int maxScanProjects = Character.getNumericValue(webElemText.charAt(webElemText.length()-1));

        Assert.assertEquals(n, maxScanProjects);
    }

    @And("Link {string} is not active")
    public void linkIsNotActive(String linkLabel)
    {

        WebElement link = page.waitFor(driver.findElement(By.className("disabled")));
        WebElement link2 = page.waitFor(driver.findElement(By.partialLinkText(linkLabel)));
        Assert.assertEquals(link, link2);

    }

    @And("I wait {int} seconds")
    public void iWaitNSeconds(int n)
    {
        page.sleep(n*1000);
    }

    @When ("I enter my profile")
    public void iEnterMyProfile()
    {
        page.waitFor(driver.findElement(By.id("userDropdown"))).click();
        page.waitFor(driver.findElement(By.xpath("//*[@aria-labelledby='userDropdown']//a[@href='/html/user/10012/settings']"))).click();
    }




}
