/**
 * Copyright (c) 2020 Sascha Steinbiss <sascha@steinbiss.name>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.satta.balboa.backend;

import java.util.List;

public class PrintProcessor implements InputProcessor {

    @Override
    public void handle(Observation o) throws BalboaException {
        System.out.println(o);
    }

    @Override
    public void handle(DumpRequest dr) throws BalboaException {
        System.out.println("Dump request to " + dr.path);
        throw new BalboaException("not implemented yet");
    }

    @Override
    public void handle(BackupRequest br) throws BalboaException {
        System.out.println("Backup request with path " + br.path);
        throw new BalboaException("not implemented yet");
    }

    @Override
    public void handle(Query q, ObservationStreamConsumer osc) throws BalboaException {
        System.out.println("Query " + q);
    }

    @Override
    public void close() {}
}
