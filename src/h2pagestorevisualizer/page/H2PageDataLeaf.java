/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer.page;

import java.util.Map;

/**
 *
 * @author ysobj
 */
public class H2PageDataLeaf extends H2Page {

    protected int columnCount;
    protected int indexId;

    public H2PageDataLeaf(byte[] data) {
        super(data);
        this.h2data.readShortInt();
        this.parentPageId = this.h2data.readInt();
        this.indexId = this.h2data.readVarInt();
        this.columnCount = this.h2data.readVarInt();
        this.entryCount = this.h2data.readShortInt();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getTableId() {
        return indexId;
    }

    public H2Data getH2data() {
        return h2data;
    }

    public byte[] getRawdata() {
        return rawdata;
    }

    public int getParentPageId() {
        return parentPageId;
    }

    public int getEntryCount() {
        return entryCount;
    }

    @Override
    public String getPageTypeDesc() {
        return super.getPageTypeDesc() + String.format(" tableId=%d", this.getTableId());
    }
}
