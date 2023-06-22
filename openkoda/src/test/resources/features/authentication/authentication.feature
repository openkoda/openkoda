Feature: Authentication process

  Background:
    Given I am on "login" page

  Scenario: Check if user see login error when given wrong username
    When I login as user "jasiu" with password "admin"
    Then I should see "Invalid username or password." error message

  Scenario: Check if user see login error when given wrong password
    When I login as user "admin" with password "12345"
    Then I should see "Invalid username or password." error message

  Scenario: Check if user can login
    When I login as user "admin" with password "admin"
    Then I should see "/html/dashboard" page
