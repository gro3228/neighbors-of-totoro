package com.example.backend.Service;

import com.example.backend.Model.Day;
import com.example.backend.Model.Event;
import com.example.backend.Model.TimeRange;
import com.example.backend.Model.User;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

@Service
public class UserService {
    @Autowired
    DataSource dataSource;

    //CREATE
    public Object[] createUser(User user) {
        String stmt = ("INSERT INTO \"user\" (email, username, password, avatar) VALUES('%s', '%s', '%s', %d)")
                .formatted(user.getEmail(), user.getUsername(), user.getPassword(), user.getAvatar());
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            int rowsAffected = statement.executeUpdate(stmt, Statement.RETURN_GENERATED_KEYS);
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            UUID key = keys.getObject(5, UUID.class);
            Object[] results = {rowsAffected, key};
            return results;
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Object[2];
    }

    //READ
    public User getUser(UUID userID) {
        String stmt = "SELECT * FROM \"user\" WHERE user_id='%s'".formatted(userID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(stmt);
            User user = new User();
            while(rs.next()) {
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setAvatar(rs.getInt("avatar"));
                user.setUserID(rs.getObject("user_id", UUID.class));
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String stmt = "SELECT * FROM \"user\" WHERE username='%s'".formatted(username);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(stmt);
            User user = new User();
            while(rs.next()) {
                user.setUserID(rs.getObject("user_id", UUID.class));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setAvatar(rs.getInt("avatar"));
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //UPDATE
    public int updateUser(UUID userID, User user) {
        String stmt = ("UPDATE \"user\" SET email='%s', username='%s', password='%s', avatar=%d WHERE user_id='%s'")
                .formatted(user.getEmail(), user.getUsername(), user.getPassword(), user.getAvatar(), userID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return statement.executeUpdate(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    //DELETE
    public int deleteUser(UUID userID) {
        String stmt = "DELETE FROM \"user\" WHERE user_id='%s'".formatted(userID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return statement.executeUpdate(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    //UserParticipatesEvent RELATIONSHIP
    //CREATE
    public int createUserParticipatesEvent(UUID userID, UUID eventID) {
        String st = ("INSERT INTO user_participates_event (user_id, event_id) VALUES ('%s', '%s')")
                .formatted(userID, eventID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return stmt.executeUpdate(st);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    //GET
    public Event getUserEvent(UUID userID, UUID eventID) {
        String stmt = ("SELECT * FROM event " +
                "INNER JOIN user_participates_event upe on event.event_id = upe.event_id " +
                "WHERE user_id='%s' AND upe.event_id='%s'").formatted(userID, eventID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(stmt);
            Event event = new Event();
            while(rs.next()) {
                event.setEventID(rs.getObject("event_id", UUID.class));
                event.setTitle(rs.getString("title"));
            }
            return event;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Event> getUserEvents(UUID userID) {
        String stmt = ("SELECT * FROM event " +
                "INNER JOIN user_participates_event upe on event.event_id = upe.event_id " +
                "WHERE user_id='%s'").formatted(userID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(stmt);
            List<Event> events = new ArrayList<>();
            while(rs.next()) {
                Event event = new Event();
                event.setEventID(rs.getObject("event_id", UUID.class));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                events.add(event);
            }
            return events;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //DELETE
    public int deleteUserParticipatesEvent(UUID userID, UUID eventID) {
        String st = ("DELETE FROM user_participates_event WHERE (user_id='%s' AND event_id='%s')")
                .formatted(userID, eventID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return stmt.executeUpdate(st);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    //user invites user RELATIONSHIP
    //CREATE
    public Object[] createInvitation(UUID inviterID, UUID inviteeID, UUID eventID) {
        String stmt = ("insert into user_invites_user (inviter_id, invitee_id, event_id) " +
                "values('%s', '%s', '%s')").formatted(inviterID, inviteeID, eventID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            int rowsAffected = statement.executeUpdate(stmt, Statement.RETURN_GENERATED_KEYS);
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            UUID inviterKey = keys.getObject(1, UUID.class);
            UUID inviteeKey = keys.getObject(2, UUID.class);
            UUID eventKey = keys.getObject(3, UUID.class);
            Object[] results = {rowsAffected, inviterKey, inviteeKey, eventKey};
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Object[4];
    }

    //READ
    public List<Event> getAllEventByInvitationsReceived(UUID inviteeID){
        String query = ("select * from event " +
                "inner join user_invites_user uiu on uiu.event_id = event.event_id " +
                "where uiu.invitee_id='%s'").formatted(inviteeID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(query);
            List<Event> events = new ArrayList<>();
            while(rs.next()) {
                Event event = new Event();
                event.setEventID(rs.getObject("event_id", UUID.class));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                events.add(event);
            }
            return events;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //DELETE
    public int deleteInvitation(UUID inviterID, UUID inviteeID, UUID eventID) {
        String stmt = ("delete from user_invites_user " +
                "where inviter_id='%s' and invitee_id='%s' and event_id='%s'")
                .formatted(inviterID, inviteeID, eventID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return statement.executeUpdate(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    //UserAvailabilityDay RELATIONSHIP
    //CREATE
    public int createUserAvailabilityDay(TimeRange timeRange, UUID userID, UUID dayID) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        String st = ("INSERT INTO user_availability_day (start_time, end_time, user_id, day_id) VALUES " +
                "('%tc', '%tc', '%s', '%s')")
                .formatted(timeRange.getStartTime(), timeRange.getEndTime(), userID, dayID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return stmt.executeUpdate(st);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    //GET
    public List<TimeRange> getAllAvailabilityRangesForDay(UUID userID, UUID dayID) {
        String query = ("SELECT start_time, end_time " +
                "FROM user_availability_day uad " +
                "WHERE uad.user_id='%s' and uad.day_id='%s'").formatted(userID, dayID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(query);
            List<TimeRange> timeRanges = new ArrayList<>();
            while(rs.next()) {
                TimeRange timeRange = new TimeRange();
                timeRange.setStartTime(rs.getTimestamp("start_time"));
                timeRange.setEndTime(rs.getTimestamp("end_time"));
                timeRanges.add(timeRange);
            }
            return timeRanges;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //DELETE
    public int deleteUserAvailabilityDay(TimeRange timeRange, UUID userID, UUID dayID) {
        String st = ("DELETE FROM user_availability_day WHERE " +
                "start_time='%tc' AND end_time='%tc' AND user_id='%s' AND day_id='%s'")
                .formatted(timeRange.getStartTime(), timeRange.getEndTime(), userID, dayID);
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return stmt.executeUpdate(st);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    public boolean verifyPassword(UUID userID, String hashedPass, String pass) {
        Random rand = new Random(userID.getMostSignificantBits() & Long.MAX_VALUE);
        int randInt = rand.nextInt(1000000000);
        return Hashing.sha256().hashString(pass+randInt, StandardCharsets.UTF_8).toString()
                .equals(hashedPass);
    }
}
