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

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class Query {
    private String rrname,
            rdata,
            rrtype,
            sensorid;
    private int limit;

    public String getRrname() { return rrname; }

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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public String toString() {
        return this.rrname + " : " + this.rdata + " : " + this.rrtype + " : " + limit;
    }

    private static boolean equalsNullable(String a, String b) {
        if (a == null) {
            return b == null;
        } else {
            if (b == null) {
                return false;
            } else {
                return a.equals(b);
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Query q2 = (Query) o;
        return equalsNullable(q2.rrname, this.rrname) &&
                equalsNullable(q2.rdata, this.rdata) &&
                equalsNullable(q2.rrtype, this.rrtype) &&
                equalsNullable(q2.sensorid, this.sensorid) &&
                q2.limit == this.limit;
    }

    public static Query unpack(MessageUnpacker unpacker) throws IOException {
        Query q = new Query();
        if (unpacker.unpackMapHeader() != 9) {
            throw new ProtocolException("inner input map does not have 9 fields");
        }
        boolean hrrname = false, hrdata = false,
                hsensorid = false, hrrtype = false;
        String rrname = null, rdata = null,
                sensorid = null, rrtype = null;
        while (unpacker.hasNext()) {
            String iv = unpacker.unpackString();
            switch (iv) {
                case "Qrrname":
                    rrname = unpacker.unpackString();
                    break;
                case "Qrdata":
                    rdata = unpacker.unpackString();
                    break;
                case "Qrrtype":
                    rrtype = unpacker.unpackString();
                    break;
                case "QsensorID":
                    sensorid = unpacker.unpackString();
                    break;
                case "Hrrname":
                    hrrname = unpacker.unpackBoolean();
                    break;
                case "Hrdata":
                    hrdata = unpacker.unpackBoolean();
                    break;
                case "Hrrtype":
                    hrrtype = unpacker.unpackBoolean();
                    break;
                case "HsensorID":
                    hsensorid = unpacker.unpackBoolean();
                    break;
                case "Limit":
                    q.setLimit(unpacker.unpackInt());
                    break;
                default:
                    throw new ProtocolException("unknown field name: " + iv);
            }
        }
        if (hrrname) {
            q.setRrname(rrname);
        }
        if (hrdata) {
            q.setRdata(rdata);
        }
        if (hrrtype) {
            q.setRrtype(rrtype);
        }
        if (hsensorid) {
            q.setSensorid(sensorid);
        }
        return q;
    }

    public void pack(MessagePacker packer) throws IOException {
        String rrname = this.rrname;
        if (this.rrname == null) {
            rrname = "<null>";
        }
        String rdata = this.rdata;
        if (this.rdata == null) {
            rdata = "<null>";
        }
        String rrtype = this.rrtype;
        if (this.rrtype == null) {
            rrtype = "<null>";
        }
        String sensorid = this.sensorid;
        if (this.sensorid == null) {
            sensorid = "<null>";
        }
        packer
                .packMapHeader(9)
                .packString("Qrrname")
                .packString(rrname)
                .packString("Qrdata")
                .packString(rdata)
                .packString("Qrrtype")
                .packString(rrtype)
                .packString("QsensorID")
                .packString(sensorid)
                .packString("Hrrname")
                .packBoolean(this.rrname != null)
                .packString("Hrdata")
                .packBoolean(this.rdata != null)
                .packString("Hrrtype")
                .packBoolean(this.rrtype != null)
                .packString("HsensorID")
                .packBoolean(this.sensorid != null)
                .packString("Limit")
                .packInt(this.limit);
        packer.flush();
    }
}
