package co.remotectrl.eventctrl

interface CtrlAggregate<TAggregate : CtrlAggregate<TAggregate>> {
    val legend: AggregateLegend<TAggregate>
}

class AggregateId<TAggregate>(val value: String) where TAggregate : CtrlAggregate<TAggregate>

class AggregateLegend<TAggregate : CtrlAggregate<TAggregate>>(
    val aggregateId: AggregateId<TAggregate>,
    val latestVersion: Int
) {
    constructor(
            aggregateIdVal: String,
            latestVersion: Int
    ) : this(AggregateId(value = aggregateIdVal), latestVersion = latestVersion)
}
