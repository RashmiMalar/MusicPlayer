package dao;

import model.Song;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {

    public static List<Song> getAllSongs(String keyword) {
        List<Song> list = new ArrayList<>();
        String sql = "SELECT * FROM songs";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE title LIKE ? OR artist LIKE ? OR genre LIKE ?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (sql.contains("LIKE")) {
                String kw = "%" + keyword + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Song s = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("genre"),
                        rs.getString("filepath")
                );
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Song getSongById(int id) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM songs WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("genre"),
                        rs.getString("filepath")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean insertSong(String title, String artist, String genre, String filepath) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO songs(title, artist, genre, filepath) VALUES (?, ?, ?, ?)");
            ps.setString(1, title);
            ps.setString(2, artist);
            ps.setString(3, genre);
            ps.setString(4, filepath);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteSongById(int id) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM songs WHERE id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
