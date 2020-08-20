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
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DumpRequestTests {
    @Test
    @DisplayName("dump request msgpack round-trip")
    void msgPackRoundtrip() {
        DumpRequest d = new DumpRequest();
        d.path = "foo";
        MessageBufferPacker mp = MessagePack.newDefaultBufferPacker();
        try {
            d.pack(mp);
            mp.flush();
            byte[] v = mp.toByteArray();
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(v);
            DumpRequest d2 = DumpRequest.unpack(unpacker);
            assertEquals(d, d2);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @DisplayName("dump request equals override")
    void equalsImplementation() {
        DumpRequest d = new DumpRequest();
        d.path = "foo";
        assertEquals(d, d);
        DumpRequest d2 = new DumpRequest();
        d2.path = "foo";
        assertEquals(d, d2);
        assertEquals(d2, d);
        d2.path = "bar";
        assertNotEquals(d, d2);
    }
}
