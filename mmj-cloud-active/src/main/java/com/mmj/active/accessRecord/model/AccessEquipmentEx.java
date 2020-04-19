package com.mmj.active.accessRecord.model;

import java.util.Date;

public class AccessEquipmentEx extends AccessEquipment {

    private Date createdTimeStart;

    private Date createdTimeEnd;

    public Date getCreatedTimeStart() {
        return createdTimeStart;
    }

    public void setCreatedTimeStart(Date createdTimeStart) {
        this.createdTimeStart = createdTimeStart;
    }

    public Date getCreatedTimeEnd() {
        return createdTimeEnd;
    }

    public void setCreatedTimeEnd(Date createdTimeEnd) {
        this.createdTimeEnd = createdTimeEnd;
    }
}
