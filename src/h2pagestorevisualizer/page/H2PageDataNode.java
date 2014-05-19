/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer.page;

import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author ysobj
 */
public class H2PageDataNode extends H2Page {

    protected int rowCount;
    protected int indexId;
    protected long[] keys;
    protected int[] childPageIds;

    public H2PageDataNode(int pageId,byte[] data) {
        super(pageId, data);
        this.h2data.readShortInt();
        this.parentPageId = this.h2data.readInt();
        this.indexId = this.h2data.readVarInt();
        this.rowCount = this.h2data.readInt();
        this.entryCount = this.h2data.readShortInt();
        this.keys = new long[entryCount];
        this.childPageIds = new int[entryCount+1];
        childPageIds[entryCount] = this.h2data.readInt();
        for (int i = 0; i < this.entryCount; i++) {
            this.childPageIds[i] = this.h2data.readInt();
            this.keys[i] = this.h2data.readVarLong();
        }
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
        return super.getPageTypeDesc() + String.format(" parentPageId=%d indexId=%d countOfAllChildren=%d entryCount=%d keys=%s childPageIds=%s", this.getParentPageId(), this.getIndexId(), this.getRowCount(), this.getEntryCount(), Arrays.toString(this.keys), Arrays.toString(this.childPageIds));
    }
}
