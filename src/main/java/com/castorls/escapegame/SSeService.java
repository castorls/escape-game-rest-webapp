package com.castorls.escapegame;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@Path("/")
public class SSeService {

  private Sse sse;
  private OutboundSseEvent.Builder eventBuilder;
  private SseBroadcaster broadcaster;

  public SSeService() {
  }

  @Context
  public void setSse(Sse sse) {
    this.sse = sse;
    this.eventBuilder = sse.newEventBuilder();
    this.broadcaster = sse.newBroadcaster();
  }

  @GET
  @Path("/stream")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public void getStream(@Context SseEventSink sseEventSink) {
    getBroadcaster().register(sseEventSink);
  }

  public void sendMessage(String name, String id, Object data, String comment) {
    OutboundSseEvent.Builder builder = getEventBuilder()
        .id(id);
    // .mediaType(MediaType.APPLICATION_JSON_TYPE);
    // .name(name)

    if (data != null) {
      builder = builder.data(data);
    } else {
      builder = builder.data("");
    }

    if (comment != null) {
      builder = builder.comment(comment);
    }
    OutboundSseEvent sseEvent = builder.build();
    getBroadcaster().broadcast(sseEvent);
  }

  public OutboundSseEvent.Builder getEventBuilder() {
    return eventBuilder;
  }

  public void setEventBuilder(OutboundSseEvent.Builder eventBuilder) {
    this.eventBuilder = eventBuilder;
  }

  public SseBroadcaster getBroadcaster() {
    return broadcaster;
  }

  public void setBroadcaster(SseBroadcaster broadcaster) {
    this.broadcaster = broadcaster;
  }

  public Sse getSse() {
    return sse;
  }
}
