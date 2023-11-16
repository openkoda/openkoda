flow
.then(a => a.services.data.getRepository("timelog").findOne( a.params.get('id')+"" ))
.thenSet("timelogEntity", a => a.services.data.saveForm(a.form, a.result))
.thenSet("redirectUrl", a => "/html/webEndpoint/timelog" + (a.form.dto.get("startedOn") != null ? "?startedOn=" + a.form.dto.get("startedOn") : ""));