INSERT INTO public.frontend_resource VALUES(20002, 'admin@yourdomain.org', 10000, '2023-05-26 12:16:11.960', 'tickets tickets ui_component 00000/120171', 'admin@yourdomain.org', 10000, NULL, '2023-05-29 13:15:49.486', '<!--DEFAULT CONTENT-->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" lang="en" layout:decorate="~{${defaultLayout}}">
<body>
<div class="container">
    <h1 layout:fragment="title"/>
    <div layout:fragment="content">
	  <form action="./report/csv" method="GET">
		<div class="form-group">
		  <select name="month" class="selectpicker type1 form-control d-inline">
			<option th:each="m : ${months}" th:value="${m[0]}" th:text="${m[1]}" />
		  </select>
		</div>
		<div><button type="submit" class="btn btn-primary btn-submit d-inline">GET REPORT</button></div>
	  </form>
    </div>
</div>
</body>
</html>
', NULL, false, false, 'report', NULL, 'UI_COMPONENT', 'report');

INSERT INTO public.frontend_resource VALUES (20001, 'admin@yourdomain.org', 10000, '2023-04-25 10:16:24.820479+02', 'dashboard dashboard html 00000/20001', 'admin@yourdomain.org', 10000, NULL, '2023-05-06 15:25:05.71898+02', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" xmlns="http://www.w3.org/1999/html" lang="en"
      layout:decorate="~{${defaultLayout}}">
<body>
<div class="container-fluid">

    <div layout:fragment="content" th:attr="data-menu-item=''dashboard''">

          <a th:href="@{./webendpoints/timelog}">Log Your Time</a>
          <th:block th:if="${@auth.hasGlobalPrivilege(''canAccessGlobalSettings'')}">
        <div th:utext="#{template.dashboard.default.message(${@url.allFrontendResource()})}"></div>
        <div class="row" id="registered-cruds">
            <th:block th:each="entry : ${@htmlCRUDControllerConfigurationMap.entrySet()}">
                <div class="col-lg-3">
                    <div class="card shadow mb-4">
                        <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                            <h6 class="m-0 font-weight-bold" th:text="${@messages.getFieldLabel(entry.key, entry.key)}"></h6>
                        </div>
                        <div class="card-body">
                            <ul>
                                <li><a th:href="${@url.all(organizationEntityId, entry.key)}">All</a></li>
                                <li><a th:href="${@url.form(organizationEntityId, entry.key)}">New</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </th:block>
        </div>
    </th:block>

    </div>
</div>
</body>
</html>', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" xmlns="http://www.w3.org/1999/html" lang="en"
      layout:decorate="~{${defaultLayout}}">
<body>
<div class="container-fluid">

    <div layout:fragment="content" th:attr="data-menu-item=''dashboard''">

          <div class="card shadow mb-4">
                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                  <h6 class="m-0 font-weight-bold">Timelog</h6>
                </div>
                <div class="card-body">
                    <a th:href="@{./webendpoints/timelog}">Log Your Time</a>
                    </br>
                    <th:block th:if="${@auth.hasGlobalPrivilege(''canAccessGlobalSettings'')}">
                        <a th:href="@{./webendpoints/report}">Report</a>
                    </th:block>
                </div>
          </div>
          <th:block th:if="${@auth.hasGlobalPrivilege(''canAccessGlobalSettings'')}">
        <div th:utext="#{template.dashboard.default.message(${@url.allFrontendResource()})}"></div>
        <div class="row" id="registered-cruds">
            <th:block th:each="entry : ${@htmlCRUDControllerConfigurationMap.entrySet()}">
                <div class="col-lg-3">
                    <div class="card shadow mb-4">
                        <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                            <h6 class="m-0 font-weight-bold" th:text="${@messages.getFieldLabel(entry.key, entry.key)}"></h6>
                        </div>
                        <div class="card-body">
                            <ul>
                                <li><a th:href="${@url.all(organizationEntityId, entry.key)}">All</a></li>
                                <li><a th:href="${@url.form(organizationEntityId, entry.key)}">New</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </th:block>
        </div>
    </th:block>

    </div>
</div>
</body>
</html>', false, false, 'dashboard', '', 'HTML', 'dashboard');
INSERT INTO public.frontend_resource VALUES (20000, 'admin@yourdomain.org', 10000, '2023-04-25 10:54:43.749621+02', 'timelog timelog ui_component 00000/20000', 'admin@yourdomain.org', 10000, NULL, '2023-05-09 12:34:45.258121+02',
'<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" lang="en" layout:decorate="~{${defaultLayout}}">

<head>
    <script>
		app.changeDateInForm = function (date) {
			const dateInput = document.getElementById(''-dto[startedOn]'');
			dateInput.value = date;
			dateInput.scrollIntoView({
				behavior: ''auto'',
				block: ''center'',
				inline: ''center''
			});
			document.getElementById(''dto[duration]'').focus();
		}
    </script>
</head>

<body>
<div class="container-fluid">
    <div layout:fragment="content">
        <style>
            .day-card, .sum-card, .weekday-card {
                flex-basis: 20%;
                flex-grow: 0;
                flex-shrink: 1;
            }

            .day-card {
                min-height: 70px;
            }

            .weekdays .day-card {
                min-height: unset;
            }

            .day-card .description {
                flex: 0 1 70%;
            }

            .sum-card {
                background-color: #efe;
            }

            .weekday-card {
                background-color: #e2f8fb;
            }
		  	.weekend-card, .sum-card {
			  	flex: 0 0 5%;
		  	}

            .day-card.overtime {
                background-color: #fff3ef;
            }

            .day-logs {
                border-bottom: 1px solid #d1d3e2;
                justify-content: space-between;
            }

            .day-logs:last-child {
                border: 0;
            }

            .day-log-buttons {
                justify-content: flex-end;
            }

            @media (max-width: 1024px) {
                .day-card, .sum-card {
                    flex-basis: 50%;
                    flex-grow: 2;
                    flex-shrink: 0;
                }

            }

            @media (max-width: 576px) {
                .day-card, .sum-card {
                    flex-basis: 100%;
                    flex-grow: 2;
                    flex-shrink: 0;
                }

                .day-card .description {
                    flex: 0 1 30%;
                }
            }
        </style>
        <form th:replace="~{form::form(${timelog}, '''')}"></form>
        <div class="centered container-fluid p-0 overflow-auto">
            <div class="mb-2">
                <a th:href="@{./timelog(month=${month + 1})}">&lt;&lt;Prev month</a> | <a
                    th:href="@{./timelog(month=${month - 1})}">Next month&gt;&gt;</a>
            </div>
            <div class="card mb-2">
                <div class="card-header d-flex flex-row align-items-center justify-content-between">
                    <h6 class="m-0 font-weight-bold">Monthly Summary</h6>
                </div>
                <div class="card-body pt-0">
                    <div><span class="text-capitalize">total: </span><span
                            th:text="${#numbers.formatDecimal(timelogsSummary.sum/3600.0,1,2) + '' h''}"></span></div>
                    <div>
                        <div><span class="text-capitalize">creative work total: </span><span
                                th:text="${#numbers.formatDecimal(timelogsSummary.creativeWorkSum/3600.0,1,2) + '' h''}"></span>
                        </div>
                        <div>
                            <th:block th:if="${timelogsSummary.cwAssigmentSums.size() > 0}">
                                <ul>
                                    <li th:each="cwAssignmentSum : ${timelogsSummary.cwAssigmentSums}">
                                        <th:block
                                                th:text="${#numbers.formatDecimal(cwAssignmentSum.value/3600.0,1,2)}"/>
                                        h -
                                        <th:block th:text="${cwAssignmentSum.key.ticket.name}"/>
                                    </li>
                                </ul>
                            </th:block>
                        </div>
                    </div>
                </div>
            </div>
            <div class="d-flex justify-content-center text-sm weekdays">
                <div th:each="weekday, it: ${T(java.time.DayOfWeek).values()}"
                     th:class="''card weekday-card p-1 text-uppercase '' + ${weekday.getValue() == 6 || weekday.getValue() == 7 ? ''weekend-card'' : ''''}">
                    <span th:text="${weekday.getDisplayName(T(java.time.format.TextStyle).SHORT, T(java.util.Locale).ENGLISH)}"></span>
                </div>
                <div class="day-card sum-card card p-1 text-capitalize">Weekly total:</div>
            </div>

            <th:block th:each="week, itw: ${timelogsSummary.weeks}">
                <div class="d-flex flex-row ">
                    <th:block
                            th:if="${#lists.size(week.days) < 7 && week.days.get(0).date.getDayOfWeek().getValue() > 1}">
                        <th:block th:each="i: ${#numbers.sequence(1, 7 - #lists.size(week.days))}">
                            <div class="day-card p-1"></div>
                        </th:block>
                    </th:block>
                    <th:block th:each="day, itd: ${week.days}">

                        <div th:with="daySum=${day.sum/3600.0}"
                             th:class="''day-card card p-1 '' + ${daySum &gt; 8.0 ? ''overtime'' : ''''} + ${day.isWeekend ? ''weekend-card bg-gray-200'' : ''''}">

                            <div class="d-flex flex-row align-items-start justify-content-between">
                                <div class="font-weight-bold">
								  <th:block th:text="${#temporals.format(day.date,''dd/MM/yy'')}"/>
								  <th:block th:if="${!day.isWeekend}" th:text="${'' - '' + #numbers.formatDecimal(daySum,1,2) + '' h''}"/>
							  	</div>
                                <th:block th:if="${!day.isWeekend}">
                                    <a class="border-0 font-weight-normal" role="button"
                                       th:attr="onclick=|app.changeDateInForm(''${#temporals.format(day.date,''yyyy-MM-dd'')}'')|"><i
                                            class="fa fa-circle-plus"></i></a>
                                </th:block>
                            </div>
                            <th:block th:each="t: ${day.logs}">
                                <div class="d-flex flex-row align-items-start day-logs mt-1 text-sm">
                                    <div th:text="${t.durationInHoursString + '' ''}" class="font-weight-bold"
                                         style="flex: 0 1 15%"/>
                                    <div class="description">
                                        <div th:text="${t.assignment != null ? t.assignment.ticket.name : ''''}"
                                             class="font-weight-bold"/>
                                        <div th:text="${t.description}" class="text-break"/>
                                    </div>
                                    <div class="d-flex day-log-buttons">
                                        <a class="text-primary"
                                           th:href="@{./timelog(id=${t.id})}"><i class="fas fa-edit"></i></a>
                                        <a th:replace="~{forms::single-nostyle-button-post-form-with-confirm-class(@{./timelog/remove(id=${t.id})},
														 ''<i class=&quot;fa-solid fa-trash-can&quot;></i>'', ''Are you sure?'',''d-inline-flex'', ''bg-transparent border-0 pr-0 font-weight-normal ml-2 text-danger'')}"/>
                                    </div>
                                </div>
                            </th:block>
                        </div>
                    </th:block>
                    <th:block
                            th:if="${#lists.size(week.days) < 7 && week.days.get(0).date.getDayOfWeek().getValue() == 1}">
                        <th:block th:each="i,it: ${#numbers.sequence(1, 7 - #lists.size(week.days))}">
                            <div th:class="''day-card p-1 '' + ${it.count >= it.size - 1 ? ''weekend-card'' : ''''}"></div>
                        </th:block>
                    </th:block>
                    <div class="p-1 day-card sum-card card">
                        <span class="font-weight-bold" th:text="${#numbers.formatDecimal(week.sum/3600.0,1,2) + '' h''}"/>
                    </div>
                </div>
            </th:block>

        </div>

    </div>
</div>
</body>
</html>
', NULL, false, false, 'timelog', NULL, 'UI_COMPONENT', 'timelog');


INSERT INTO public.controller_endpoint VALUES (30002, 'admin@yourdomain.org', 10000, '2023-04-25 10:55:06.9886+02', '', 'admin@yourdomain.org', 10000, NULL, '2023-04-25 10:55:06.9886+02', 'flow
.then(a => a.services.data.getRepository("timelog").findOne( a.params.get(''id'')+"" ))
.thenSet("timelogEntity", a => a.services.data.saveForm(a.form, a.result))
.thenSet("redirectUrl", a => "/html/webendpoints/timelog" + (a.form.dto.get("startedOn") != null ? "?startedOn=" + a.form.dto.get("startedOn") : ""));', 20000, NULL, 1, NULL, 'HTML', '');
INSERT INTO public.controller_endpoint VALUES (30003, 'admin@yourdomain.org', 10000, '2023-04-25 10:55:28.82499+02', '', 'admin@yourdomain.org', 10000, NULL, '2023-04-25 11:54:11.491098+02', 'flow
.thenSet("timelogEntity", a => a.services.data.getRepository("timelog").deleteOne( a.params.get(''id'') ));
', 20000, NULL, 1, 'timelogEntity', 'MODEL_AS_JSON', 'remove');
INSERT INTO public.controller_endpoint VALUES (30001, 'admin@yourdomain.org', 10000, '2023-04-25 10:54:55.062701+02', '', 'admin@yourdomain.org', 10000, NULL, '2023-05-07 18:31:28.627062+02', 'flow.then(a => a.services.data.getRepository("timelog").findOne(a.params.get("id")+"") )
  .thenSet("timelog", a => {
                            let f = a.services.data.getForm("timelog", a.result);
                            if(a.params.get("id") == null){
                              f.dto.put("startedOn", a.params.get("startedOn"));
                            }
                            return f;
                            })
 .thenSet("month", a => a.params.get("month") == null ? 0 : a.services.util.parseInt(a.params.get("month")))
 .thenSet("timelogs", a => a.services.data.getRepository(''timelog'').findBy( (root, query, cb) => {
    let monthParam = a.params.get("month");
    let month = monthParam == null ? 0 : a.services.util.parseInt(monthParam);
    let now = a.services.util.dateNow();
    let from = now.minusMonths(month + 1);
    let to = now.minusMonths(month);
    return cb.between( root.get("startedOnMonth"), from, to);
  }
  ))
  .thenSet("timelogsSummary", a => a.services.data.getRepository(''timelog'').summarize(a.model.get("timelogs"), a.model.get("month")));', 20000, NULL, 0, NULL, 'HTML', '');

INSERT INTO public.controller_endpoint VALUES (30004,'admin@yourdomain.org',10000,'2023-05-26 12:20:50.860973+02','',
'admin@yourdomain.org',10000,NULL,'2023-05-29 13:08:11.787288+02','flow
.thenSet("now", a => a.services.util.dateNow())
.thenSet("months", a => Array.from(Array(12).keys()).map((m) => {
	let date = a.model.get("now").minusMonths(m);
	return [m, date.getMonth().toString() + '' '' + date.getYear()];
    }));',20002,NULL,0,NULL,'HTML','');

INSERT INTO public.controller_endpoint VALUES (30005,'admin@yourdomain.org',10000,'2023-05-26 12:20:50.860973+02','',
'admin@yourdomain.org',10000,NULL,'2023-05-29 13:08:11.787288+02','flow
.thenSet("csvHeaders", a => ["name","date","duration (h)","description","ticket","isBillable","isCreativeWork"])
.thenSet("timelogs", a => a.services.data.getRepository(''timelog'').findBy( (root, query, cb) => {
    let monthParam = a.params.get("month");
    let month = monthParam == null ? 0 : a.services.util.parseInt(monthParam);
    let now = a.services.util.dateNow();
    let onMonth = now.minusMonths(month);
    return cb.equal( root.get("startedOnMonth"), onMonth);
  }
))
.thenSet("timelogsArray", a => a.model.get("timelogs")
.filter(t => !!t.getAssignment())
.map(t =>[
	t.getAssignment().getUser().getName(),
    t.getStartedOn().toString(),
    t.getDuration()/3600.0,
    t.getDescription(),
    t.getAssignment().getTicket().getName(),
    t.getAssignment().isBillable(),
    t.getAssignment().isCreativeWork()
    ]))
.thenSet("timelogCsv", a => a.services.util.toCSV("timelog-report",a.model.get("timelogsArray"),a.model.get("csvHeaders")))
;',20002,'Cache-Control: no-cache',0,'timelogCsv','FILE','csv');



INSERT INTO public.server_js (id, created_by, created_by_id, created_on, index_string, modified_by, modified_by_id, organization_id, updated_on, arguments, code, model, name) VALUES (251, 'admin@yourdomain.org', 10000, '2023-05-11 12:24:20.401142+02', '251', 'admin@yourdomain.org', 10000, NULL, '2023-05-16 11:27:56.361811+02', '', 'let timelogRepository = services.data.getRepository(''timelog'');
let ticketRepository = services.data.getRepository(''ticket'');
let assignmentRepository = services.data.getRepository(''assignment'');

let timelogForm = services.frontendMappingDefinition.createFrontendMappingDefinition(
                "timelog", "readOrgData", "readOrgData",
                a => a.datalist("assignments", f => f.getDictionaryRepository().dictionary("assignment"))
                        .radioList("assignmentId", "assignments").validate(v => v != null ? null : "not.valid")
                        .date("startedOn")
                        .text("duration").valueConverters( v => timelogRepository.convertToSeconds(v), v => timelogRepository.convertToHoursString(v) ).validate(v => (v != null && v > 0) ? null : "not.valid")
                        .text("description"));


let ticketForm = services.frontendMappingDefinition.createFrontendMappingDefinition(
                "ticket", "canAccessGlobalSettings", "canAccessGlobalSettings",
                a => a.datalist("organizations", f => f.getDictionaryRepository().dictionary("organization"))
                        .dropdown("organizationId", "organizations").additionalPrivileges("readOrgData", "canAccessGlobalSettings")
                        .text("name"));

let assignmentForm = services.frontendMappingDefinition.createFrontendMappingDefinition(
                "assignment", "canAccessGlobalSettings", "canAccessGlobalSettings",
                a => a.datalist("organizations", f => f.getDictionaryRepository().dictionary("organization"))
                        .dropdown("organizationId", "organizations").additionalPrivileges("readOrgData", "canAccessGlobalSettings")
                        .datalist("tickets", f => f.getDictionaryRepository().dictionary("ticket"))
                        .datalist("users", f => f.getDictionaryRepository().dictionary("user"))
                        .dropdown("ticketId", "tickets")
                        .dropdown("userId", "users")
                        .hidden("description")
                        .checkbox("billable")
                        .checkbox("researchAndDevelopment")
                        .checkbox("creativeWork"));

services.customisation.registerFrontendMapping(timelogForm, timelogRepository);
services.customisation.registerHtmlCrudController(ticketForm, ticketRepository).setGenericTableFields("name");
services.customisation.registerHtmlCrudController(assignmentForm, assignmentRepository).setGenericTableFields("description","creativeWork","billable","researchAndDevelopment");
services.customisation.registerApiCrudController(timelogForm, timelogRepository).setDefaultControllerPrivilege(com.openkoda.model.Privilege.readOrgData);

model', '{}', 'forms');

INSERT INTO public.event_listener (id, created_by, created_by_id, created_on, modified_by, modified_by_id, updated_on, consumer_class_name, consumer_method_name, consumer_parameter_class_name, event_class_name, event_name, event_object_type, index_string, organization_id, static_data_1, static_data_2, static_data_3, static_data_4) VALUES (281, 'admin@yourdomain.org', 10000, '2023-05-15 18:34:53.61748+02', 'admin@yourdomain.org', 10000, '2023-05-15 18:34:53.61748+02', 'com.openkoda.core.customisation.ServerJSRunner', 'startCustomisationServerJs', 'java.time.LocalDateTime', 'com.openkoda.core.service.event.ApplicationEvent', 'APPLICATION_STARTED', 'java.time.LocalDateTime', 'com.openkoda.core.service.event.applicationevent application_started java.time.localdatetime com.openkoda.core.customisation.serverjsrunner startcustomisationserverjs orgid: 00000/281', NULL, 'forms', '0', '0', '0');
