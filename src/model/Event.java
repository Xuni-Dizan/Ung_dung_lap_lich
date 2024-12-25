package model;

import java.time.LocalDateTime;

public class Event {
	private String name;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String description;
	
	public Event(String name, LocalDateTime startTime, LocalDateTime endTime, String description) {
		super();
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Event [name=" + name + ", startTime=" + startTime + ", endTime=" + endTime + ", description="
				+ description + "]";
	}
	
}
