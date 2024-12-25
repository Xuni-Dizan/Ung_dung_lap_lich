// src/view/TabInfo.java
package model;

import java.io.Serializable;
import java.util.Date;

public class TabInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private Date selectedDate;

    public TabInfo(String title, Date selectedDate) {
        this.title = title;
        this.selectedDate = selectedDate;
    }

    public String getTitle() {
        return title;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }
}