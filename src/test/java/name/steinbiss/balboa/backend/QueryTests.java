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

public class QueryTests {
    @Test
    @DisplayName("query msgpack round-trip")
    void msgPackRoundtrip() {
        Query q = new Query();
        q.setRrname("foo");
        MessageBufferPacker mp = MessagePack.newDefaultBufferPacker();
        try {
            q.pack(mp);
            mp.flush();
            byte[] v = mp.toByteArray();
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(v);
            Query q2 = Query.unpack(unpacker);
            assertEquals(q, q2);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @DisplayName("query equals override")
    void equalsImplementation() {
        Query q = new Query();
        q.setRrname("foo");
        assertEquals(q, q);
        Query q2 = new Query();
        q2.setRrname("foo");
        assertEquals(q, q2);
        assertEquals(q2, q);
        q2.setRrname("bar");
        assertNotEquals(q, q2);
    }
}
