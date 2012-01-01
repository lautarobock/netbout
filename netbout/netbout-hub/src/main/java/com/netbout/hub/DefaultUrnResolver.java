/**
 * Copyright (c) 2009-2011, netBout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code occasionally and without intent to use it, please report this
 * incident to the author by email.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.netbout.hub;

import com.netbout.spi.Identity;
import com.netbout.spi.UnreachableUrnException;
import com.netbout.spi.Urn;
import com.ymock.util.Logger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default URN resolver.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
final class DefaultUrnResolver implements UrnResolver {

    /**
     * The hub.
     */
    private final transient Hub hub;

    /**
     * Loaded already from Hub.
     * @see #initialize()
     */
    private transient boolean initialized;

    /**
     * Namespaces and related URL templates, allocated in slots.
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final transient Map<Urn, Map<String, String>> slots =
        new ConcurrentHashMap<Urn, Map<String, String>>();

    /**
     * Public ctor.
     * @param ihub The hub
     */
    public DefaultUrnResolver(final Hub ihub) {
        this.hub = ihub;
        this.save(new Urn(), "void", "http://www.netbout.com/");
        this.save(new Urn(), "netbout", "http://www.netbout.com/nb");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(final Identity owner, final String namespace,
        final String template) {
        if (!namespace.matches("^[a-z]{1,31}$")) {
            throw new IllegalArgumentException(
                String.format(
                    "Namespace format is not valid '%s'",
                    namespace
                )
            );
        }
        try {
            new URL(template.replace(UrnResolver.MARKER, "-"));
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Template format is not valid '%s'",
                    template
                ),
                ex
            );
        }
        this.save(owner.name(), namespace, template);
        this.hub
            .make("namespace-was-registered")
            .asap()
            .arg(owner.name())
            .arg(namespace)
            .arg(template)
            .asDefault(false)
            .exec();
        Logger.info(
            this,
            "#register('%s', '%s', '%s'): namespace registered",
            owner.name(),
            namespace,
            template
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    public Map<String, String> registered(final Identity owner) {
        this.initialize();
        final Map<String, String> found =
            new ConcurrentHashMap<String, String>();
        if (this.slots.containsKey(owner.name())) {
            for (Map.Entry<String, String> entry
                : this.slots.get(owner.name()).entrySet()) {
                found.put(entry.getKey(), entry.getValue());
            }
        }
        return found;
    }

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (3 lines)
     */
    @Override
    public URL authority(final Urn urn) throws UnreachableUrnException {
        String template;
        try {
            template = this.load(urn.nid());
        } catch (NamespaceNotFoundException ex) {
            throw new UnreachableUrnException(urn, ex);
        }
        URL result;
        try {
            result = new URL(template.replace(UrnResolver.MARKER, urn.nss()));
        } catch (java.net.MalformedURLException ex) {
            throw new UnreachableUrnException(urn, ex);
        }
        Logger.debug(
            this,
            "#authority('%s'): resolved to '%s'",
            urn,
            result
        );
        return result;
    }

    /**
     * Save data.
     * @param urn The identity
     * @param name Name of the namespace
     * @param template The template
     */
    private void save(final Urn urn, final String name, final String template) {
        synchronized (this.slots) {
            if (!this.slots.containsKey(urn)) {
                this.slots.put(urn, new ConcurrentHashMap<String, String>());
            }
            this.slots.get(urn).put(name, template);
        }
    }

    /**
     * When namespace is not found.
     */
    private static final class NamespaceNotFoundException extends Exception {
        /**
         * Public ctor.
         * @param desc Description of the problem
         */
        public NamespaceNotFoundException(final String desc) {
            super(desc);
        }
    }

    /**
     * Load template by namespace.
     * @param name The namespace
     * @return The template
     * @throws DefaultUrnResolver.NamespaceNotFoundException If can't find it
     */
    private String load(final String name)
        throws DefaultUrnResolver.NamespaceNotFoundException {
        synchronized (this.slots) {
            this.initialize();
            String template = null;
            final List<String> all = new ArrayList<String>();
            for (Map<String, String> map : this.slots.values()) {
                if (map.containsKey(name)) {
                    template = map.get(name);
                    break;
                }
                all.addAll(map.keySet());
            }
            if (template == null) {
                throw new NamespaceNotFoundException(
                    Logger.format(
                        "Namespace '%s' is not registered among %[list]s",
                        name,
                        all
                    )
                );
            }
            return template;
        }
    }

    /**
     * Load all slots from persistence storage.
     */
    private void initialize() {
        synchronized (this.slots) {
            if (!this.initialized) {
                final long start = System.currentTimeMillis();
                final List<String> names = this.hub
                    .make("get-all-namespaces")
                    .synchronously()
                    .asDefault(new ArrayList<String>())
                    .exec();
                for (String name : names) {
                    final String template = this.hub
                        .make("get-namespace-template")
                        .synchronously()
                        .arg(name)
                        .exec();
                    final Urn owner = this.hub
                        .make("get-namespace-owner")
                        .synchronously()
                        .arg(name)
                        .exec();
                    assert owner != null;
                    this.save(owner, name, template);
                }
                this.initialized = true;
                if (!names.isEmpty()) {
                    Logger.info(
                        this,
                        "#initialize(): loaded %d namespaces in %dms: %[list]s",
                        names.size(),
                        System.currentTimeMillis() - start,
                        names
                    );
                }
            }
        }
    }

}