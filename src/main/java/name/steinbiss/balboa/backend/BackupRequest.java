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

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class BackupRequest {
    public String path;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BackupRequest d2 = (BackupRequest) o;
        return d2.path.equals(this.path);
    }

    public static BackupRequest unpack(MessageUnpacker unpacker) throws IOException {
        BackupRequest dr = new BackupRequest();
        if (unpacker.unpackMapHeader() != 1) {
            throw new ProtocolException("inner input map does not have 1 field");
        }
        String iv = unpacker.unpackString();
        if (!iv.equals("P")) {
            throw new ProtocolException("inner input map does not have 'P' field");
        }
        dr.path = unpacker.unpackString();
        return dr;
    }

    public void pack(MessagePacker packer) throws IOException {
        packer
                .packMapHeader(1)
                .packString("P")
                .packString(this.path);
    }
}
