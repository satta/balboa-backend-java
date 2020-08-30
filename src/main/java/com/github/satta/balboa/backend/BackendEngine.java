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
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BackendEngine {
    InputStream in;
    OutputStream out;

    public BackendEngine(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    private void streamResponseStart(OutputStream out) throws IOException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        OuterMessage.pack(MessageID.QUERY_STREAM_START_RESPONSE, packer, new byte[]{});
        packer.flush();
        out.write(packer.toByteArray());
    }

    private void streamResponseEnd(OutputStream out) throws IOException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        OuterMessage.pack(MessageID.QUERY_STREAM_END_RESPONSE, packer, new byte[]{});
        packer.flush();
        out.write(packer.toByteArray());
    }

    private void streamError(OutputStream out, BalboaException e) throws IOException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        MessageBufferPacker innerPacker = MessagePack.newDefaultBufferPacker();
        innerPacker.packMapHeader(1);
        innerPacker.packString("Message");
        innerPacker.packString(e.getMessage());
        innerPacker.flush();
        OuterMessage.pack(MessageID.ERROR_RESPONSE, packer, innerPacker);
        packer.flush();
        out.write(packer.toByteArray());
    }

    private void streamResponseData(OutputStream out, Observation o) throws IOException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        MessageBufferPacker innerPacker = MessagePack.newDefaultBufferPacker();
        o.pack(innerPacker);
        innerPacker.flush();
        OuterMessage.pack(MessageID.QUERY_STREAM_DATA_RESPONSE, packer, innerPacker);
        packer.flush();
        out.write(packer.toByteArray());
    }

    public void run(InputProcessor p) throws IOException, ProtocolException {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(this.in);
        MessageUnpacker innerUnpacker = null;
        while (unpacker.hasNext()) {
            try {
                OuterMessage om = OuterMessage.unpack(unpacker);
                if (innerUnpacker == null) {
                    innerUnpacker = MessagePack.newDefaultUnpacker(om.innerPayload);
                } else {
                    innerUnpacker.reset(new ArrayBufferInput(om.innerPayload));
                }
                try {
                    switch (om.msgid) {
                        case MessageID.INPUT_REQUEST:
                            Observation o = Observation.unpack(innerUnpacker);
                            p.handle(o);
                            break;
                        case MessageID.QUERY_REQUEST:
                            Query q = Query.unpack(innerUnpacker);
                            if (out != null) {
                                streamResponseStart(out);
                                p.handle(q, observation -> {
                                    streamResponseData(out, observation);
                                });
                                streamResponseEnd(out);
                                out.flush();
                            }
                            break;
                        case MessageID.DUMP_REQUEST:
                            DumpRequest dr = DumpRequest.unpack(innerUnpacker);
                            p.handle(dr);
                            return;
                        case MessageID.BACKUP_REQUEST:
                            BackupRequest br = BackupRequest.unpack(innerUnpacker);
                            p.handle(br);
                            return;
                        default:
                            System.out.println("unhandled message type " +
                                    om.msgid + " seen, ignoring");
                            break;
                    }
                } catch (BalboaException e) {
                    streamError(out, e);
                    out.flush();
                    return;
                }
            } catch (Exception e) {
                throw new ProtocolException(e.getMessage());
            }
        }
    }
}

