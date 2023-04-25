INSERT INTO public.frontend_resource VALUES (20000, 'admin@yourdomain.org', 10000, '2023-04-25 10:54:43.749621+02', 'timelog timelog ui_component 00000/20000', 'admin@yourdomain.org', 10000, NULL, '2023-04-25 11:08:27.421751+02', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" lang="en" layout:decorate="~{${defaultLayout}}">

<body>
<div class="container-fluid">
  <div layout:fragment="content">
    <form th:replace="~{form::form(${timelog}, '''')}"></form>
    <div class="table-responsive">
          <a th:href="@{./timelog(week=${week + 1})}">&lt;&lt;Prev Week</a> | <a th:href="@{./timelog(week=${week - 1})}">Next Week&gt;&gt;</a>
      <table class="table table-hover">
        <thead><tr>
        <td>Ticket</td>
        <td>Date</td>
        <td>Time</td>
        <td></td>
        <td></td>
        </tr></thead>
        <tbody><tr th:each="t : ${timelogs}">
        <td th:text="${t.assignment.description}"></td>
        <td th:text="${t.startedOn}"></td>
        <td th:text="${t.duration}"></td>
                <td>
                  <a class="btn btn-sm btn-link" th:href="@{./timelog(id=${t.id})}">Edit</a>
                </td>
                <td>
                  <a th:replace="~{forms::single-link-post-form-with-confirm(@{./timelog/remove(id=${t.id})}, ''Remove'', ''Are you sure?'')}"/>
                </td>
        </tr></tbody>
      </table>
    </div>
  </div>
 </div>
</body>
</html>
', NULL, false, false, 'timelog', NULL, 'UI_COMPONENT', 'timelog');

INSERT INTO public.frontend_resource VALUES (20001, 'admin@yourdomain.org', 10000, '2023-04-25 10:16:24.820479+02', 'dashboard dashboard html 00000/20001', 'admin@yourdomain.org', 10000, NULL, '2023-04-25 13:48:33.151083+02', '<!DOCTYPE html>
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
            <th:block th:each="entry : ${@CRUDControllerConfigurationMap.entrySet()}">
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
</html>', NULL, false, false, 'dashboard', '', 'HTML', 'dashboard');


INSERT INTO public.controller_endpoint VALUES (30001, 'admin@yourdomain.org', 10000, '2023-04-25 10:54:55.062701+02', '', 'admin@yourdomain.org', 10000, NULL, '2023-04-25 10:54:55.062701+02', 'flow.then(a => a.services.data.getRepository("timelog").findOne(a.params.get("id")+"") )
 .thenSet("timelog", a => a.services.data.getForm(''timelog'', a.result))
 .thenSet("week", a => a.params.get("week") == null ? 0 : parseInt(a.params.get("week")))
 .thenSet("timelogs", a => a.services.data.getRepository(''timelog'').findBy( (root, query, cb) => {
    let weekParam = a.params.get("week");
    let week = weekParam == null ? 0 : parseInt(weekParam);
    let now = dateNow();
    let from = now.minusWeeks(week+1);
    let to = now.minusWeeks(week);
    return cb.between( root.get("startedOn"), from, to);
  }
  ));', 20000, NULL, 0, NULL, 'HTML', '');

INSERT INTO public.controller_endpoint VALUES (30002, 'admin@yourdomain.org', 10000, '2023-04-25 10:55:06.9886+02', '', 'admin@yourdomain.org', 10000, NULL, '2023-04-25 10:55:06.9886+02', 'flow
.then(a => a.services.data.getRepository("timelog").findOne( a.params.get(''id'')+"" ))
.thenSet("timelogEntity", a => a.services.data.saveForm(a.form, a.result));', 20000, NULL, 1, NULL, 'HTML', '');

INSERT INTO public.controller_endpoint VALUES (30003, 'admin@yourdomain.org', 10000, '2023-04-25 10:55:28.82499+02', '', 'admin@yourdomain.org', 10000, NULL, '2023-04-25 11:54:11.491098+02', 'flow
.thenSet("timelogEntity", a => a.services.data.getRepository("timelog").deleteOne( a.params.get(''id'') ));
', 20000, NULL, 1, 'timelogEntity', 'MODEL_AS_JSON', 'remove');

