package io.embesozzi.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.embesozzi.keycloak.event.EventParser;
import io.embesozzi.keycloak.model.AuthorizationModel;
import io.embesozzi.keycloak.service.ServiceHandler;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class OpenFgaEventListenerProvider implements EventListenerProvider {
	private static final Logger LOG = Logger.getLogger(OpenFgaEventListenerProvider.class);
	private ObjectMapper mapper;
	private ServiceHandler service;

	private AuthorizationModel model;
	private KeycloakSession session;

	public OpenFgaEventListenerProvider(AuthorizationModel model, ServiceHandler service, KeycloakSession session) {
		LOG.info("[OpenFgaEventListener] OpenFgaEventListenerProvider initializing...");
		this.service = service;
		this.session = session;
		this.model = model;
		LOG.info("[OpenFgaEventListener] OpenFgaEventListenerProvider initialized with model: " + model.toString());
		mapper = new ObjectMapper();
	}

	@Override
	public void onEvent(Event event) {
		LOG.debug("[OpenFgaEventListener] onEvent type: " + event.getType().toString());
		LOG.debug("[OpenFgaEventListener] Discarding event...");
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
		LOG.debug("[OpenFgaEventListener] onEvent Admin received events");

		try {
			LOG.debugf("[OpenFgaEventListener] admin event: " + mapper.writeValueAsString(adminEvent));
			EventParser eventParser = new EventParser(adminEvent, model, session);
			LOG.debugf("[OpenFgaEventListener] event received: " + eventParser.toString());
			service.handle(adminEvent.getId(), mapper.writeValueAsString(eventParser.toTupleEvent()));
		} catch (IllegalArgumentException e) {
			LOG.warn(e.getMessage());
		}
		catch (JsonProcessingException | ExecutionException | InterruptedException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		// ignore
	}
}
