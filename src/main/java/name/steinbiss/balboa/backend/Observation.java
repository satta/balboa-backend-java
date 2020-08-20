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

import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class Observation {
    private String rrname = "";
    private String rdata = "";
    private String rrtype = "";
    private String sensorid = "";
    private int count;
    private int first_seen_ts,
            last_seen_ts;
    private Date first_seen,
            last_seen;

    public String getRrname() {
        return rrname;
    }

    public void setRrname(String rrname) {
        this.rrname = rrname;
    }

    public String getRdata() {
        return rdata;
    }

    public void setRdata(String rdata) {
        this.rdata = rdata;
    }

    public String getRrtype() {
        return rrtype;
    }

    public void setRrtype(String rrtype) {
        this.rrtype = rrtype;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getFirst_seen() {
        return first_seen;
    }
    public Integer getFirst_seen_ts() { return first_seen_ts; }

    public void setFirst_seen(int first_seen) {
        this.first_seen_ts = first_seen;
        this.first_seen = new Date((long) first_seen * 1000);
    }

    public Date getLast_seen() {
        return last_seen;
    }
    public Integer getLast_seen_ts() { return last_seen_ts; }

    public void setLast_seen(int last_seen) {
        this.last_seen_ts = last_seen;
        this.last_seen = new Date((long) last_seen * 1000);
    }

    public String toString() {
        return this.rrname + " : " + this.rdata + " : " + this.rrtype +
                " : " + this.count + " : " + this.first_seen + " : " + this.last_seen;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Observation o2 = (Observation) o;
        return o2.count == this.count &&
                o2.sensorid.equals(this.sensorid) &&
                o2.last_seen_ts == this.last_seen_ts &&
                o2.first_seen_ts == this.first_seen_ts &&
                o2.rdata.equals(this.rdata) &&
                o2.rrname.equals(this.rrname) &&
                o2.rrtype.equals(this.rrtype);
    }

    public static Observation unpack(MessageUnpacker unpacker) throws IOException, ProtocolException {
        Observation o = new Observation();
        if (unpacker.unpackMapHeader() != 7) {
            throw new ProtocolException("inner input map does not have 7 fields");
        }
        while (unpacker.hasNext()) {
            ExtensionTypeHeader eth;
            byte[] pl;
            String iv = unpacker.unpackString();
            switch (iv) {
                case "N":
                    o.setRrname(unpacker.unpackString());
                    break;
                case "D":
                    o.setRdata(unpacker.unpackString());
                    break;
                case "T":
                    o.setRrtype(unpacker.unpackString());
                    break;
                case "I":
                    o.setSensorid(unpacker.unpackString());
                    break;
                case "C":
                    o.setCount(unpacker.unpackInt());
                    break;
                case "F":
                    eth = unpacker.unpackExtensionTypeHeader();
                    pl = unpacker.readPayload(eth.getLength());
                    o.setFirst_seen(ByteBuffer.wrap(pl).getInt(eth.getLength() - 4));
                    break;
                case "L":
                    eth = unpacker.unpackExtensionTypeHeader();
                    pl = unpacker.readPayload(eth.getLength());
                    o.setLast_seen(ByteBuffer.wrap(pl).getInt(eth.getLength() - 4));
                    break;
                default:
                    throw new ProtocolException("unknown field name: " + iv);
            }
        }
        return o;
    }


    public void pack(MessagePacker packer) throws IOException {
        byte[] firstseen = new byte[4];
        ByteBuffer bbfs = ByteBuffer.wrap(firstseen);
        bbfs.putInt(this.first_seen_ts);
        byte[] lastseen = new byte[4];
        ByteBuffer bbls = ByteBuffer.wrap(lastseen);
        bbls.putInt(this.last_seen_ts);
        String sensorid = this.sensorid;
        if (sensorid == null) {
            sensorid = "<none>";
        }
        packer
                .packMapHeader(7)
                .packString("C")
                .packInt(this.count)
                .packString("F")
                .packExtensionTypeHeader((byte) -1, 4)
                .writePayload(firstseen)
                .packString("L")
                .packExtensionTypeHeader((byte) -1, 4)
                .writePayload(lastseen)
                .packString("D")
                .packString(this.rdata)
                .packString("N")
                .packString(this.rrname)
                .packString("T")
                .packString(this.rrtype)
                .packString("I")
                .packString(sensorid);
    }
}
