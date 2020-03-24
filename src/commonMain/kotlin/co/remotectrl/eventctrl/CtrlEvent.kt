package co.remotectrl.eventctrl

interface CtrlEvent<TAggregate : CtrlAggregate<TAggregate>> {

    val legend: EventLegend<TAggregate>

    fun applyChangesTo(aggregate: TAggregate, latestVersion: Int): TAggregate

    fun applyTo(mutable: MutableAggregate<TAggregate>) {
        mutable.aggregate = applyChangesTo(mutable.aggregate, legend.version)
    }
}

class MutableAggregate<TAggregate: CtrlAggregate<TAggregate>>(var aggregate: TAggregate)

class EventId<TAggregate>(val value: String) where TAggregate : CtrlAggregate<TAggregate>

class EventLegend<TAggregate: CtrlAggregate<TAggregate>>(val eventId: EventId<TAggregate>, val aggregateId: AggregateId<TAggregate>, val version: Int){
    constructor(evtIdVal: String, aggregateIdVal: String, version: Int) : this(EventId(evtIdVal), AggregateId(aggregateIdVal), version)
}