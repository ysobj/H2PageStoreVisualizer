/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer.page;

/**
 *
 * @author ysobj
 */
public class H2PageFactory {

    public static H2Page create(byte[] data) {
        H2Page h2page;
        if ((data[0] & ~16) == 1) {
            h2page = new H2PageDataLeaf(data);
        } else if ((data[0] & ~16) == 2) {
            h2page = new H2PageDataNode(data);
        } else if ((data[0] & ~16) == 4) {
            h2page = new H2PageBtreeLeaf(data);
        } else {
            h2page = new H2Page(data);
        }
        return h2page;
    }

    public static int readInt(byte[] buff, int pos) {
        //org.h2.store.Data#readInt()
        int x = (buff[pos] << 24)
                + ((buff[pos + 1] & 0xff) << 16)
                + ((buff[pos + 2] & 0xff) << 8)
                + (buff[pos + 3] & 0xff);
        return x;
    }
}
