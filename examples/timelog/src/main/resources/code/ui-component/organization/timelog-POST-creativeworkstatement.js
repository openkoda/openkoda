flow
.thenSet("creativeWorkStatementEntity", a => {
	let form = a.services.data.getForm('creativeWorkStatement');
    form.dto.set("fullName", a.params.get("fullName"));
    form.dto.set("month", a.params.get("month"));
    form.dto.set("year", a.params.get("year"));
    form.dto.set("totalTimeSpent", a.params.get("numberOfHours"));
    form.dto.set("assignments", a.params.get("assignments"));
    form.dto.set("userId", a.params.get("userId"));
    let entity = a.services.data.saveForm(form);
    return entity;
})
.thenSet("reload",a => "");