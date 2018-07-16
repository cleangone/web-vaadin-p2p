package fit.pay2play.web.vaadin.desktop.org.profile;

import com.vaadin.navigator.View;
import xyz.cleangone.web.manager.SessionManager;
import xyz.cleangone.web.vaadin.desktop.org.BasePage;
import xyz.cleangone.web.vaadin.ui.PageDisplayType;

import java.time.format.DateTimeFormatter;

public class PlayPage extends BasePage implements View
{
    public static final String NAME = "Play";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("EEE MM/dd/yyyy hh:mm a");

    public PlayPage()
    {
        // todo - extend singleBannerPage
        super(BannerStyle.Single);
    }

    protected PageDisplayType set(SessionManager sessionManager)
    {
        super.set(sessionManager);

        resetHeader();

        mainLayout.removeAllComponents();

//        Component calendar = createCalendar(sessionManager.getEventManager());
//        VerticalLayout calendarLayout = vertical(calendar, MARGIN_TRUE);
//
//        mainLayout.addComponent(calendarLayout);

        return PageDisplayType.NotApplicable;
    }

//    private Calendar<EventCalendarItem> createCalendar(EventManager eventMgr)
//    {
//        // show dates for all events
//        List<EventDate> eventDates = eventMgr.getEventDates();
//        Map<String, OrgEvent> eventsById = eventMgr.getEventsById();
//
//        BasicItemProvider<EventCalendarItem> itemProvider = new BasicItemProvider<>();
//        List<EventCalendarItem> calendarItems = eventDates.stream()
//            .map(eventDate -> new EventCalendarItem(eventDate, eventsById.get(eventDate.getEventId())))
//            .collect(Collectors.toList());
//        itemProvider.setItems(calendarItems);
//
//        Calendar<EventCalendarItem> calendar = new Calendar<>(itemProvider);
//        calendar.addStyleName("meetings");
//        calendar.setWidth("100%");
//        calendar.setHeight("100%");
//
//        calendar.setItemCaptionAsHtml(true);
//        calendar.setContentMode(ContentMode.HTML);
//        calendar.withMonth(ZonedDateTime.now().getMonth());
//
//        calendar.setHandler(this::onCalendarClick);
//
//        return calendar;
//    }
//
//    private void onCalendarClick(CalendarComponentEvents.ItemClickEvent event)
//    {
//        EventCalendarItem item = (EventCalendarItem)event.getCalendarItem();
//
//        EventDate eventDate = item.getEventDate();
//        OrgEvent orgEvent = item.getEvent();
//        LocalDateTime ldt = eventDate.getLocalDateTime();
//        String dateTimeString = ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
//
//        Notification.show(
//            item.getCaption(), dateTimeString + eventDate.getDetails(), Notification.Type.HUMANIZED_MESSAGE);
//    }
}
