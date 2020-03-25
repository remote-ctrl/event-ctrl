package co.remotectrl.eventctrl

class CtrlPlayer<TAggregate : CtrlAggregate<TAggregate>> {
    fun playFor(evts: Array<CtrlEvent<TAggregate>>, aggregate: TAggregate) {
        for (evt in evts) {
            evt.applyTo(MutableAggregate(aggregate = aggregate))
        }
    }
}
