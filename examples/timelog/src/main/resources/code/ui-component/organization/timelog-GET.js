flow.then(a => a.services.data.getRepository("timelog").findOne(a.params.get("id")) )
  .thenSet("timelog", a => {
                            let f = a.services.data.getForm("timelog", a.result);
                            if(a.params.get("id") == null){
                              f.dto.put("startedOn", a.params.get("startedOn"));
                            }
                            return f;
                            })
 .thenSet("month", a => a.params.get("month") == null ? 0 : a.services.util.parseInt(a.params.get("month")))
 .thenSet("timelogs", a => a.services.data.getRepository('timelog').search( (root, query, cb) => {
    let monthParam = a.params.get("month");
    let month = monthParam == null ? 0 : a.services.util.parseInt(monthParam);
    let now = a.services.util.dateNow();
    let from = now.minusMonths(month + 1);
    let to = now.minusMonths(month);
    return cb.between( root.get("startedOnMonth"), from, to);
  }
  ))
  .thenSet("timelogsSummary", a => a.services.data.getRepository('timelog').summarize(a.model.get("timelogs"), a.model.get("month")))
  .thenSet("assignmentsDescription", a => a.services.data.getRepository('timelog').convertToAssignmentsDescriptionString(a.model.get("timelogsSummary")))
  .thenSet("assignmentsSubmitted", a => a.services.data.getRepository('creativeWorkStatement').search( (root, query, cb) => {
    let userId = a.model.get("userEntityId");
  	let monthValue = a.services.data.getRepository('timelog').getMonthFromSummary(a.model.get("timelogsSummary"));
    let yearValue = a.services.data.getRepository('timelog').getYearFromSummary(a.model.get("timelogsSummary"));
    return cb.and(cb.equal(root.get("userId"), userId), cb.equal(root.get("month"), monthValue), cb.equal(root.get("year"), yearValue));
  }));