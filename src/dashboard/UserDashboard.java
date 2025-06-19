package dashboard;

import dao.SongDAO;
import dao.PlaylistDAO;
import model.Song;
import model.User;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class UserDashboard extends JFrame {

    private User user;
    private JTable songTable;
    private JTextField searchField;
    private JButton playBtn, stopBtn, addBtn;
    private Clip currentClip;

    public UserDashboard(User user) {
        this.user = user;
        setTitle("SwingBeats - Music Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(20, 20, 30));

        JLabel welcome = new JLabel("Welcome, " + user.getName());
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setForeground(Color.WHITE);
        welcome.setBounds(50, 30, 400, 30);
        add(welcome);

        searchField = new JTextField();
        searchField.setBounds(50, 80, 300, 30);
        add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(360, 80, 100, 30);
        searchBtn.addActionListener(e -> loadSongs(searchField.getText()));
        add(searchBtn);

        // Table
        String[] columns = {"ID", "Title", "Artist", "Genre"};
        songTable = new JTable(new DefaultTableModel(columns, 0));
        JScrollPane sp = new JScrollPane(songTable);
        sp.setBounds(50, 130, 800, 400);
        add(sp);

        playBtn = new JButton("▶ Play");
        playBtn.setBounds(880, 130, 100, 40);
        playBtn.addActionListener(e -> playSelectedSong());
        add(playBtn);

        stopBtn = new JButton("■ Stop");
        stopBtn.setBounds(880, 180, 100, 40);
        stopBtn.addActionListener(e -> stopSong());
        add(stopBtn);

        addBtn = new JButton("➕ Add to Playlist");
        addBtn.setBounds(880, 230, 150, 40);
        addBtn.addActionListener(e -> addToPlaylist());
        add(addBtn);
        
        JButton openPlaylist = new JButton("🎼 My Playlist");
        openPlaylist.setBounds(880, 280, 150, 40);
        openPlaylist.addActionListener(e -> new PlaylistFrame(user));
        add(openPlaylist);


        loadSongs(null);
        setVisible(true);
    }

    private void loadSongs(String keyword) {
        List<Song> songs = SongDAO.getAllSongs(keyword);
        DefaultTableModel model = (DefaultTableModel) songTable.getModel();
        model.setRowCount(0);
        for (Song s : songs) {
            model.addRow(new Object[]{s.getId(), s.getTitle(), s.getArtist(), s.getGenre()});
        }
    }

    private void playSelectedSong() {
        int row = songTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a song first!");
            return;
        }
        int songId = (int) songTable.getValueAt(row, 0);
        Song song = SongDAO.getSongById(songId);
        if (song == null) return;

        try {
            stopSong(); // Stop previous if any
            File audioFile = new File(song.getFilepath());
            if (!audioFile.exists()) {
                JOptionPane.showMessageDialog(this, "File not found: " + song.getFilepath());
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioIn);
            currentClip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to play song.");
        }
    }

    private void stopSong() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
        }
    }

    private void addToPlaylist() {
        int row = songTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a song to add!");
            return;
        }
        int songId = (int) songTable.getValueAt(row, 0);
        boolean added = PlaylistDAO.addToPlaylist(user.getId(), songId);
        if (added) {
            JOptionPane.showMessageDialog(this, "Song added to playlist!");
        } else {
            JOptionPane.showMessageDialog(this, "Already in playlist or error.");
        }
    }
}
