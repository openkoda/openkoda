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

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.form.TestModel;
import cucumber.common.StepsBase;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;


//@CucumberStepsDefinition
public class FormsStepsDefs extends StepsBase implements LoggingComponent {

    FormPage page = new FormPage(driver);

    @And("I open {string} page")
    public void iOpenPage(String pageUrl) {
        System.out.println(SpringStepsDefs.baseUrl);
        driver.get(SpringStepsDefs.baseUrl + pageUrl);
        page.sleep(2000);
    }

    @When("I open {string} page with url parameter {string}")
    public void iOpenPageUrlWithParameter(String pageUrl, String pageSize) {
        driver.get(SpringStepsDefs.baseUrl + pageUrl + "?" + pageSize);
        page.sleep(2000);
    }

    @When("I fill {string} with values {string}")
    public void fillWithValues(String formName, String urlEncodedParameters) {
        List<NameValuePair> params = URLEncodedUtils.parse(urlEncodedParameters, Charset.defaultCharset());
        FrontendMappingDefinition f = TestModel.forms.get(formName);
        for (NameValuePair p : params) {
            FrontendMappingFieldDefinition ff = f.findField(p.getName());
            try {
                page.setValueFieldDefinition(ff, p.getValue());
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @And("I click {string} button on {string}")
    public void iClickButtonOn(String buttonLabel, String formName) {
        //FIXME: does not work
        WebElement button = page.waitFor(driver.findElement(By.className(formName)).findElement(By.tagName("button")));
        button.click();
    }

    @And("I should find {string} in the visible table")
    public void iShouldFindInTheVisibleTable(String rowValues) {
        String[] values = StringUtils.split(rowValues, ';');
        WebElement table = page.waitFor(driver.findElement(By.className("table-responsive")));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        boolean foundRow = false;
        for (WebElement we : rows) {
            List<WebElement> cells = we.findElements(By.tagName("td"));
            foundRow |= valuesMatch(values, cells);
        }
        Assert.assertTrue("Row values not found: " + rowValues, foundRow);
    }

    @And("I submit form {string}")
    public void iSubmitForm(String formName) {
        WebElement form;
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfElementLocated(By.className(formName)));
            form = driver.findElement(By.className(formName));
        } catch (Exception e) {
            System.out.println("Couldn't find className " + formName + ". Trying again. Page Source for debug:");
            System.out.println(driver.getPageSource());
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfElementLocated(By.className(formName)));
            form = driver.findElement(By.className(formName));
        }
        WebElement button = page.waitFor(form.findElement(By.className("btn-submit")));
        page.click(button);
        page.sleep(2000);
    }

    @And("I click {string} column header")
    public void iClickColumnHeader(String property) {
        String elementId = "sort_" + property;
        WebElement header = driver.findElement(By.id(elementId));
        page.click(header);
    }

    @Then("I should find data sorted {string} by {string} column")
    public void i_should_find_data_sorted_by_column(String sortType, String property) {
        WebElement table = page.waitFor(driver.findElement(By.className("table-responsive")));
        WebElement headerRow = table.findElement(By.xpath("//table//tr[th]"));
        int propertyColumn = findPropertyIndex(property, headerRow);
        List<WebElement> dataRows = table.findElements(By.xpath("//table//tr[not(th)]"));
        Assert.assertTrue(checkIfColumnSorted(dataRows, propertyColumn, sortType));
    }

    @And("I click {string} arrow")
    public void iClickArrow(String paginationLinkText) {
        WebElement button = page.waitFor(driver.findElement(By.partialLinkText(paginationLinkText)));
        button.click();
        page.sleep(2000);
    }

    @And("I should see {string} arrow")
    public void iShouldSeeArrow(String paginationLinkText) {
        try {
            WebElement button = page.waitFor(driver.findElement(By.partialLinkText(paginationLinkText)));
            Assert.assertNotNull(button);
        }catch (NoSuchElementException e){
            // If no pagination link was found on the page test passed
            Assert.assertTrue(false);
        }
    }

