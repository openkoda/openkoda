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

package cucumber.common;

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage implements LoggingComponent {
    protected final WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void setValueFieldDefinition(FrontendMappingFieldDefinition field, String value) {

        String expr = "//*[starts-with(@name, 'dto') and contains(@name, '"+field.getName()+"')]";
        WebElement element;
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(expr)));
            element = this.driver.findElement(By.xpath(expr));
        } catch (Exception e) {
            System.out.println("Couldn't find expr " + expr + ". Trying again. Page Source for debug:");
            System.out.println(driver.getPageSource());
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(expr)));
            element = this.driver.findElement(By.xpath(expr));
        }
        switch (field.type) {
            case dropdown_with_disable:
            case dropdown:
            case section_with_dropdown:
                Select s = new Select(element);
                s.selectByValue(value);
                break;
            case radio_list:
                element.findElement(By.xpath("//*[@value=\""+value+"\"]")).click();
                break;
            default:
                element.click();
                element.clear();
                element.sendKeys(value);
        }
    }

    public WebElement waitFor(WebElement webElement){
        sleep(); //Temporary solution, should be improved
//        driverWait().until(ExpectedConditions.elementToBeClickable(webElement));
        return webElement;
    }

    public List<WebElement> waitFor(List<WebElement> webElements) {
        sleep(); //Temporary solution, should be improved
        return webElements;
    }

    public WebElement click(WebElement webElement) {
        waitFor(webElement);
        webElement.click();
        return webElement;
    }

    public void sleep() {
        sleep(2000);
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            error("[sleep]", e);
        }
    }

    private WebDriverWait driverWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(5));
    }

}
