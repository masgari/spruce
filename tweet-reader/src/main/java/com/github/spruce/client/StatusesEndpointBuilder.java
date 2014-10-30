package com.github.spruce.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;

import java.util.List;

/**
 * @author mamad
 * @since 29/10/14.
 */
public class StatusesEndpointBuilder {
    private List<String> terms = Lists.newArrayList();

    public static StatusesEndpointBuilder create() {
        return new StatusesEndpointBuilder();
    }

    public StatusesEndpointBuilder terms(Iterable<String> terms) {
        Iterables.addAll(this.terms, terms);
        return this;
    }

    public StatusesEndpointBuilder terms(String... terms) {
        return terms(Sets.newHashSet(terms));
    }

    public StatusesFilterEndpoint get() {
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        if (!terms.isEmpty()) {
            endpoint.trackTerms(terms);
        }
        return endpoint;
    }
}
