let timelogRepository = services.data.getRepository('timelog');
let ticketRepository = services.data.getRepository('ticket');
let assignmentRepository = services.data.getRepository('assignment');
let creativeWorkStatementRepository = services.data.getRepository('creativeWorkStatement');

let timelogForm = services.frontendMappingDefinition.createFrontendMappingDefinition(
                "timelog", "readOrgData", "readOrgData",
                a => a.datalist("assignments", f => f.dictionary("assignment"))
                        .radioList("assignmentId", "assignments").validate(v => v != null ? null : "not.valid")
                        .date("startedOn")
                        .text("duration").valueConverters( v => timelogRepository.convertToSeconds(v), v => timelogRepository.convertToHoursString(v) ).validate(v => (v != null && v > 0) ? null : "not.valid")
                        .text("description"));


let ticketForm = services.frontendMappingDefinition.createFrontendMappingDefinition(
                "ticket", "canAccessGlobalSettings", "canAccessGlobalSettings",
                a => a.datalist("organizations", f => f.dictionary("organization"))
                        .dropdown("organizationId", "organizations").additionalPrivileges("readOrgData", "canAccessGlobalSettings")
                        .text("name")
                        .datalist("projects", f => f.dictionary("project"))
                        .dropdown("projectId", "projects").additionalPrivileges("readOrgData", "canAccessGlobalSettings")
                        );

let assignmentForm = services.frontendMappingDefinition.createFrontendMappingDefinition(
                "assignment", "canAccessGlobalSettings", "canAccessGlobalSettings",
                a => a.datalist("organizations", f => f.dictionary("organization"))
                        .dropdown("organizationId", "organizations").additionalPrivileges("readOrgData", "canAccessGlobalSettings")
                        .datalist("tickets", f => f.dictionary("ticket"))
                        .datalist("users", f => f.dictionary("user"))
                        .dropdown("ticketId", "tickets")
                        .dropdown("userId", "users")
                        .hidden("description")
                        .checkbox("billable")
                        .checkbox("researchAndDevelopment")
                        .checkbox("creativeWork"));

let creativeWorkStatementForm = services.frontendMappingDefinition.createFrontendMappingDefinition(
  				"creativeWorkStatement", "readOrgData", "readOrgData",
  				a => a.datalist("fullNames", f => f.dictionary("user", "name", "name"))
  						.dropdown("fullName", "fullNames")
  						.datalist("users", f => f.dictionary("user"))
  						.dropdown("userId", "users")
  						.number("month")
  						.text("year")
  						.text("totalTimeSpent")
  						.text("assignments"));

services.customisation.registerFrontendMapping(timelogForm, timelogRepository);
services.customisation.registerHtmlCrudController(ticketForm, ticketRepository).setGenericTableFields("name");
services.customisation.registerHtmlCrudController(assignmentForm, assignmentRepository).setGenericTableFields("description","creativeWork","billable","researchAndDevelopment");
services.customisation.registerApiCrudController(timelogForm, timelogRepository).setDefaultControllerPrivilege(com.openkoda.model.Privilege.readOrgData);
services.customisation.registerFrontendMapping(creativeWorkStatementForm, creativeWorkStatementRepository);
services.customisation.registerHtmlCrudController(creativeWorkStatementForm, creativeWorkStatementRepository).setGenericTableFields("fullName","month","year","totalTimeSpent","assignments","userId");
services.customisation.registerApiCrudController(creativeWorkStatementForm, creativeWorkStatementRepository).setDefaultControllerPrivilege(com.openkoda.model.Privilege.readOrgData);



model