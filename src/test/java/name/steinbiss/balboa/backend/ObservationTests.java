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
package name.steinbiss.balboa.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ObservationTests {
    @Test
    @DisplayName("observation msgpack round-trip")
    void msgPackRoundtrip() {
        Observation o = new Observation();
        o.setRdata("rdata");
        o.setRrname("rrname");
        o.setRrtype("X");
        o.setFirst_seen(12345);
        o.setLast_seen(54321);
        o.setCount(2);
        MessageBufferPacker mp = MessagePack.newDefaultBufferPacker();
        try {
            o.pack(mp);
            mp.flush();
            byte[] v = mp.toByteArray();
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(v);
            Observation o2 = Observation.unpack(unpacker);
            assertEquals(o, o2);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @DisplayName("observation equals override")
    void equalsImplementation() {
        Observation o = new Observation();
        o.setRdata("rdata");
        o.setRrname("rrname");
        o.setRrtype("X");
        o.setFirst_seen(12345);
        o.setLast_seen(54321);
        o.setCount(2);
        assertEquals(o, o);
        Observation o2 = new Observation();
        o2.setRdata("rdata");
        o2.setRrname("rrname");
        o2.setRrtype("X");
        o2.setFirst_seen(12345);
        o2.setLast_seen(54321);
        o2.setCount(2);
        assertEquals(o, o2);
        assertEquals(o2, o);
        o2.setCount(3);
        assertNotEquals(o, o2);
    }
}
