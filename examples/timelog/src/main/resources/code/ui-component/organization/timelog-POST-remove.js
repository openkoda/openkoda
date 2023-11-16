flow
.thenSet("timelogEntity", a => a.services.data.getRepository("timelog").deleteOne( a.params.get('id') ));
