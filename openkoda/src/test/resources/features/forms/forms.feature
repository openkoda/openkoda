@active @current
Feature: Forms

  Background:
    Given I am logged as user "admin" with password "admin"

  Scenario Outline: Global admin can submit forms on admin pages
    When I open "/html/organization/all" page
    And I open "<formPageUrl>" page
    When I fill "<formName>" with values "<setFormValues>"
    And I submit form "<newFormName>"
    Then I should see "<targetPageUrl>" page
    And I should find "<expectedTableColumnValues>" in the visible table
    Examples:
      | formPageUrl                          | targetPageUrl               | formName             | setFormValues                             |  expectedTableColumnValues | newFormName |
      | /html/frontendresource/new/settings  | /html/frontendresource/all  | frontendResourceForm | name=ddd;urlPath=www                      |  ?;ddd;www;                | organizationRelatedForm |
      | /html/scheduler/new/settings         | /html/scheduler/all         | schedulerForm        | cronExpression=3 2 1 * * *;eventData=Boo  | ?;3 2 1 * * *;Boo          | schedulerForm |
      | /html/role/new/settings              | /html/role/all              | roleForm             | name=ROLE_TEST;type=ORG                   | ?;ROLE_TEST;ORG            | roleForm      |


  Scenario Outline: Global admin can submit forms form on admin pages, and forms will not be redirected after save
    When I open "/html/organization/all" page
    And I open "<formPageUrl>" page
    When I fill "<formName>" with values "<setFormValues>"
    And I submit form "<formName>"
    Then I should see "<formAlertMessage>" "<formAlertType>" form alert
    Examples:
      | formPageUrl                     | formName                | setFormValues                                                                       | formAlertMessage    | formAlertMessage |
      | /html/organization/121/settings | inviteUserForm          | firstName=boo;lastName=foo;email=test@codedose.com;roleName=ROLE_ORG_USER           | ?;User invited      | success          |
      | /html/organization/new/settings | organizationForm        | name=boo                                                                            | Organization saved  | success          |
      | /html/user/10000/settings       | editUserForm            | firstName=Test;lastName=LastTest;email=test@t.test                                  | User data saved     | success          |