package school.hei.admin.endpoint.event.consumer.model;

import school.hei.admin.PojaGenerated;
import school.hei.admin.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
