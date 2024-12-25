package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventManager {
	private List<Event> events;
	
	public EventManager() {
		events = new ArrayList<>();
	}
	
	//Them su kien
	public boolean addEvent(Event event) {
		//Kiem tra ten su kien co rong hay null khong
		if(event.getName() == null || event.getName().trim().isEmpty()) {
			return false;
		}
		//Kiem tra thoi gian bat dau co truoc thoi gian ket thuc khong
		if (event.getStartTime().isAfter(event.getEndTime()) 
				|| event.getStartTime().isEqual(event.getEndTime())) {
	        return false;
	    }
		
		events.add(event);
		return true;
	}
	
	//Chinh sua su kien da co
	public boolean updateEvent(String name, LocalDateTime newStartTime, LocalDateTime newEndTime, String newDesc) {
		for(Event event : events) {
			if(event.getName().equalsIgnoreCase(name)) {
				event.setStartTime(newStartTime);
				event.setEndTime(newEndTime);
				event.setDescription(newDesc);
				return true;
			}
		}
		
		return false;
	}
	
	//Xoa su kien
	public boolean deleteEvent(String name) {
		return events.removeIf(event -> event.getName().equalsIgnoreCase(name));
	}
	
	//Xem danh sach su kien
	public List<Event> showEvents(){
		return events;
	}
	
	//Sap xep su kien theo thoi gian bat dau
	public void sortByStartTime(){
		Collections.sort(events, Comparator.comparing(Event::getStartTime));
	}
	
	//Sap xep su kien theo ten su kien
	public void sortByName() {
		Collections.sort(events, Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER));
	}
	
	//Tim kiem su kien theo ten
	public Event findByName(String name) {
		for(Event event : events) {
			if(event.getName().equalsIgnoreCase(name)) {
				return event;
			}
		}
		return null;
	}
	
	//Loc su kien theo khoang thoi gian cu the
	public List<Event> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime){
		List<Event> filtheredEvents = new ArrayList<>();
		for(Event event : events) {
			if(event.getStartTime().isAfter(startTime) && event.getEndTime().isBefore(endTime)) {
				filtheredEvents.add(event);
			}
		}
		return filtheredEvents;
	}
}
