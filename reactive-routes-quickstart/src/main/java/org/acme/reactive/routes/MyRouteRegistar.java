package org.acme.reactive.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import io.vertx.ext.web.Router;

@ApplicationScoped
public class MyRouteRegistar {

    public void init(@Observes Router router) {
        router.get("/my-route").handler(rc -> rc.response().end("Hello from my route"));
    }

}
