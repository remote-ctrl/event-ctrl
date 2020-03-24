package co.remotectrl.eventctrl

interface CtrlCommand<TAggregate : CtrlAggregate<TAggregate>> {

    fun getEventLegend(aggregateId: AggregateId<TAggregate>, version: Int) : EventLegend<TAggregate>{
        return EventLegend(
                0.toString(),
                aggregateId.value,
                version
        )
    }

    fun getEvent(eventLegend: EventLegend<TAggregate>): CtrlEvent<TAggregate>

    fun validate(aggregate: TAggregate, validation: PlayValidation)

    fun executeOn(aggregate: TAggregate): PlayExecution<TAggregate, CtrlEvent<TAggregate>, PlayInvalidation<TAggregate>> {
        val validation = PlayValidation(mutableListOf())

        validate(aggregate, validation)

        val validatedItems = validation.invalidInputItems.toTypedArray()

        return if (validatedItems.isNotEmpty()) PlayExecution.Invalidated(items = validatedItems)
        else {
            PlayExecution.Validated(
                    event = getEvent(
                            getEventLegend(aggregateId = aggregate.legend.aggregateId, version = aggregate.legend.latestVersion + 1)
                    )
            )
        }
    }
}

sealed class PlayExecution<TAggregate : CtrlAggregate<TAggregate>, out TEvent : CtrlEvent<TAggregate>, out TInvalid: PlayInvalidation<TAggregate>>{
    class Validated<TAggregate : CtrlAggregate<TAggregate>, out TEvent : CtrlEvent<TAggregate>>(
            val event: CtrlEvent<TAggregate>
    ) : PlayExecution<TAggregate, TEvent, Nothing>()

    class Invalidated<TAggregate : CtrlAggregate<TAggregate>>(
            val items: Array<PlayInvalidInput>
    ) : PlayExecution<TAggregate, Nothing, PlayInvalidation<TAggregate>>()
}

class PlayInvalidation<TAggregate>(items: Array<PlayInvalidInput>)

class PlayValidation(internal val invalidInputItems: MutableList<PlayInvalidInput>){
    fun assert(that: () -> Boolean, description: String){

        when {
            !that() -> invalidInputItems.add(PlayInvalidInput(description = description))
        }
    }
}

class PlayInvalidInput(val description: String)