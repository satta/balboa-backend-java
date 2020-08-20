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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BackendEngineTests {
    @Test
    @DisplayName("backend engine parse query from stream")
    public void TestBackendEngineQuery() {
        Query q = new Query();
        q.setSensorid("foo");
        q.setRrtype("rrtype");
        q.setRdata("rdata");
        q.setRrname("rrname");
        q.setLimit(13);
        MessageBufferPacker innerPacker = MessagePack.newDefaultBufferPacker();
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            q.pack(innerPacker);
            OuterMessage.pack(MessageID.QUERY_REQUEST, packer, innerPacker);
            packer.flush();
            ByteArrayInputStream is = new ByteArrayInputStream(packer.toByteArray());
            BackendEngine e = new BackendEngine(is, null);
            TestInputProcessor p = new TestInputProcessor();
            e.run(p);
            assertEquals(p.queries.size(), 1);
            assertEquals(p.queries.get(0).getSensorid(), "foo");
            assertEquals(p.queries.get(0).getLimit(), 13);
            assertEquals(p.queries.get(0).getRdata(), "rdata");
            assertEquals(p.queries.get(0).getRrtype(), "rrtype");
            assertEquals(p.queries.get(0).getRrname(), "rrname");
            assertEquals(p.observations.size(), 0);
            assertEquals(p.dumpRequests.size(), 0);
            assertEquals(p.backupRequests.size(), 0);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("backend engine get response for query")
    public void TestBackendEngineQueryWithResponse() {
        Query q = new Query();
        q.setSensorid("foo");
        q.setRrtype("rrtype");
        q.setRdata("rdata");
        q.setRrname("rrname");
        q.setLimit(13);
        MessageBufferPacker innerPacker = MessagePack.newDefaultBufferPacker();
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            q.pack(innerPacker);
            OuterMessage.pack(MessageID.QUERY_REQUEST, packer, innerPacker);
            packer.flush();
            ByteArrayInputStream is = new ByteArrayInputStream(packer.toByteArray());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BackendEngine e = new BackendEngine(is, os);
            TestInputProcessor p = new TestInputProcessor();
            assertEquals(os.size(), 0);
            e.run(p);
            assertEquals(p.queries.size(), 1);
            assertEquals(p.observations.size(), 0);
            assertEquals(p.dumpRequests.size(), 0);
            assertEquals(p.backupRequests.size(), 0);
            assertTrue(os.size() > 0);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("backend engine parse input from stream")
    public void TestBackendEngineInput() {
        Observation o1 = new Observation();
        o1.setSensorid("foo");
        o1.setRrtype("rrtype");
        o1.setRdata("rdata");
        o1.setRrname("rrname");
        o1.setFirst_seen(12345);
        o1.setLast_seen(54321);
        Observation o2 = new Observation();
        o2.setSensorid("bar");
        o2.setRrtype("rrtype2");
        o2.setRdata("rdata2");
        o2.setRrname("rrname2");
        o2.setFirst_seen(123456);
        o2.setLast_seen(654321);
        try {
            MessageBufferPacker innerPacker = MessagePack.newDefaultBufferPacker();
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            o1.pack(innerPacker);
            OuterMessage.pack(MessageID.INPUT_REQUEST, packer, innerPacker);
            innerPacker = MessagePack.newDefaultBufferPacker();
            o2.pack(innerPacker);
            OuterMessage.pack(MessageID.INPUT_REQUEST, packer, innerPacker);
            packer.flush();
            ByteArrayInputStream is = new ByteArrayInputStream(packer.toByteArray());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BackendEngine e = new BackendEngine(is, os);
            TestInputProcessor p = new TestInputProcessor();
            e.run(p);
            assertEquals(p.queries.size(), 0);
            assertEquals(p.observations.size(), 2);
            assertEquals(p.observations.get(0).getSensorid(), "foo");
            assertEquals(p.observations.get(0).getLast_seen_ts(), 54321);
            assertEquals(p.observations.get(0).getRdata(), "rdata");
            assertEquals(p.observations.get(0).getRrtype(), "rrtype");
            assertEquals(p.observations.get(0).getRrname(), "rrname");
            assertEquals(p.observations.get(1).getSensorid(), "bar");
            assertEquals(p.observations.get(1).getLast_seen_ts(), 654321);
            assertEquals(p.observations.get(1).getRdata(), "rdata2");
            assertEquals(p.observations.get(1).getRrtype(), "rrtype2");
            assertEquals(p.observations.get(1).getRrname(), "rrname2");
            assertEquals(p.dumpRequests.size(), 0);
            assertEquals(p.backupRequests.size(), 0);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("backend engine parse backup request from stream")
    public void TestBackendEngineBackupRequest() {
        BackupRequest b = new BackupRequest();
        b.path = "foo";
        MessageBufferPacker innerPacker = MessagePack.newDefaultBufferPacker();
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            b.pack(innerPacker);
            OuterMessage.pack(MessageID.BACKUP_REQUEST, packer, innerPacker);
            packer.flush();
            ByteArrayInputStream is = new ByteArrayInputStream(packer.toByteArray());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BackendEngine e = new BackendEngine(is, os);
            TestInputProcessor p = new TestInputProcessor();
            e.run(p);
            assertEquals(p.queries.size(), 0);
            assertEquals(p.observations.size(), 0);
            assertEquals(p.dumpRequests.size(), 0);
            assertEquals(p.backupRequests.size(), 1);
            assertEquals(p.backupRequests.get(0).path, "foo");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("backend engine parse dump request from stream")
    public void TestBackendEngineDumpRequest() {
        DumpRequest d = new DumpRequest();
        d.path = "foo";
        MessageBufferPacker innerPacker = MessagePack.newDefaultBufferPacker();
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            d.pack(innerPacker);
            OuterMessage.pack(MessageID.DUMP_REQUEST, packer, innerPacker);
            packer.flush();
            ByteArrayInputStream is = new ByteArrayInputStream(packer.toByteArray());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BackendEngine e = new BackendEngine(is, os);
            TestInputProcessor p = new TestInputProcessor();
            e.run(p);
            assertEquals(p.queries.size(), 0);
            assertEquals(p.observations.size(), 0);
            assertEquals(p.dumpRequests.size(), 1);
            assertEquals(p.dumpRequests.get(0).path, "foo");
            assertEquals(p.backupRequests.size(), 0);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
