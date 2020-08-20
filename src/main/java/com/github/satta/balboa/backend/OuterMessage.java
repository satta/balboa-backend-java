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

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class OuterMessage {
    Integer msgid;
    byte[] innerPayload;

    public String toString() {
        return msgid + " : " + innerPayload.length + " bytes";
    }

    public static void pack(int id, MessageBufferPacker outerPacker,
                            MessageBufferPacker innerPacker) throws IOException {
        innerPacker.flush();
        pack(id, outerPacker, innerPacker.toByteArray());
    }

    public static void pack(int id, MessageBufferPacker outerPacker,
                            byte[] innerBuf) throws IOException {
        outerPacker.packMapHeader(2)
                .packString("T")
                .packInt(id)
                .packString("M")
                .packBinaryHeader(innerBuf.length)
                .addPayload(innerBuf);
        outerPacker.flush();
    }

    public static OuterMessage unpack(MessageUnpacker unpacker) throws IOException, ProtocolException {
        if (unpacker.unpackMapHeader() != 2) {
            throw new ProtocolException("outer map does not have 2 fields");
        }
        OuterMessage om = new OuterMessage();
        for (int i = 0; i < 2; i++) {
            String iv = unpacker.unpackString();
            switch (iv) {
                case "M":
                    int binLen = unpacker.unpackBinaryHeader();
                    om.innerPayload = unpacker.readPayload(binLen);
                    break;
                case "T":
                    om.msgid = unpacker.unpackInt();
                    break;
                default:
            }
        }
        return om;
    }
}
