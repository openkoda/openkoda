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

import cucumber.common.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class RegisterPage extends BasePage{

    public void fillRegisterForm(String firstName, String lastName, String email, String password, String confirmPassword) {
        this.waitFor(this.firstName).click();
        this.firstName.sendKeys(firstName);
        this.lastName.click();
        this.lastName.sendKeys(lastName);
        this.login.click();
        this.login.clear();
        this.login.sendKeys(email);
        this.password.click();
        this.password.clear();
        this.password.sendKeys(password);
        this.confirmPassword.click();
        this.confirmPassword.sendKeys(confirmPassword);
    }

    public void fillRegisterForm(String email, String password) {
        this.login.click();
        this.login.clear();
        this.login.sendKeys(email);
        this.password.click();
        this.password.clear();
        this.password.sendKeys(password);
    }

    public void submitRegister(){
        this.enabledSubmit.click();
    }

    @FindBy(how = How.NAME, using = "firstName")
    public WebElement firstName;
    @FindBy(how = How.NAME, using = "lastName")
    public WebElement lastName;
    @FindBy(how = How.NAME, using = "login")
    public WebElement login;
    @FindBy(how = How.NAME, using = "password")
    public WebElement password;
    @FindBy(how = How.ID, using = "confirmPassword")
    public WebElement confirmPassword;
    @FindBy(how = How.CSS, using = "button[type='submit'")
    public WebElement enabledSubmit;
    @FindBy(how = How.XPATH, using = "//*['Registration Successful']")
    public WebElement registeredMessage;
    @FindBy(how=How.LINK_TEXT, using = "Forgot Password?")
    public WebElement forgotPasswordLink;
    @FindBy(how=How.LINK_TEXT, using = "Already have an account? Login!")
    public WebElement loginLink;


    public RegisterPage(WebDriver driver) {
        super(driver);
    }
}
