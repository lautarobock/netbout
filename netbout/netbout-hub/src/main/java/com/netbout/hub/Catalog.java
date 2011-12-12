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

import com.netbout.spi.Helper;
import com.netbout.spi.Identity;
import com.netbout.spi.UnreachableIdentityException;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Catalog of all known identities.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
interface Catalog {

    /**
     * Create statistics in the given XML document and return their element.
     * @param doc The document to work in
     * @return The element just created
     */
    Element stats(final Document doc);

    /**
     * Make new identity or find existing one.
     * @param name The name of identity
     * @return Identity found
     * @throws UnreachableIdentityException If can't reach it by name
     * @checkstyle RedundantThrows (4 lines)
     */
    Identity make(String name) throws UnreachableIdentityException;

    /**
     * Make new identity for the specified user, or find existing one and
     * assign to this user.
     * @param name The name of identity
     * @param user Name of the user
     * @return Identity found or created
     * @throws UnreachableIdentityException If can't reach it by name
     * @checkstyle RedundantThrows (4 lines)
     */
    Identity make(String name, User user) throws UnreachableIdentityException;

    /**
     * Promote existing identity to the helper.
     * @param identity The identity to promote
     * @param helper The helper to use
     */
    void promote(Identity identity, Helper helper);

    /**
     * Find identities by name (including aliases).
     * @param keyword The keyword to find by
     * @return Identities found
     */
    Set<Identity> findByKeyword(String keyword);

}