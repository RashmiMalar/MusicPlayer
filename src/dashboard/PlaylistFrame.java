package dashboard;

import dao.PlaylistDAO;
import dao.SongDAO;
import model.Song;
import model.User;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class PlaylistFrame extends JFrame {

    private User user;
    private JTable playlistTable;
    private Clip currentClip;

    public PlaylistFrame(User user) {
        this.user = user;
        setTitle("🎵 Your Playlist - SwingBeats");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 45));

        JLabel title = new JLabel("🎶 My Playlist");
        title.setFont(new Font("Verdana", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(50, 30, 400, 40);
        add(title);

        String[] cols = {"ID", "Title", "Artist", "Genre"};
        playlistTable = new JTable(new DefaultTableModel(cols, 0));
        JScrollPane scroll = new JScrollPane(playlistTable);
        scroll.setBounds(50, 100, 800, 400);
        add(scroll);

        JButton playBtn = new JButton("▶ Play");
        playBtn.setBounds(900, 120, 120, 35);
        playBtn.addActionListener(e -> playSelected());
        add(playBtn);

        JButton stopBtn = new JButton("■ Stop");
        stopBtn.setBounds(900, 170, 120, 35);
        stopBtn.addActionListener(e -> stopPlaying());
        add(stopBtn);

        JButton removeBtn = new JButton("🗑 Remove from Playlist");
        removeBtn.setBounds(900, 220, 180, 35);
        removeBtn.addActionListener(e -> removeFromPlaylist());
        add(removeBtn);

        JButton closeBtn = new JButton("⬅ Back to Dashboard");
        closeBtn.setBounds(900, 270, 180, 35);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn);

        loadPlaylist();
        setVisible(true);
    }

    private void loadPlaylist() {
        List<Song> songs = PlaylistDAO.getUserPlaylist(user.getId());
        DefaultTableModel model = (DefaultTableModel) playlistTable.getModel();
        model.setRowCount(0);
        for (Song s : songs) {
            model.addRow(new Object[]{s.getId(), s.getTitle(), s.getArtist(), s.getGenre()});
        }
    }

    private void playSelected() {
        int row = playlistTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a song to play.");
            return;
        }

        int songId = (int) playlistTable.getValueAt(row, 0);
        Song song = SongDAO.getSongById(songId);
        if (song == null) return;

        try {
            stopPlaying(); // if already playing
            File file = new File(song.getFilepath());
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "File not found: " + song.getFilepath());
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioIn);
            currentClip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to play audio.");
        }
    }

    private void stopPlaying() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
        }
    }

    private void removeFromPlaylist() {
        int row = playlistTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a song to remove.");
            return;
        }

        int songId = (int) playlistTable.getValueAt(row, 0);
        boolean removed = PlaylistDAO.removeFromPlaylist(user.getId(), songId);
        if (removed) {
            JOptionPane.showMessageDialog(this, "Removed from playlist.");
            loadPlaylist();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to remove.");
        }
    }
}
