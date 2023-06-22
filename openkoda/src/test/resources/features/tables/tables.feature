Feature: Tables
  Background:
    Given I am logged as user "admin" with password "admin"

  Scenario Outline: Global admin can see default values in admin tables
    When I open "<tablePageUrl>" page
    Then I should find "<expectedTableColumnValues>" in the visible table
    Examples:
      | tablePageUrl                | expectedTableColumnValues       |
      | /html/organization/all      | 121;Test Org                    |
      | /html/role/all              | ?;ROLE_USER;GLOBAL              |
      | /html/role/all              | ?;ROLE_ORG_ADMIN;ORG            |
      | /html/role/all              | ?;ROLE_ADMIN;GLOBAL             |
      | /html/role/all              | ?;ROLE_ORG_USER;ORG             |
      | /html/role/all              | ?;ROLE_UNAUTHENTICATED;GLOBAL   |
      | /html/user/all              | ?;Test Org;?               |
      | /html/frontendresource/all  | ?;login;login |

  Scenario Template: Global admin can sort values lexicographically in admin tables
    When I open "<page>" page
    And I click "<sortColumn>" column header
    Then I should see "<sortPage>" page
    And I should find data sorted "<sortType>" by "<sortColumn>" column
    Examples:
      | page                        | sortColumn        | sortType | sortPage                                                                                                        |
      | /html/user/all              | name              | lexic    | /html/user/all?user_page=0&user_size=20&user_sort=name,ASC&user_search=                                         |
      | /html/organization/all      | name              | lexic    | /html/organization/all?organization_page=0&organization_size=20&organization_sort=name,ASC&organization_search= |
      | /html/audit/all             | createdOn         | lexic    | /html/audit/all?audit_page=0&audit_size=20&audit_sort=createdOn,ASC&audit_search=                               |
      | /html/audit/all             | ipAddress         | lexic    | /html/audit/all?audit_page=0&audit_size=20&audit_sort=ipAddress,ASC&audit_search=                               |
      | /html/role/all              | name              | lexic    | /html/role/all?role_page=0&role_size=20&role_sort=name,ASC&role_search=                                         |
      | /html/frontendresource/all  | name              | lexic    | /html/frontendresource/all?obj_page=0&obj_size=20&obj_sort=name,ASC&obj_search=                             |
      | /html/eventlistener/all     | eventClassName    | lexic    | /html/eventlistener/all?event_page=0&event_size=20&event_sort=eventClassName,ASC&event_search=                  |
      | /html/eventlistener/all     | eventName         | lexic    | /html/eventlistener/all?event_page=0&event_size=20&event_sort=eventName,ASC&event_search=                       |
      | /html/eventlistener/all     | consumerClassName | lexic    | /html/eventlistener/all?event_page=0&event_size=20&event_sort=consumerClassName,ASC&event_search=               |
      | /html/scheduler/all         | cronExpression    | lexic    | /html/scheduler/all?scheduler_page=0&scheduler_size=20&scheduler_sort=cronExpression,ASC&scheduler_search=      |
      | /html/scheduler/all         | eventData         | lexic    | /html/scheduler/all?scheduler_page=0&scheduler_size=20&scheduler_sort=eventData,ASC&scheduler_search=           |
      | /html/organization/all      | id                | linear   | /html/organization/all?organization_page=0&organization_size=20&organization_sort=id,ASC&organization_search=   |
      | /html/audit/all             | id                | linear   | /html/audit/all?audit_page=0&audit_size=20&audit_sort=id,ASC&audit_search=                                      |
      | /html/role/all              | id                | linear   | /html/role/all?role_page=0&role_size=20&role_sort=id,ASC&role_search=                                           |
      | /html/frontendresource/all  | id                | linear   | /html/frontendresource/all?obj_page=0&obj_size=20&obj_sort=id,ASC&obj_search=                                    |
      | /html/eventlistener/all     | id                | linear   | /html/eventlistener/all?event_page=0&event_size=20&event_sort=id,ASC&event_search=                              |
      | /html/scheduler/all         | id                | linear   | /html/scheduler/all?scheduler_page=0&scheduler_size=20&scheduler_sort=id,ASC&scheduler_search=                  |

  Scenario Template: Global admin can use pagination in admin tables
    Given I am expecting no less than "<numberOfRecords>" number of records in "<column>" column
    When I open "<page>" page with url parameter "<pageSize>"
    And I should see pagination links list with class "<paginationId>"
    Then I click "<linkName>" arrow
    And I should see "<urlParameters>" parameter in page URL
    And I should see "<previousLinkArrow>" arrow
    Examples:
      | page                        | urlParameters                                     | linkName | previousLinkArrow | paginationId         | column            | numberOfRecords | pageSize                 |
      | /html/audit/all             | audit_page=1&audit_size=1                         | Next     | Previous          | pagination-wrapper   | audit             | 2               | audit_size=1             |
      | /html/user/all              | user_page=1&user_size=1                           | Next     | Previous          | pagination-wrapper   | user              | 2               | user_size=1              |
      | /html/organization/all      | organization_page=1&organization_size=1           | Next     | Previous          | pagination-wrapper   | organization      | 2               | organization_size=1      |
      | /html/role/all              | role_page=1&role_size=1                           | Next     | Previous          | pagination-wrapper   | role              | 2               | role_size=1              |
      | /html/frontendresource/all  | obj_page=1&obj_size=1                             | Next     | Previous          | pagination-wrapper   | frontendResource  | 2               | obj_size=1 |
      | /html/eventlistener/all     | event_page=1&event_size=1                         | Next     | Previous          | pagination-wrapper   | event             | 2               | event_size=1             |
      | /html/scheduler/all         | scheduler_page=1&scheduler_size=1                 | Next     | Previous          | pagination-wrapper   | scheduler         | 2               | scheduler_size=1         |

  Scenario Template: Global admin don't see pagination in admin tables
    Given I am expecting no less than "<numberOfRecords>" number of records in "<column>" column
    When I open "<page>" page with page size URL parameter equal to number of records in "<column>" column
    And I should not see pagination links in list with id "<paginationId>"

    Examples:
      | page                        | column           | numberOfRecords | paginationId |
      | /html/audit/all             | audit            | 1               | pagination   |
      | /html/user/all              | user             | 1               | pagination   |
      | /html/organization/all      | organization     | 1               | pagination   |
      | /html/role/all              | role             | 1               | pagination   |
      | /html/frontendresource/all  | frontendResource | 1               | pagination   |
      | /html/eventlistener/all     | event            | 1               | pagination   |
      | /html/scheduler/all         | scheduler        | 1               | pagination   |
