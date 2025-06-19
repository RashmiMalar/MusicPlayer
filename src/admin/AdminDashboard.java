package admin;

import dao.SongDAO;
import dao.UserDAO;
import model.Song;
import model.User;
import utils.DBUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private User admin;
    private JTable userTable, songTable;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - SwingBeats");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(25, 25, 35));

        JLabel heading = new JLabel("🎛 SwingBeats - Admin Panel");
        heading.setFont(new Font("Verdana", Font.BOLD, 26));
        heading.setForeground(Color.WHITE);
        heading.setBounds(50, 20, 600, 40);
        add(heading);

        // === USERS TABLE ===
        JLabel usersLabel = new JLabel("👤 Manage Users");
        usersLabel.setForeground(Color.LIGHT_GRAY);
        usersLabel.setBounds(50, 80, 200, 25);
        add(usersLabel);

        userTable = new JTable(new DefaultTableModel(new String[]{"ID", "Name", "Email", "Role"}, 0));
        JScrollPane userScroll = new JScrollPane(userTable);
        userScroll.setBounds(50, 110, 600, 200);
        add(userScroll);

        JButton deleteUserBtn = new JButton("🗑 Delete User");
        deleteUserBtn.setBounds(670, 110, 150, 35);
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());
        add(deleteUserBtn);

        // === SONGS TABLE ===
        JLabel songsLabel = new JLabel("🎵 Manage Songs");
        songsLabel.setForeground(Color.LIGHT_GRAY);
        songsLabel.setBounds(50, 330, 200, 25);
        add(songsLabel);

        songTable = new JTable(new DefaultTableModel(new String[]{"ID", "Title", "Artist", "Genre", "File"}, 0));
        JScrollPane songScroll = new JScrollPane(songTable);
        songScroll.setBounds(50, 360, 900, 200);
        add(songScroll);

        JButton deleteSongBtn = new JButton("🗑 Delete Song");
        deleteSongBtn.setBounds(980, 360, 150, 35);
        deleteSongBtn.addActionListener(e -> deleteSelectedSong());
        add(deleteSongBtn);

        JButton addSongBtn = new JButton("➕ Upload New Song");
        addSongBtn.setBounds(980, 410, 180, 35);
        addSongBtn.addActionListener(e -> uploadNewSong());
        add(addSongBtn);

        loadUsers();
        loadSongs();

        setVisible(true);
    }

    private void loadUsers() {
        List<User> users = UserDAO.getAllUsers();
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);
        for (User u : users) {
            model.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()});
        }
    }

    private void loadSongs() {
        List<Song> songs = SongDAO.getAllSongs(null);
        DefaultTableModel model = (DefaultTableModel) songTable.getModel();
        model.setRowCount(0);
        for (Song s : songs) {
            model.addRow(new Object[]{s.getId(), s.getTitle(), s.getArtist(), s.getGenre(), s.getFilepath()});
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) userTable.getValueAt(row, 0);
        if (id == admin.getId()) {
            JOptionPane.showMessageDialog(this, "Cannot delete yourself!");
            return;
        }
        if (UserDAO.deleteUserById(id)) {
            JOptionPane.showMessageDialog(this, "User deleted.");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete.");
        }
    }

    private void deleteSelectedSong() {
        int row = songTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) songTable.getValueAt(row, 0);
        if (SongDAO.deleteSongById(id)) {
            JOptionPane.showMessageDialog(this, "Song deleted.");
            loadSongs();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete song.");
        }
    }

    private void uploadNewSong() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("WAV Audio Files", "wav"));
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            String title = JOptionPane.showInputDialog("Enter Song Title:");
            String artist = JOptionPane.showInputDialog("Enter Artist:");
            String genre = JOptionPane.showInputDialog("Enter Genre:");

            try {
                String destPath = "songs/" + file.getName();
                Files.createDirectories(Paths.get("songs"));
                Files.copy(file.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
                if (SongDAO.insertSong(title, artist, genre, destPath)) {
                    JOptionPane.showMessageDialog(this, "Song uploaded!");
                    loadSongs();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to upload.");
            }
        }
    }
}
