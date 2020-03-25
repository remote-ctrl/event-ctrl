package co.remotectrl.eventctrl

interface CtrlCommand<TAggregate : CtrlAggregate<TAggregate>> {

    fun getEventLegend(aggregateId: AggregateId<TAggregate>, version: Int): EventLegend<TAggregate> {
        return EventLegend(
                0.toString(),
                aggregateId.value,
                version
        )
    }

    fun getEvent(eventLegend: EventLegend<TAggregate>): CtrlEvent<TAggregate>

    fun validate(aggregate: TAggregate, validation: CtrlValidation)

    fun executeOn(aggregate: TAggregate): CtrlExecution<TAggregate, CtrlEvent<TAggregate>, CtrlInvalidation> {
        val validation = CtrlValidation(mutableListOf())

        validate(aggregate, validation)

        val validatedItems = validation.invalidInputItems.toTypedArray()

        return if (validatedItems.isNotEmpty()) CtrlExecution.Invalidated(items = validatedItems)
        else {
            CtrlExecution.Validated(
                    event = getEvent(
                            getEventLegend(aggregateId = aggregate.legend.aggregateId, version = aggregate.legend.latestVersion + 1)
                    )
            )
        }
    }
}

@Suppress("FINAL_UPPER_BOUND")
sealed class CtrlExecution<
        TAggregate : CtrlAggregate<TAggregate>,
        out TEvent : CtrlEvent<TAggregate>,
        out TInvalid : CtrlInvalidation
        > {
    class Validated<TAggregate : CtrlAggregate<TAggregate>, out TEvent : CtrlEvent<TAggregate>>(
        val event: CtrlEvent<TAggregate>
    ) : CtrlExecution<TAggregate, TEvent, Nothing>()

    class Invalidated<TAggregate : CtrlAggregate<TAggregate>>(
        val items: Array<CtrlInvalidInput>
    ) : CtrlExecution<TAggregate, Nothing, CtrlInvalidation>()
}

class CtrlInvalidation(val items: Array<CtrlInvalidInput>)

class CtrlValidation(internal val invalidInputItems: MutableList<CtrlInvalidInput>) {
    fun assert(that: () -> Boolean, description: String) {
        when {
            !that() -> invalidInputItems.add(CtrlInvalidInput(description = description))
        }
    }
}

class CtrlInvalidInput(val description: String)
