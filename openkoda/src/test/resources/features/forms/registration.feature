Feature: Registration
  Background:
    Given I am on "register" page

  Scenario Outline: Check if registration errors disable Submit button
    When I fill registration form with "<firstName>","<lastName>","<login>","<password>","<confirmPassword>"
    Then I should see "<buttonState>" button
    Examples:
      | firstName | lastName | login         | password | confirmPassword | buttonState |
      |           |          |               |          |                 | disabled    |
      | aaa       | aaa      | aaa@gmail.com | aaa      |                 | disabled    |
      | aaa       | aaa      | aaa@gmail.com |          | aaa             | disabled    |
      | aaa       | aaa      |               | aaa      | aaa             | disabled    |
      | aaa       |          | aaa@gmail.com | aaa      | aaa             | disabled    |
      |           | aaa      | aaa@gmail.com | aaa      | aaa             | disabled    |
      | aaa       | aaa      | aaa           | aaa      | aaa             | disabled    |
      | aaa       | aaa      | aaa           | aaaaaaaa | aaaaaaab        | disabled    |
      | aaa       | aaa      | aaa@gmail.com | aaaaaaaa | aaaaaaaa        | enabled     |

  Scenario: Register new user
    When I fill registration form with "TestFirstName","TestLastName","aaa@aaa.aa","admin123","admin123"
    And I submit registration
    Then I should see "Your account is now created." message

  Scenario Outline: Links lead to relevant pages
    When I click on "<linkName>" link
    Then I should see "<targetPageUrl>" page
    Examples:
      | linkName        | targetPageUrl     |
      | forgotPassword  | /forgot-password  |
      | login           | /login            |