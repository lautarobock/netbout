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
package com.netbout.rest.period;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.ws.rs.core.UriBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link PeriodsBuilder}.
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class PeriodsBuilderTest {

    /**
     * The builder to test.
     */
    private transient PeriodsBuilder builder;

    /**
     * Initialize builder.
     * @throws Exception If there is some problem inside
     */
    @Before
    public void prepareBuilder() throws Exception {
        final Period period = new PosPeriod().next(this.date("2008-08-24"));
        this.builder = new PeriodsBuilder(period, UriBuilder.fromPath("/"))
            .setQueryParam("abc");
    }

    /**
     * PeriodsBuilder can return empty set of links if no extra periods found.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void returnsEmptySetOfLinksForSmallGroup() throws Exception {
        MatcherAssert.assertThat(
            "this date should be visible to users",
            this.builder.show(this.date("2008-08-22"))
        );
        MatcherAssert.assertThat(
            "still have space to show",
            this.builder.more()
        );
        MatcherAssert.assertThat(
            "is empty, since no slides should be linked",
            this.builder.links().isEmpty()
        );
    }

    /**
     * PeriodsBuilder can return one link.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void returnsOneLinkForTwoGroups() throws Exception {
        for (int day = Period.MAX; day > 0; day -= 1) {
            MatcherAssert.assertThat(
                "should be visible",
                this.builder.show(this.date(String.format("2008-08-%02d", day)))
            );
            MatcherAssert.assertThat(
                "need to show more, we're still in visible slide",
                this.builder.more()
            );
        }
        MatcherAssert.assertThat(
            "should be hidden",
            !this.builder.show(this.date("2008-01-01"))
        );
        MatcherAssert.assertThat(
            "but we still have space for more dates",
            this.builder.more()
        );
        MatcherAssert.assertThat(
            this.builder.links().get(0),
            // @checkstyle MultipleStringLiterals (2 lines)
            Matchers.hasProperty("rel", Matchers.equalTo("more"))
        );
    }

    /**
     * PeriodsBuilder can return two links.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void returnsTwoLinksForThreeGroups() throws Exception {
        for (int day = Period.MAX * 2; day > 0; day -= 1) {
            this.builder.show(this.date(String.format("2008-07-%02d", day)));
            MatcherAssert.assertThat("still have space", this.builder.more());
        }
        MatcherAssert.assertThat(
            "it shouldn't be visible",
            !this.builder.show(this.date("2004-01-01"))
        );
        MatcherAssert.assertThat("that's it", !this.builder.more());
        MatcherAssert.assertThat(
            this.builder.links().get(PeriodsBuilder.MAX_LINKS - 1),
            // @checkstyle MultipleStringLiterals (2 lines)
            Matchers.hasProperty("rel", Matchers.equalTo("earliest"))
        );
    }

    /**
     * String to date.
     * @param text The text
     * @return The date
     * @throws java.text.ParseException If failed to parse
     */
    private Date date(final String text) throws java.text.ParseException {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(text);
    }

}
