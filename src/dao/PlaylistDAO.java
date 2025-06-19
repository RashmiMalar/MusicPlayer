package dao;
import model.Song;
import utils.DBUtil;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
public class PlaylistDAO {

    public static boolean addToPlaylist(int userId, int songId) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement check = conn.prepareStatement(
                "SELECT id FROM playlists WHERE user_id = ? AND song_id = ?"
            );
            check.setInt(1, userId);
            check.setInt(2, songId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return false;

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO playlists(user_id, song_id) VALUES (?, ?)"
            );
            ps.setInt(1, userId);
            ps.setInt(2, songId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<Song> getUserPlaylist(int userId) {
        List<Song> list = new ArrayList<>();
        String sql = "SELECT s.* FROM songs s JOIN playlists p ON s.id = p.song_id WHERE p.user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
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

    public static boolean removeFromPlaylist(int userId, int songId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM playlists WHERE user_id = ? AND song_id = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, songId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
