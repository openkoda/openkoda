flow
.thenSet("timelogs", a => a.services.data.getRepository('timelog').findBy( (root, query, cb) => {
    let now = a.services.util.dateNow();
    let from = now.withDayOfMonth(1);
	query.orderBy(
      cb.desc(root.get("createdOn")));
    return cb.equal( root.get("startedOnMonth"), from);
  }
))
.thenSet("totalLoggedHours", a => a.model.get("timelogs").length > 0 ? (a.model.get("timelogs").map(t => t.getDuration()).reduce((a,b) => a+b) / 3600).toFixed(2) : 0 )
.thenSet("totalHolidayTimelogs", a => a.model.get("timelogs").filter(t => t.getAssignment().getTicket().getName().toLowerCase().includes("holiday")))
.thenSet("totalHolidayHours", a => a.model.get("totalHolidayTimelogs").length > 0 ? (a.model.get("totalHolidayTimelogs").map(t => t.getDuration()).reduce((a,b) => a+b) / 3600).toFixed(2)  : 0)
.thenSet("recentTimelogs", a => a.model.get("timelogs").slice(0,10))
.thenSet("usersTimelogs", a => a.model.get("timelogs").reduce(
    (entryMap, e) => entryMap.set(e.getAssignment().getUser(), [...entryMap.get(e.id)||[], e]),
    new Map()
))
.thenSet("usersTimelogsSum", a => {
	let res = {};
	a.model.get("usersTimelogs").forEach((values, keys) => {
        res[keys.getId()] = {'user': keys.getName(), 'sum': values.length > 0 ? (values.map(t => t.getDuration()).reduce((a,b) => a+b) / 3600).toFixed(2) : 0 };
	});
    return res;
})
