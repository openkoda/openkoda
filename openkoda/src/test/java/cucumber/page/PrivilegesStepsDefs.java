package cucumber.page;

import com.openkoda.TestDataLoader;
import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.model.component.FrontendResource;
import com.openkoda.model.Organization;
import com.openkoda.model.Privilege;
import cucumber.common.StepsBase;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrivilegesStepsDefs extends StepsBase implements LoggingComponent {

    AdminPage page = new AdminPage(driver);

    @Autowired
    protected TestDataLoader testDataLoader;

    @Given("Frontend resources {string}")
    public void frontendResource(String name) {
        testDataLoader.createFrontendResource(name, "test.js", " ", FrontendResource.Type.JS);
    }

    @When("Create user {string} {string} with {string} privilege {string} and organization {string} {string}")
    public void createUserWithPrivilege(String userName, String password, String area, String privilege, String organization, String privilegeName) {
        Set<Enum> privileges = new HashSet<>();
        for (String p : privilege.split(";")) {
            privileges.add(Privilege.valueOf(p));
        }
        Organization org = testDataLoader.findOrganizationByName(organization);
        if (org == null) {
            org = testDataLoader.createOrganization(organization);
        }

        Tuple2[] tuple2 = new Tuple2[]{Tuples.of(privilegeName, org.getId())};
        if ("Organization".equals(area)) {
            testDataLoader.createOrganizationRole(privilegeName, privileges, true);
            testDataLoader.createUser(userName, password, userName + "@openkodatest.com", true, new String[]{"ROLE_USER"}, tuple2);

        } else {
            testDataLoader.createGlobalRole(privilegeName, privileges, true);
            testDataLoader.createUser(userName, password, userName + "@openkodatest.com", true, new String[]{"ROLE_USER"}, new Tuple2[]{});

        }

    }

    @And("I click {string} on the side menu")
    public void i_click_on_the_side_menu(String menuItemLabel) {
        page.waitFor(driver.findElement(By.partialLinkText(menuItemLabel))).click();
    }

    @And("I click {string} link")
    public void i_click(String linkName) {
        page.waitFor(driver.findElement(By.linkText(linkName))).click();
    }

    @Then("I should see test user email {string}")
    public void iShouldSeeTestUserEmail(String condition) {
        List<WebElement> we = driver.findElements(By.xpath("//div[@class=\"card-body\"]//div//div[contains(string(), \"E-mail\")]"));
        Assert.assertEquals(condition, we.size() > 0 ? "1" : "0");
    }

    @Then("I should see {string} forms")
    public void iShouldSeeForms(String forms) {
        List<WebElement> we;
        for (String form : forms.split(";")) {
            we = driver.findElements(By.xpath("//form[@class='" + form + "']"));
            Assert.assertTrue(we.size() > 0);
        }
    }

    @Then("I should see {string} pages {string}")
    public void iShouldSeePages(String pagePaths, String condition) {
        List<WebElement> we = driver.findElements(By.linkText(pagePaths));
        Assert.assertEquals(condition, we.size() > 0 ? "1" : "0");
    }

    @Then("I should see {string} button {string}")
    public void iShouldSeeLink(String text, String condition) {
        List<WebElement> we = driver.findElements(By.xpath("//button[contains(text(),'" + text + "')]"));
        Assert.assertEquals(condition, we.size() > 0 ? "1" : "0");
    }

    @And("I should find {string} in the side menu {string}")
    public void iShouldFindInTheSideMenu(String rowValues, String name) {
        List<String> values = List.of(StringUtils.split(rowValues + ";" + name, ';'));
        boolean condition = true;
        List<WebElement> menus = page.waitFor(driver.findElements(By.cssSelector("[class='collapse show']")));
        for (WebElement webElement : menus) {

            List<WebElement> rows = webElement.findElements(By.tagName("a"));
            for (WebElement we : rows) {
                if (!values.contains(we.getText())) {
                    condition = false;
                    break;
                }
            }
        }
        Assert.assertTrue("Elements found", condition);
    }

}
