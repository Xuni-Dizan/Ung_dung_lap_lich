
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleManagerTest {
    @Test
    public void testAddEvent() {
        ScheduleManager manager = new ScheduleManager();
        Event event = new Event("Meeting", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Discuss project");
        manager.addEvent(event);
        assertEquals(1, manager.getEvents().size());
    }

    @Test
    public void testEditEvent() {
        ScheduleManager manager = new ScheduleManager();
        Event event = new Event("Meeting", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Discuss project");
        manager.addEvent(event);

        Event updatedEvent = new Event("Updated Meeting", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), "New description");
        boolean result = manager.editEvent("Meeting", updatedEvent);
        assertTrue(result);
        assertEquals("Updated Meeting", manager.getEvents().get(0).getName());
    }

    @Test
    public void testDeleteEvent() {
        ScheduleManager manager = new ScheduleManager();
        Event event = new Event("Meeting", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Discuss project");
        manager.addEvent(event);

        boolean result = manager.deleteEvent("Meeting");
        assertTrue(result);
        assertEquals(0, manager.getEvents().size());
    }
}
