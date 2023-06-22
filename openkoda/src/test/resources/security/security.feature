Feature: Privileges process

#  Background:
#    Given test user "test" "User"

  Scenario Outline: Check user organization privileges
    When Create user "<userName>" "Test" with "<area>" privilege "<privilege>" and organization "<name>" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "<name>" on the side menu
    Then I should find "<targetPageUrls>" in the side menu "<name>"
    And I should see "<forms>" forms

    Examples:
      | userName | login    | area         | name     | privilege                                            | targetPageUrls                          | privilegeName | forms                           |
      | test1    | test1    | Organization | testOrg1 | readOrgData;readUserData                             | Show All;Dashboard;Settings;            | testOrg1      | ;;                              |
      | test2    | test2    | Organization | testOrg1 | readOrgData;readUserData;manageOrgData               | Show All;Dashboard;Settings;            | testOrg2      | organizationForm;inviteUserForm |
      | test3    | test3    | Organization | testOrg1 | readOrgData;readUserData;canChangeEntityOrganization | Show All;Dashboard;Settings             | testOrg3      | ;;                              |
      | userdata | userdata | Global       | testOrg1 | readOrgData;readUserData;canReadBackend              | Show All;Dashboard;Settings;Audit;Users | testOrg4      | ;;                              |


  Scenario Outline: Check global setting privileges
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "Test admin" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Admin" on the side menu
    Then I should find "<targetPageUrls>" in the side menu "Admin"
    Examples:
      | userName  | login     | privilege                           | targetPageUrls                                                                               | privilegeName           |
      | global    | global    | readOrgData;canAccessGlobalSettings | Show All;Organizations;Files                                                                 | canAccessGlobalSettings |
      | support   | support   | readOrgData;canReadSupportData      | Show All;Organizations;Audit;Files;Logs;System Health                                        | canReadSupportData      |
      | resources | resources | readOrgData;readFrontendResource    | Show All;Organizations;Resources;Frontend resources                                          | readFrontendResource    |
      | backend   | backend   | readOrgData;canReadBackend          | Show All;Organizations;Roles;Files;Server-Side Javascript;Threads;Event Listeners;Schedulers | canReadBackend          |


  Scenario Outline: Check canSeeUserEmail privilege
    When Create user "<userName>" "Test" with "Global" privilege "<privilege>" and organization "<name>" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Users" on the side menu
    And I click "test User" link
    Then I should see test user email "<condition>"

    Examples:
      | userName            | login                                | privilege                                | privilegeName    | condition |
      | can_see_user_email  | can_see_user_email@openkodatest.com  | readOrgData;readUserData;canSeeUserEmail | canSeeUserEmail  | 1         |
      | can_see_user_email1 | can_see_user_email1@openkodatest.com | readOrgData;readUserData                 | canSeeUserEmail1 | 0         |

  Scenario Outline: Check canImpersonate setting - privileges impersonate(spoof) another user
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Users" on the side menu
    Then I should see "Spoof" pages "<condition>"

    Examples:
      | userName     | login                         | privilege                               | privilegeName   | condition |
      | impersonate  | impersonate@openkodatest.com  | readOrgData;readUserData;canImpersonate | canImpersonate1 | 1         |
      | impersonate1 | impersonate1@openkodatest.com | readOrgData;readUserData                | canImpersonate2 | 0         |

  Scenario Outline: Check canImpersonate setting - privileges impersonate(spoof) another user
    When Create user "<userName>" "Password" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<login>" with password "Password"
    And I click "Users" on the side menu
    And I click "test User" link
    Then I should see "Reset Password" button "<condition>"
    Examples:
      | userName | login                     | privilege                                 | privilegeName | condition |
      | restart  | restart@openkodatest.com  | readOrgData;readUserData;canResetPassword | restart1      | 1         |
      | restart1 | restart1@openkodatest.com | readOrgData;readUserData                  | restart2      | 0         |

  Scenario Outline: Check manageUserData setting - update user profiles data
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Users" on the side menu
    And I click "test User" link
    Then I should see "Settings" pages "<condition>"
    Examples:
      | userName          | login                              | privilege                               | privilegeName   | condition |
      | manage_user_data  | manage_user_data@openkodatest.com  | readOrgData;readUserData;manageUserData | manageUserData1 | 1         |
      | manage_user_data1 | manage_user_data1@openkodatest.com | readOrgData;readUserData;               | manageUserData2 | 0         |

  Scenario Outline: Check manageUserData setting - update user roles (in organization)
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Users" on the side menu
    Then I should see "Remove Role" button "<condition>"
    Examples:
      | userName | login                   | privilege                                               | privilegeName | condition |
      | roles    | roles@openkodatest.com  | readOrgData;canReadBackend;readUserData;manageUserRoles | readUserRole1 | 1         |
      | roles1   | roles2@openkodatest.com | readOrgData;canReadBackend;readUserData                 | readUserRole2 | 0         |

  Scenario Outline: Check canManageSupportData setting - manage logs and system health data
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Logs" on the side menu
    And I click "Settings" link
    Then I should see "Save" button "<condition>"
    Examples:
      | userName | login                     | privilege                                           | privilegeName | condition |
      | support1 | support1@openkodatest.com | readOrgData;canReadSupportData;canManageSupportData | support1      | 1         |
      | support2 | support2@openkodatest.com | readOrgData;canReadSupportData;                     | support2      | 0         |

  Scenario Outline: Check manageFrontendResource setting - save and update frontend resource data
    Given Frontend resources "<userName>"
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Resources" on the side menu
    And I click "<userName>" link
    Then I should see "Save" button "<condition>"
    Examples:
      | userName                  | login                                      | privilege                                                                       | privilegeName           | condition |
      | manage_frontend_resource  | manage_frontend_resource@openkodatest.com  | readOrgData;canAccessGlobalSettings;readFrontendResource;manageFrontendResource | manageFrontendResource1 | 1         |
      | manage_frontend_resource1 | manage_frontend_resource2@openkodatest.com | readOrgData;canAccessGlobalSettings;readFrontendResource                        | manageFrontendResource2 | 0         |

  Scenario Outline: Check canManageBackend setting - read any configurable backend data
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<login>" with password "Test"
    And I click "Roles" on the side menu
    Then I should see "Remove" button "<condition>"
    Examples:
      | userName        | login                                 | privilege                                   | privilegeName     | condition |
      | manage_backend  | manage_backend@openkodatest.com       | readOrgData;canReadBackend;canManageBackend | canManageBackend  | 1         |
      | manage_backend1 | test2manage_backend1@openkodatest.com | readOrgData;canReadBackend                  | canManageBackend1 | 0         |
