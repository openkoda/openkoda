Feature: Navigation
  Background:
    Given I am logged as user "admin" with password "admin"

  Scenario Outline: Global admin can navigate through admin pages
    When I open "/html/organization/all" page
    And I click "<adminPageLinkLabel>" in admin menu
    Then I should see "<targetPageUrl>" page
    Examples:
      | adminPageLinkLabel     | targetPageUrl               |
      | users                  | /html/user/all              |
      | organizations          | /html/organization/all      |
      | audit                  | /html/audit/all             |
      | roles                  | /html/role/all              |
      | frontendresource       | /html/frontendResource/all  |
      | frontend-elements      | /html/webEndpoint/all      |
      | files                  | /html/file/all              |
      | serverJs               | /html/serverJs/all          |
      | threads                | /html/thread                |
      | event-listeners        | /html/eventListener/all     |
      | schedulers             | /html/scheduler/all         |
      | logs                   | /html/logs/all              |
      | system-health          | /html/system-health         |

