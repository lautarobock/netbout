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
package com.netbout.hub.predicates;

import com.netbout.hub.Hub;
import com.netbout.hub.Predicate;
import com.netbout.spi.Message;
import com.netbout.spi.Urn;
import com.ymock.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Call predicate by name in Hub.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class CustomPred extends AbstractVarargPred {

    /**
     * Hub to work with.
     */
    private final transient Hub ihub;

    /**
     * Public ctor.
     * @param hub The hub to work with
     * @param name Name of the predicate
     * @param args The arguments
     */
    public CustomPred(final Hub hub, final Urn name,
        final List<Predicate> args) {
        super(name.toString(), args);
        this.ihub = hub;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Message msg, final int pos) {
        final List<Object> values = new ArrayList<Object>();
        for (Predicate pred : this.args()) {
            values.add(pred.evaluate(msg, pos));
        }
        final Object result = this.ihub.make("evaluate-predicate")
            .inBout(msg.bout())
            .arg(msg.bout().number())
            .arg(msg.number())
            .arg(Urn.create(this.name()))
            .arg(values)
            .asDefault(false)
            .exec();
        Logger.debug(
            this,
            "#evaluate(): evaluated '%s': %[type]s",
            this.name(),
            result
        );
        return result;
    }

}
