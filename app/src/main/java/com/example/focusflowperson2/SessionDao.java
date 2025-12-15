package com.example.focusflowperson2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SessionDao {
    @Insert
    void insert(Session session);

    @Delete
    void delete(Session session);

    // --- LOAD PRESETS ---
    @Query("SELECT * FROM session_table WHERE userEmail = :email AND sessionType = 'PRESET'")
    List<Session> getPresetsForUser(String email);

    // --- RECENT SESSION ---
    @Query("SELECT * FROM session_table WHERE userEmail = :email ORDER BY id DESC LIMIT 1")
    Session getLastSessionForUser(String email);

    // --- ACCURATE STATS ---

    // Count only Completed WORK
    @Query("SELECT COUNT(id) FROM session_table WHERE sessionType = 'HISTORY_WORK' AND userEmail = :email")
    int getWorkSessionCount(String email);

    // Count only Completed BREAKS (Short or Long)
    @Query("SELECT COUNT(id) FROM session_table WHERE (sessionType = 'HISTORY_BREAK' OR sessionType = 'HISTORY_LONG_BREAK') AND userEmail = :email")
    int getBreakSessionCount(String email);

    // Total Work Time
    @Query("SELECT SUM(workDuration) FROM session_table WHERE sessionType = 'HISTORY_WORK' AND userEmail = :email")
    int getTotalWorkTime(String email);

    // Total Break Time
    @Query("SELECT SUM(breakDuration) FROM session_table WHERE (sessionType = 'HISTORY_BREAK' OR sessionType = 'HISTORY_LONG_BREAK') AND userEmail = :email")
    int getTotalBreakTime(String email);
}