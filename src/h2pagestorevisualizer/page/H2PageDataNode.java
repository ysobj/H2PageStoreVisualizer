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
public class H2PageDataNode extends H2Page {

    protected int rowCount;
    protected int indexId;

    public H2PageDataNode(byte[] data) {
        super(data);
        this.h2data.readShortInt();
        this.parentPageId = this.h2data.readInt();
        this.indexId = this.h2data.readVarInt();
        this.rowCount = this.h2data.readVarInt();
        this.entryCount = this.h2data.readShortInt();
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getIndexId() {
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
        return super.getPageTypeDesc() + String.format(" indexId=%d rowCount=%d entryCount=%d", this.getIndexId(), this.getRowCount(), this.getEntryCount());
    }
}
