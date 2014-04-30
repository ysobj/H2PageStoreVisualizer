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
public class H2PageDataLeaf extends H2Page {
    protected int columnCount;
    public H2PageDataLeaf(byte[] data) {
        super(data);
    }
    
}
