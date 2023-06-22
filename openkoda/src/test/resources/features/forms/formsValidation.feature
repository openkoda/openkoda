@active
Feature: FormsValidation

  Scenario Outline: Check if forms produce errors when wrong values are entered
    Given I am logged as user "admin" with password "admin"
    When I open "/html/organization/all" page
    And I open "<formPageUrl>" page
    When I fill "<formName>" with values "<setFormValues>"
    And I submit form "<formName>"
    Then I should see "<formAlertType>" "<formAlertMessage>" form alert
    And I should see "<alertMessage>" alert
    Examples:
      | formPageUrl                        | alertMessage                                       | formName                | setFormValues                                                                                                                                                                                                                                          | formAlertMessage          | formAlertType |

      | /html/organization/new/settings    | Required                                           | organizationForm        | ;;                                                                                                                                                                                                                                                     | This form contains errors | danger        |

      | /html/eventlistener/new/settings   | Required                                           | eventListenerForm       | ;;                                                                                                                                                                                                                                                     | This form contains errors | danger        |
      | /html/eventlistener/new/settings   | Required                                           | eventListenerForm       | ;event=com.openkoda.core.service.event.ApplicationEvent,SCHEDULER_EXECUTED,com.openkoda.dto.system.ScheduledSchedulerDto;                                                                                                                              | This form contains errors | danger        |
      | /html/eventlistener/new/settings   | Consumer is not compatible with event object       | eventListenerForm       | ;event=com.openkoda.core.service.event.ApplicationEvent,USER_MODIFIED,com.openkoda.dto.user.BasicUser;consumer=com.openkoda.core.service.email.EmailService,sendAndSaveEmail,com.openkoda.model.User,1;                                                | This form contains errors | danger        |
      | /html/eventlistener/new/settings   | Consumer is not compatible with event object       | eventListenerForm       | ;event=com.openkoda.core.service.event.ApplicationEvent,SCHEDULER_EXECUTED,com.openkoda.dto.system.ScheduledSchedulerDto;consumer=com.openkoda.core.service.email.EmailService,sendAndSaveEmail,com.openkoda.model.User,1;                             | This form contains errors | danger        |

      | /html/scheduler/new/settings       | RequiredNot valid                                  | schedulerForm           | ;;                                                                                                                                                                                                                                                     | This form contains errors | danger        |
      | /html/scheduler/new/settings       | Not valid                                          | schedulerForm           | ;cronExpression=cron;                                                                                                                                                                                                                                  | This form contains errors | danger        |
      | /html/scheduler/new/settings       | Not valid                                          | schedulerForm           | ;cronExpression=cron;eventData=test                                                                                                                                                                                                                    | This form contains errors | danger        |

      # No form after send
#      | /html/role/new/settings          | Required                                           | roleForm                | ;;                                                                                                                                                                                                                                                     | This form contains errors | danger        |
#      | /html/role/new/settings          | Required                                           | roleForm                | name=test;;                                                                                                                                                                                                                                            | This form contains errors | danger        |
#      | /html/role/new/settings          | Required                                           | roleForm                | ;type=ORG;                                                                                                                                                                                                                                             | This form contains errors | danger        |       | This form contains errors | danger        |

      | /html/user/10000/settings         | Required                                           | editUserForm            | firstName=;lastName=;email=                                                                                                                                                                                                                             | This form contains errors | danger        |
      | /html/user/10000/settings         | Required                                           | editUserForm            | firstName=Test;lastName=;email=                                                                                                                                                                                                                         | This form contains errors | danger        |
      | /html/user/10000/settings         | Required                                           | editUserForm            | firstName=;lastName=LastTest;email=                                                                                                                                                                                                                     | This form contains errors | danger        |
      | /html/user/10000/settings         | Not valid                                          | editUserForm            | firstName=;lastName=;email=email                                                                                                                                                                                                                        | This form contains errors | danger        |
      | /html/user/10000/settings         | Required                                           | editUserForm            | firstName=;lastName=;email=;enabled=true                                                                                                                                                                                                                | This form contains errors | danger        |
      | /html/user/10000/settings         | Not valid                                          | editUserForm            | firstName=Test;lastName=LastTest;email=test;enabled=true                                                                                                                                                                                                | This form contains errors | danger        |

        # No form after send
#      | /html/organization/121/settings | RequiredNot valid                                  | inviteUserForm          | firstName=;lastName=;email=;                                                                                                                                                                                                                           | This form contains errors | danger        |
#      | /html/organization/121/settings | Not valid                                          | inviteUserForm          | firstName=Test;lastName=Test;email=test;roleName=;                                                                                                                                                                                                     | This form contains errors | danger        |
#      | /html/organization/121/settings | Required                                           | inviteUserForm          | firstName=Test;lastName=Test;email=test@test.pl;roleName=;                                                                                                                                                                                             | This form contains errors | danger        |

  Scenario Outline: Check if forms produce errors when wrong values are entered
    Given I am logged as user "admin" with password "admin"
    When I open "/html/organization/all" page
    And I open "<formPageUrl>" page
    When I fill "<formName>" with values "<setFormValues>"
    And I submit form "organizationRelatedForm"
    Then I should see "<formAlertType>" "<formAlertMessage>" form alert
    Examples:
      | formPageUrl                            | formName                | setFormValues                      | formAlertMessage          | formAlertType |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;;                                 | This form contains errors | danger        |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;name=CMSNAME;                     | This form contains errors | danger        |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;type=JS;                          | This form contains errors | danger        |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;name=NAME;type=JS;                | This form contains errors | danger        |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;name=NAME;type=CSS;               | This form contains errors | danger        |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;name=NAME;urlPath=css.css;        | This form contains errors | danger        |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;name=NAME;urlPath=js.js;          | This form contains errors | danger        |
      | /html/frontendresource/new/settings    | frontendResourceForm    | ;name=NAME;urlPath=js.js;type=CSS; | This form contains errors | danger        |
