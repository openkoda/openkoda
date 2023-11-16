flow
.thenSet("csvHeaders", a => ["name","date","duration (h)","description","ticket","isBillable","isCreativeWork"])
.thenSet("timelogs", a => a.services.data.getRepository('timelog').findBy( (root, query, cb) => {
    let monthParam = a.params.get("month");
    let month = monthParam == null ? 0 : a.services.util.parseInt(monthParam);
    let now = a.services.util.dateNow();
    let from = now.minusMonths(month).withDayOfMonth(1);
    return cb.equal( root.get("startedOnMonth"), from);
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
;