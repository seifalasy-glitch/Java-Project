package model;

/** A lecture session. Its extra detail is the topic of the lecture. */
public class LectureSession extends AttendanceSession {

    private String topic;

    public LectureSession(Course course, String topic) {
        super(course);
        this.topic = topic;
    }

    public LectureSession(int id, Course course, String date, boolean closed, String topic) {
        super(id, course, date, closed);
        this.topic = topic;
    }

    @Override
    public String getType() {
        return "Lecture";
    }

    @Override
    public String getDetails() {
        return "Topic: " + topic;
    }

    public String getTopic() {
        return topic;
    }
}
