/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer.page;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ysobj
 */
public class H2Page {
    protected int pageId;
    protected final H2Data h2data;
    protected final byte[] rawdata;
    protected final int pageType;
    protected int parentPageId;
    protected int entryCount;

    protected static final Map<Integer, String> typeMap = new HashMap<>();

    static {
        typeMap.put(0, "TYPE_EMPTY");//An empty page.
        typeMap.put(1, "TYPE_DATA_LEAF");//A data leaf page (without overflow: + FLAG_LAST).
        typeMap.put(2, "TYPE_DATA_NODE");//A data node page (never has overflow pages).
        typeMap.put(3, "TYPE_DATA_OVERFLOW"); //A data overflow page (the last page: + FLAG_LAST).
        typeMap.put(4, "TYPE_BTREE_LEAF"); //A b-tree leaf page (without overflow: + FLAG_LAST).
        typeMap.put(5, "TYPE_BTREE_NODE"); //A b-tree node page (never has overflow pages).
        typeMap.put(6, "TYPE_FREE_LIST"); //A page containing a list of free pages (the last page: + FLAG_LAST).
        typeMap.put(7, "TYPE_STREAM_TRUNK"); //A stream trunk page.
        typeMap.put(8, "TYPE_STREAM_DATA"); //A stream data page.
    }

    public H2Page(int pageId, byte[] data) {
        this.pageId = pageId;
        this.rawdata = data;
        this.h2data = new H2Data(data);
        this.pageType = h2data.readByte() & ~16;
    }

    public byte[] getRawData() {
        return this.rawdata;
    }

    public int getPageType() {
        return this.pageType;
    }

    public String getPageTypeDesc() {
        return String.format("%3d:%s",this.pageId,typeMap.get(pageType));
    }
}
