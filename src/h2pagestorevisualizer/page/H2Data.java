/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer.page;

/**
 *
 * original source code is org.h2.store.Data
 *
 * @author ysobj
 */
public class H2Data {

    protected byte[] data;
    protected int pos;

    public H2Data(byte[] data) {
        this.data = data;
    }

    /**
     *
     * org.h2.store.Data#readInt()
     *
     * Read an integer at the current position. The current position is
     * incremented.
     *
     * @return the value
     */
    public int readInt() {
        byte[] buff = data;
        int x = (buff[pos] << 24)
                + ((buff[pos + 1] & 0xff) << 16)
                + ((buff[pos + 2] & 0xff) << 8)
                + (buff[pos + 3] & 0xff);
        pos += 4;
        return x;
    }

    /**
     * Get the length of a String. This includes the bytes required to encode
     * the length.
     *
     * @param s the string
     * @return the number of bytes required
     */
    public static int getStringLen(String s) {
        int len = s.length();
        return getStringWithoutLengthLen(s, len) + getVarIntLen(len);
    }

    /**
     * Calculate the length of String, excluding the bytes required to encode
     * the length.
     * <p>
     * For performance reasons the internal representation of a String is
     * similar to UTF-8, but not exactly UTF-8.
     *
     * @param s the string
     * @param len the length of the string
     * @return the number of bytes required
     */
    private static int getStringWithoutLengthLen(String s, int len) {
        int plus = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c >= 0x800) {
                plus += 2;
            } else if (c >= 0x80) {
                plus++;
            }
        }
        return len + plus;
    }

    /**
     * Read a String value. The current position is incremented.
     *
     * @return the value
     */
    public String readString() {
        int len = readVarInt();
        return readString(len);
    }

    /**
     * Read a String from the byte array.
     * <p>
     * For performance reasons the internal representation of a String is
     * similar to UTF-8, but not exactly UTF-8.
     *
     * @param len the length of the resulting string
     * @return the String
     */
    private String readString(int len) {
        byte[] buff = data;
        int p = pos;
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            int x = buff[p++] & 0xff;
            if (x < 0x80) {
                chars[i] = (char) x;
            } else if (x >= 0xe0) {
                chars[i] = (char) (((x & 0xf) << 12)
                        + ((buff[p++] & 0x3f) << 6)
                        + (buff[p++] & 0x3f));
            } else {
                chars[i] = (char) (((x & 0x1f) << 6)
                        + (buff[p++] & 0x3f));
            }
        }
        pos = p;
        return new String(chars);
    }

    /**
     * Get the current write position of this buffer, which is the current
     * length.
     *
     * @return the length
     */
    public int length() {
        return pos;
    }

    /**
     * Get the byte array used for this page.
     *
     * @return the byte array
     */
    public byte[] getBytes() {
        return data;
    }

    /**
     * Set the position to 0.
     */
    public void reset() {
        pos = 0;
    }

    /**
     * Copy a number of bytes to the given buffer from the current position. The
     * current position is incremented accordingly.
     *
     * @param buff the output buffer
     * @param off the offset in the output buffer
     * @param len the number of bytes to copy
     */
    public void read(byte[] buff, int off, int len) {
        System.arraycopy(data, pos, buff, off, len);
        pos += len;
    }

    /**
     * Read one single byte.
     *
     * @return the value
     */
    public byte readByte() {
        return data[pos++];
    }

    /**
     * Read a long value. This method reads two int values and combines them.
     *
     * @return the long value
     */
    public long readLong() {
        return ((long) (readInt()) << 32) + (readInt() & 0xffffffffL);
    }

    /**
     * Set the current read / write position.
     *
     * @param pos the new position
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     * Read an short integer at the current position. The current position is
     * incremented.
     *
     * @return the value
     */
    public short readShortInt() {
        byte[] buff = data;
        return (short) (((buff[pos++] & 0xff) << 8) + (buff[pos++] & 0xff));
    }

    /**
     * The number of bytes required for a variable size int.
     *
     * @param x the value
     * @return the len
     */
    private static int getVarIntLen(int x) {
        if ((x & (-1 << 7)) == 0) {
            return 1;
        } else if ((x & (-1 << 14)) == 0) {
            return 2;
        } else if ((x & (-1 << 21)) == 0) {
            return 3;
        } else if ((x & (-1 << 28)) == 0) {
            return 4;
        }
        return 5;
    }

    /**
     * Read a variable size int.
     *
     * @return the value
     */
    public int readVarInt() {
        int b = data[pos];
        if (b >= 0) {
            pos++;
            return b;
        }
        // a separate function so that this one can be inlined
        return readVarIntRest(b);
    }

    private int readVarIntRest(int b) {
        int x = b & 0x7f;
        b = data[pos + 1];
        if (b >= 0) {
            pos += 2;
            return x | (b << 7);
        }
        x |= (b & 0x7f) << 7;
        b = data[pos + 2];
        if (b >= 0) {
            pos += 3;
            return x | (b << 14);
        }
        x |= (b & 0x7f) << 14;
        b = data[pos + 3];
        if (b >= 0) {
            pos += 4;
            return x | b << 21;
        }
        x |= ((b & 0x7f) << 21) | (data[pos + 4] << 28);
        pos += 5;
        return x;
    }

    /**
     * The number of bytes required for a variable size long.
     *
     * @param x the value
     * @return the len
     */
    public static int getVarLongLen(long x) {
        int i = 1;
        while (true) {
            x >>>= 7;
            if (x == 0) {
                return i;
            }
            i++;
        }
    }

    /**
     * Read a variable size long.
     *
     * @return the value
     */
    public long readVarLong() {
        long x = data[pos++];
        if (x >= 0) {
            return x;
        }
        x &= 0x7f;
        for (int s = 7;; s += 7) {
            long b = data[pos++];
            x |= (b & 0x7f) << s;
            if (b >= 0) {
                return x;
            }
        }
    }

}
