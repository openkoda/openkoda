Feature: Privileges process

  Background:
    Given test user "test" "User"

  Scenario Outline: Check user organization privileges
    When Create user "<userName>" "Test" with "<area>" privilege "<privilege>" and organization "<name>" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "<name>" on the side menu
    Then I should find "<targetPageUrls>" in the side menu "<name>"
    And I should see "<forms>" forms

    Examples:
      | userName | area         | name     | privilege                               | targetPageUrls                                       | privilegeName | forms                           |
      | test1    | Organization | testOrg1 | readOrgData                             | Show All;Dashboard;Settings;Integrations;Audit       | testOrg1      | ;;                              |
      | test2    | Organization | testOrg1 | readOrgData;manageOrgData               | Show All;Dashboard;Settings;Integrations;Audit       | testOrg2      | organizationForm;inviteUserForm |
      | test3    | Organization | testOrg1 | readOrgData;canChangeEntityOrganization | Show All;Dashboard;Settings;Integrations;Audit       | testOrg3      | ;;                              |
      | userdata | Global       | testOrg1 | readOrgData;readUserData                | Show All;Dashboard;Settings;Integrations;Audit;Users | testOrg4      | ;;                              |


  Scenario Outline: Check global setting privileges
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "Test admin" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Admin" on the side menu
    Then I should find "<targetPageUrls>" in the side menu "Admin"
    Examples:
      | userName  | privilege                           | targetPageUrls                                                                               | privilegeName           |
      | global    | readOrgData;canAccessGlobalSettings | Show All;Organizations;Files                                                                 | canAccessGlobalSettings |
      | support   | readOrgData;canReadSupportData      | Show All;Organizations;Audit;Files;Logs;System Health                                        | canReadSupportData      |
      | resources | readOrgData;readFrontendResource    | Show All;Organizations;Resources;Frontend resources                                          | readFrontendResource    |
      | backend   | readOrgData;canReadBackend          | Show All;Organizations;Roles;Files;Server-Side Javascript;Threads;Event Listeners;Schedulers | canReadBackend          |


  Scenario Outline: Check canSeeUserEmail privilege
    When Create user "<userName>" "Test" with "Global" privilege "<privilege>" and organization "<name>" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Users" on the side menu
    And I click "test User" link
    Then I should see test user email "<condition>"

    Examples:
      | userName            | privilege                                | privilegeName    | condition |
      | can_see_user_email  | readOrgData;readUserData;canSeeUserEmail | canSeeUserEmail  | 1         |
      | can_see_user_email1 | readOrgData;readUserData                 | canSeeUserEmail1 | 0         |


  Scenario Outline: Check canImpersonate setting - privileges impersonate(spoof) another user
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Users" on the side menu
    Then I should see "Spoof" pages "<condition>"

    Examples:
      | userName     | privilege                               | privilegeName   | condition |
      | impersonate  | readOrgData;readUserData;canImpersonate | canImpersonate1 | 1         |
      | impersonate1 | readOrgData;readUserData                | canImpersonate2 | 0         |

  Scenario Outline: Check canImpersonate setting - privileges impersonate(spoof) another user
    When Create user "<userName>" "Password" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Password"
    And I click "Users" on the side menu
    And I click "test User" link
    Then I should see "Reset Password" button "<condition>"
    Examples:
      | userName | privilege                                 | privilegeName | condition |
      | restart  | readOrgData;readUserData;canResetPassword | restart1      | 1         |
      | restart1 | readOrgData;readUserData                  | restart2      | 0         |

  Scenario Outline: Check manageUserData setting - update user profiles data
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Users" on the side menu
    And I click "test User" link
    Then I should see "Settings" pages "<condition>"
    Examples:
      | userName          | privilege                               | privilegeName   | condition |
      | manage_user_data  | readOrgData;readUserData;manageUserData | manageUserData1 | 1         |
      | manage_user_data1 | readOrgData;readUserData;               | manageUserData2 | 0         |

  Scenario Outline: Check manageUserData setting - update user roles (in organization)
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Users" on the side menu
    Then I should see "Remove Role" button "<condition>"
    Examples:
      | userName | privilege                                               | privilegeName | condition |
      | roles    | readOrgData;canReadBackend;readUserData;manageUserRoles | readUserRole1 | 1         |
      | roles1   | readOrgData;canReadBackend;readUserData                 | readUserRole2 | 0         |

  Scenario Outline: Check canManageSupportData setting - manage logs and system health data
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Logs" on the side menu
    And I click "Settings" link
    Then I should see "Save" button "<condition>"
    Examples:
      | userName | privilege                                           | privilegeName | condition |
      | support1 | readOrgData;canReadSupportData;canManageSupportData | support1      | 1         |
      | support2 | readOrgData;canReadSupportData;                     | support2      | 0         |

  Scenario Outline: Check manageFrontendResource setting - save and update frontend resource data
    Given Frontend resources "<userName>"
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Resources" on the side menu
    And I click "<userName>" link
    Then I should see "Save" button "<condition>"
    Examples:
      | userName                  | privilege                                                                       | privilegeName           | condition |
      | manage_frontend_resource  | readOrgData;canAccessGlobalSettings;readFrontendResource;manageFrontendResource | manageFrontendResource1 | 1         |
      | manage_frontend_resource1 | readOrgData;canAccessGlobalSettings;readFrontendResource                        | manageFrontendResource2 | 0         |

  Scenario Outline: Check canManageBackend setting - read any configurable backend data
    When Create user "<userName>" "Test" with "GLOBAL" privilege "<privilege>" and organization "testOrg" "<privilegeName>"
    And I am logged as user "<userName>" with lastName "Test"
    And I click "Roles" on the side menu
    Then I should see "Remove" button "<condition>"
    Examples:
      | userName        | privilege                                   | privilegeName     | condition |
      | manage_backend  | readOrgData;canReadBackend;canManageBackend | canManageBackend  | 1         |
      | manage_backend1 | readOrgData;canReadBackend                  | canManageBackend1 | 0         |
