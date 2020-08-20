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

import java.util.ArrayList;
import java.util.List;

public class TestInputProcessor implements InputProcessor {
    public List<Observation> observations;
    public List<DumpRequest> dumpRequests;
    public List<BackupRequest> backupRequests;
    public List<Query> queries;

    public TestInputProcessor() {
        observations = new ArrayList<>();
        dumpRequests = new ArrayList<>();
        backupRequests = new ArrayList<>();
        queries = new ArrayList<>();

    }

    @Override
    public void handle(Observation o) throws BalboaException {
        observations.add(o);
    }

    @Override
    public void handle(DumpRequest d) throws BalboaException {
        dumpRequests.add(d);
    }

    @Override
    public void handle(BackupRequest b) throws BalboaException {
        backupRequests.add(b);
    }

    @Override
    public void handle(Query q, List<Observation> obs) throws BalboaException {
        queries.add(q);
        Observation o = new Observation();
        o.setRrname("foobarbaz");
        obs.add(o);
    }

    @Override
    public void close() {};
}
