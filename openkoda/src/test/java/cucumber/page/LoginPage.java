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

import cucumber.common.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class LoginPage extends BasePage {

    public void doLogin(String username, String password) {
        this.waitFor(this.username).click();
        this.username.sendKeys(username);
        this.password.click();
        this.password.sendKeys(password);
        this.submitButton.click();
    }


    @FindBy(how = How.NAME, using = "username")
    public WebElement username;
    @FindBy(how = How.NAME, using = "password")
    public WebElement password;
    @FindBy(how = How.NAME, using = "organization_search")
    public WebElement organization_search;
    @FindBy(how = How.ID, using = "errorMessage")
    public WebElement errorMessage;

    @FindBy(how = How.CSS, using = "button.btn-success")
    public WebElement successButton;

    @FindBy(how = How.CSS, using = "button[type='submit']")
    public WebElement submitButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

}
