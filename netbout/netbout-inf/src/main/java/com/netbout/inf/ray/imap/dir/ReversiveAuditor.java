/**
 * Copyright (c) 2009-2012, Netbout.com
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
package com.netbout.inf.ray.imap.dir;

import com.jcabi.log.Logger;
import com.netbout.inf.Attribute;
import com.netbout.inf.ray.imap.Numbers;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.FileUtils;

/**
 * Auditor of reversive data links.
 *
 * <p>Class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
final class ReversiveAuditor implements Auditor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void audit(final Baseline base, final Audit audit) {
        // .
    }

    // /**
    //  * Audit in the directory with an attribute and report problems.
    //  * @param attr The attribute
    //  * @param auditor Listener of problems
    //  */
    // private void audit(final Baseline baseline, final Auditor auditor, final Attribute attr) {
    //     final long start = System.currentTimeMillis();
    //     int count = 0;
    //     try {
    //         final File rfile = this.reverse(attr);
    //         SimpleReverse reverse = null;
    //         if (rfile.length() > 0) {
    //             reverse = new SimpleReverse();
    //             final InputStream stream = new FileInputStream(rfile);
    //             try {
    //                 reverse.load(stream);
    //             } finally {
    //                 stream.close();
    //             }
    //         }
    //         final Iterator<Catalog.Item> items = this.catalog(attr).iterator();
    //         final SimpleNumbers numbers = new SimpleNumbers();
    //         final RandomAccessFile data =
    //             new RandomAccessFile(this.data(attr), "r");
    //         try {
    //             while (items.hasNext()) {
    //                 final Catalog.Item item = items.next();
    //                 data.seek(item.position());
    //                 final InputStream stream =
    //                     Channels.newInputStream(data.getChannel());
    //                 numbers.load(stream);
    //                 // if (reverse != null) {
    //                 //     numbers.audit(auditor, item.value(), reverse);
    //                 // }
    //                 ++count;
    //             }
    //         } finally {
    //             data.close();
    //         }
    //     } catch (IOException ex) {
    //         auditor.problem(ex);
    //     }
    //     Logger.info(
    //         this,
    //         "#audit(): attribute '%s' with %d values in %[ms]s",
    //         attr,
    //         count,
    //         System.currentTimeMillis() - start
    //     );
    // }

}
