// src/view/Task.java
package model;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String startTime;
    private String endTime;
    private String status;

    public Task(String name, String startTime, String endTime, String status) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getStartTime() {
	    return startTime;
	}

	public void setStartTime(String startTime) {
	    this.startTime = startTime;
	}

	public String getEndTime() {
	    return endTime;
	}

	public void setEndTime(String endTime) {
	    this.endTime = endTime;
	}

	public String getStatus() {
	    return status;
	}

	public void setStatus(String status) {
	    this.status = status;
	}
}