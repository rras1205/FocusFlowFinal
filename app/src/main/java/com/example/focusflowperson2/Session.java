package com.example.focusflowperson2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "session_table")
public class Session {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Stores who owns this session (fixes the "Shared Data" problem)
    public String userEmail;

    public String sessionName;
    public int workDuration;
    public int breakDuration;
    public int longBreakDuration;

    // Stores if it was Work or Break (fixes the "Stats" problem)
    public String sessionType;

    // Updated Constructor
    public Session(String userEmail, String sessionName, int workDuration, int breakDuration, int longBreakDuration, String sessionType) {
        this.userEmail = userEmail;
        this.sessionName = sessionName;
        this.workDuration = workDuration;
        this.breakDuration = breakDuration;
        this.longBreakDuration = longBreakDuration;
        this.sessionType = sessionType;
    }
}