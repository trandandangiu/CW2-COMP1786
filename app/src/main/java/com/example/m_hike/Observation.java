package com.example.m_hike;

public class Observation {
    private int id;
    private int hikeId;
    private String observation;
    private String time;
    private String comment;

    public Observation(int id, int hikeId, String observation, String time, String comment) {
        this.id = id;
        this.hikeId = hikeId;
        this.observation = observation;
        this.time = time;
        this.comment = comment;
    }

    public int getId() { return id; }
    public int getHikeId() { return hikeId; }
    public String getObservation() { return observation; }
    public String getTime() { return time; }
    public String getComment() { return comment; }
}
