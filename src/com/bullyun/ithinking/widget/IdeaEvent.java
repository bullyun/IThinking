package com.bullyun.ithinking.widget;

import com.bullyun.ithinking.data.Idea;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;


public class IdeaEvent extends Event {

    private Idea idea;
    private Point2D mouse;
    private IdeaWidget ideaWidget;

    /**
     * Common supertype for all idea event types.
     */
    public static final EventType<IdeaEvent> ANY =
            new EventType<IdeaEvent> (Event.ANY, "ANY_IDEA_EVENT");

    public static final EventType<IdeaEvent> ITV_ADD_LINK =
            new EventType<IdeaEvent> (IdeaEvent.ANY, "ITV_ADD_LINK");

    public static final EventType<IdeaEvent> ITV_OPEN_IDEA =
            new EventType<IdeaEvent> (IdeaEvent.ANY, "ITV_OPEN_IDEA");

    public static final EventType<IdeaEvent> ITV_SELECT_IDEA =
            new EventType<IdeaEvent> (IdeaEvent.ANY, "ITV_SELECT_IDEA");

    public static final EventType<IdeaEvent> IW_FOCUS =
            new EventType<IdeaEvent> (IdeaEvent.ANY, "IW_FOCUS");

    public IdeaEvent(@NamedArg("eventType") EventType<IdeaEvent> eventType) {
        super(null, null, eventType);
    }

    public IdeaEvent(@NamedArg("eventType") EventType<IdeaEvent> eventType,
                     @NamedArg("idea") Idea idea,
                     @NamedArg("mouse") Point2D mouse) {
        this(eventType);
        this.idea = idea;
        this.mouse = mouse;
    }

    public IdeaEvent(@NamedArg("eventType") EventType<IdeaEvent> eventType,
                     @NamedArg("ideaWidget") IdeaWidget ideaWidget) {
        this(eventType);
        this.ideaWidget = ideaWidget;
    }

    public Idea getIdea() {
        return idea;
    }

    public Point2D getMouse() {
        return mouse;
    }

    public IdeaWidget getIdeaWidget() {
        return ideaWidget;
    }
}