    @And("I should see pagination links list with class {string}")
    public void iShouldSeePaginationLinksListWithClass(String paginationId) {
        try {
            WebElement link = page.waitFor(driver.findElement(By.className(paginationId)));
            Assert.assertNotNull(link);
        }catch (NoSuchElementException e){
            // If no pagination link was found on the page test failed
            Assert.assertTrue(false);
        }
    }
    @And("I should not see pagination links in list with id {string}")
    public void iShouldNotSeePaginationLinksListWithId(String paginationId) {
        try {
            WebElement link = page.waitFor(driver.findElement(By.id(paginationId)));
            Assert.assertNull(link);
        }catch (NoSuchElementException e){
            // If no pagination link was found on the page test failed
            Assert.assertTrue(true);
        }
    }

    @And("I should not see left menu bar with {string}")
    public void iShouldNotSeeLeftMenuBar(String label) {
        try {
            WebElement link = page.waitFor(driver.findElement(By.name(label)));
            Assert.assertNull(link);
        }catch (NoSuchElementException e){
            Assert.assertTrue(true);
        }
    }

    @And("I open {string} page with page size URL parameter equal to number of records in {string} column")
    public void iOpenPageWithPageSizeUrlParameterEqualToNumberOfRecordInColumn(String pageUrl, String column) {
        long pageSize = SpringStepsDefs.staticTestDataLoader.getNumberOfRecordsInDatabase(column);
        driver.get(SpringStepsDefs.baseUrl + pageUrl + "?" + column + "_size=" + pageSize);
        page.sleep(2000);
    }

    @Given("I am expecting no less than {string} number of records in {string} column")
    public void iAmExpectingNoLessThanNumberOfRecordsInColumn(String numberOfRecords, String column) {
        long numberOfrecordsInColumn = SpringStepsDefs.staticTestDataLoader.getNumberOfRecordsInDatabase(column);
        debug(String.valueOf(numberOfrecordsInColumn));
        if(numberOfrecordsInColumn < Integer.parseInt(numberOfRecords)) {
            SpringStepsDefs.staticTestDataLoader.createRecordsForColumn(column, Integer.parseInt(numberOfRecords) - numberOfrecordsInColumn);
        }
    }

    private boolean checkIfColumnSorted(List<WebElement> dataRows, int sortedColumn, String sortType) {
        if (dataRows.size() < 2) {
            return true;
        }
        String prevElement = dataRows.get(0).findElement(By.xpath("//td[" + (sortedColumn + 1) + "]")).getText();
        dataRows.remove(0);
        for (WebElement elem : dataRows) {
            String current = elem.findElement(By.xpath("//td[" + (sortedColumn + 1) + "]")).getText();
            if (compareElements(current, prevElement, sortType) >= 0) {
                prevElement = current;
            } else {
                return false;
            }
        }
        return true;
    }

    private int compareElements(String current, String prevElement, String sortType) {
        if (sortType.equals("lexic")) {
            return current.compareTo(prevElement);
        }
        if (sortType.equals("linear")) {
            if (current.contains("/")) {
                current = StringUtils.substringAfterLast(current, "/");
                prevElement = StringUtils.substringAfterLast(prevElement, "/");
            }
            Integer currentInt = Integer.parseInt(current);
            Integer prevInt = Integer.parseInt(prevElement);
            return currentInt.compareTo(prevInt);
        }
        throw new RuntimeException("Invalid sort type");
    }

    private int findPropertyIndex(String property, WebElement headerRow) {
        List<WebElement> header = headerRow.findElements(By.tagName("th"));
        int index = 0;
        for (; index < header.size(); index++) {
            if (header.get(index).getAttribute("class").contains(property)) {
                return index;
            }
        }
        return -1;
    }

    private boolean valuesMatch(String[] values, List<WebElement> cells) {
        try {
            Iterator<WebElement> it = cells.iterator();
            WebElement element = it.next();
            for (String v : values) {
                if ("?".equals(v)) {
                    element = it.next();
                    continue;
                }
                while (!element.getText().contains(v)) {
                    element = it.next();
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
