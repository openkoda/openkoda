flow
.thenSet("now", a => a.services.util.dateNow())
.thenSet("months", a => Array.from(Array(12).keys()).map((m) => {
	let date = a.model.get("now").minusMonths(m);
	return [m, date.getMonth().toString() + ' ' + date.getYear()];
    }));