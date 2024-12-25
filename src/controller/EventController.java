package Controller;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.lang.model.util.Elements;

import org.w3c.dom.events.Event;
public class EventController {
    private List<Event> events;
    public EventController() {
        Elements = new ArrayList<>();
    }
    public void addEvent(Event event) {
        events.add(event);
    }
    public void updateEvent(String name, Event updatedEvent) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getName().equalsIgnoreCase(name)) {
                events.set(i, updatedEvent);
                return;
            }
        }
    }

    public void deleteEvent(String name) {
        events.removeIf(event -> event.getName().equalsIgnoreCase(name));
    }
    public List<Event> listEvents() {
        return events;
    }
    public List<Event> sortEventsByName() {
        events.sort(Comparator.comparing(Event::getName));
        return events;
    }
    public List<Event> sortEventsByStartTime() {
        events.sort(Comparator.comparing(Event::getStartTime));
        return events;
    }
    public List<Event> searchEventsByName(String name) {
        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (event.getName().equalsIgnoreCase(name)) {
                result.add(event);
            }
        }
        return result;
    }
    public List<Event> filterEventsByTimeRange(Date start, Date end) {
        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (!event.getStartTime().before(start) && !event.getEndTime().after(end)) {
                result.add(event);
            }
        }
        return result;
    }
}

