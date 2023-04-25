Feature: Navigation
  Background:
    Given I am logged as user "admin" with password "admin"

  Scenario Outline: Global admin can navigate through admin pages
    When I click "Admin" on the left menu
    And I click "<adminPageLinkLabel>" in top horizontal admin menu
    Then I should see "<targetPageUrl>" page
    Examples:
      | adminPageLinkLabel     | targetPageUrl               |
      | Users                  | /html/user/all              |
      | Organizations          | /html/organization/all      |
      | Audit                  | /html/audit/all             |
      | Roles                  | /html/role/all              |
      | Resources              | /html/frontendresource/all  |
      | Frontend Elements      | /html/uicomponent/all       |
      | Files                  | /html/file/all              |
      | Server-Side Javascript | /html/serverJs/all          |
      | Threads                | /html/thread                |
      | Event Listeners        | /html/eventlistener/all     |
      | Schedulers             | /html/scheduler/all         |
      | Logs                   | /html/logs/all              |
      | System Health          | /html/system-health         |

