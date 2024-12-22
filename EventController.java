package Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.events.Event;

public class EventController {
	private List<Event> events = new ArrayList<>();

    public void addEvent(String name, LocalDateTime startTime, LocalDateTime endTime, String description) {
        events.add(new Event(name, startTime, endTime, description));
    }

    public void editEvent(String name, String newName, LocalDateTime newStartTime, LocalDateTime newEndTime, String newDescription) {
        Event event = findEventByName(name);
        if (event != null) {
            event.setName(newName);
            event.setStartTime(newStartTime);
            event.setEndTime(newEndTime);
            event.setDescription(newDescription);
        }
    }

    public void deleteEvent(String name) {
        events.removeIf(event -> event.getName().equalsIgnoreCase(name));
    }

    public List<Event> listEvents() {
        return events;
    }

    public List<Event> sortEventsByStartTime() {
        return events.stream()
                .sorted(Comparator.comparing(Event::getStartTime))
                .collect(Collectors.toList());
    }

    public List<Event> sortEventsByName() {
        return events.stream()
                .sorted(Comparator.comparing(Event::getName))
                .collect(Collectors.toList());
    }

    public List<Event> searchEventByName(String name) {
        return events.stream()
                .filter(event -> event.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Event> filterEventsByTime(LocalDateTime start, LocalDateTime end) {
        return events.stream()
                .filter(event -> !event.getStartTime().isBefore(start) && !event.getEndTime().isAfter(end))
                .collect(Collectors.toList());
    }

    private Event findEventByName(String name) {
        return events.stream()
                .filter(event -> event.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
