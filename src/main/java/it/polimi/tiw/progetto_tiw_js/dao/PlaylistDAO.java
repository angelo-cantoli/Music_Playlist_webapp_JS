package it.polimi.tiw.progetto_tiw_js.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progetto_tiw_js.beans.Playlist;
import it.polimi.tiw.progetto_tiw_js.beans.Song;
import it.polimi.tiw.progetto_tiw_js.utils.Deserializer;

public class PlaylistDAO {
    private Connection connection;

    public PlaylistDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Method that create a list of playList of the user
     * @param userId is the id of the user
     * @return an ArrayList of playList created by the user
     * @throws SQLException
     */
    public ArrayList<Playlist> findPlaylist(int userId) throws SQLException {
        String query = "SELECT * FROM playlist WHERE creatorId = ? ORDER BY creationDate DESC";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, userId);

            resultSet = pStatement.executeQuery();

            while (resultSet.next()) {
                Playlist playlist = new Playlist();
                playlist.setTitle(resultSet.getString("title"));
                playlist.setId(resultSet.getInt("id"));
                playlist.setCreationDate(resultSet.getDate("creationDate"));
                playlist.setSorting(resultSet.getString("sorting"));
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return playlists;
    }

    public boolean findPlaylistByTitle(String title, int userId) throws SQLException {
        String query = "SELECT * FROM playlist WHERE title = ? AND creatorId = ?";
        boolean result = false;
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, title);
            pStatement.setInt(2, userId);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) result = true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }

    /**
     * Method that create a new playList with an unique title
     * @param title
     * @param creationDate
     * @param userId
     * @return the ID of the newly created playlist, or -1 if creation failed
     * @throws SQLException
     */
    public int createPlaylist(String title, Date creationDate, int userId) throws SQLException {
        String query = "INSERT INTO playlist (creatorId, title, creationDate) VALUES (?, ?, ?)";
        int playlistId = -1;
        PreparedStatement pStatement = null;
        ResultSet generatedKeys = null;

        if (findPlaylistByTitle(title, userId)) {
            return -1;
        }

        try {
            pStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setInt(1, userId);
            pStatement.setString(2, title);
            pStatement.setDate(3, creationDate);
            int affectedRows = pStatement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = pStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    playlistId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {

            throw new SQLException("Error creating playlist", e);
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
            } catch (Exception e1) {
                 // Log or handle
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                 // Log or handle
            }
        }
        return playlistId;
    }

    /**
     * Verifica se una playlist con l'ID specificato appartiene all'utente
     * @param playlistId ID della playlist da cercare
     * @param userId ID dell'utente proprietario
     * @return true se la playlist esiste e appartiene all'utente, false altrimenti
     * @throws SQLException se si verifica un errore di database
     */
    public boolean findPlayListById(int playlistId, int userId) throws SQLException {
        String query = "SELECT * FROM playlist WHERE id = ? AND creatorId = ?";
        boolean result = false;
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, playlistId);
            pStatement.setInt(2, userId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }

    /**
     * Method that find the title of a playList by its id
     * @param playlistId is the unique id of the playList
     * @return a String containing the title
     * @throws SQLException
     */
    public String findPlayListTitleById(int playlistId) throws SQLException {
        String query = "SELECT * FROM playlist WHERE id = ?";
        String result = "";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, playlistId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }

    /**
     * Verifica se un brano è già presente in una playlist
     * @param playlistId ID della playlist
     * @param songId ID del brano
     * @return true se il brano è già nella playlist, false altrimenti
     * @throws SQLException se si verifica un errore di database
     */
    public boolean findSongInPlaylist(int playlistId, int songId) throws SQLException {
        String query = "SELECT * FROM contains WHERE pId = ? AND sId = ?";
        boolean result = false;

        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, playlistId);
            pStatement.setInt(2, songId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next())
                result = true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }

    /**
     * Aggiunge un brano a una playlist
     * @param playlistId ID della playlist
     * @param songId ID del brano da aggiungere
     * @return true se l'operazione ha successo, false altrimenti
     * @throws SQLException se si verifica un errore di database
     */
    public boolean addSong(int playlistId, int songId) throws SQLException {
        String query = "INSERT INTO contains (pId, sId) VALUES (?, ?)";
        int code = 0;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, playlistId);
            pStatement.setInt(2, songId);

            code = pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return (code > 0);
    }

    /**
     * Aggiunge o aggiorna l'ordinamento personalizzato di una playlist
     * @param playlistId ID della playlist
     * @param sorting Stringa JSON con l'ordinamento dei brani
     * @return true se l'operazione ha successo, false altrimenti
     * @throws SQLException se si verifica un errore di database
     */
    public boolean addSorting(int playlistId, String sorting) throws SQLException {
        String query = "UPDATE playlist SET sorting = ? WHERE id = ?";
        int code = 0;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, sorting);
            pStatement.setInt(2, playlistId);
            code = pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }

        return (code > 0);
    }


    public List<Playlist> getPlaylistsByUser(int userId) throws SQLException {
        String query = "SELECT * FROM playlist WHERE creatorId = ? ORDER BY creationDate DESC";
        List<Playlist> playlists = new ArrayList<>();
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, userId);
            try (ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    Playlist playlist = new Playlist();
                    playlist.setTitle(resultSet.getString("title"));
                    playlist.setId(resultSet.getInt("id"));
                    playlist.setCreationDate(resultSet.getDate("creationDate"));
                    playlists.add(playlist);
                }
            }
        }
        return playlists;
    }


    public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
        String query = "SELECT s.* FROM song s JOIN contains c ON s.id = c.sId WHERE c.pId = ? ORDER BY s.author ASC, s.albumDate ASC";
        List<Song> songs = new ArrayList<>();
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playlistId);
            try (ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    Song song = new Song();
                    song.setId(resultSet.getInt("id"));
                    song.setSongTitle(resultSet.getString("title"));
                    song.setAuthor(resultSet.getString("author"));
                    song.setPublicationYear(resultSet.getInt("publicationYear"));
                    songs.add(song);
                }
            }
        }
        return songs;
    }


    public boolean hasCustomOrder(int playlistId) throws SQLException {
        String query = "SELECT customOrder FROM playlist WHERE id = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playlistId);
            try (ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("customOrder");
                }
                return false;
            }
        }
    }

    public boolean addSongToCustomOrder(int playlistId, int songId) throws SQLException {
        try {
            connection.setAutoCommit(false);
            if (!addSong(playlistId, songId)) {
                connection.rollback();
                return false;
            }

            String query = "SELECT sorting FROM playlist WHERE id = ?";
            String currentSorting = null;
            String newSorting;
            try (PreparedStatement pStatement = connection.prepareStatement(query)) {
                pStatement.setInt(1, playlistId);
                try (ResultSet resultSet = pStatement.executeQuery()) {
                    if (resultSet.next()) {
                        currentSorting = resultSet.getString("sorting");
                    }
                }
            }
            if (currentSorting == null || currentSorting.isEmpty() || currentSorting.equals("[]")) {
                newSorting = "[" + songId + "]";
            } else {
                newSorting = currentSorting.substring(0, currentSorting.length() - 1) +
                        "," + songId + "]";
            }
            if (!addSorting(playlistId, newSorting)) {
                connection.rollback(); // Se fallisce, annulla
                return false;
            }

            // 2. Se tutto ha successo, conferma la transazione
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }


    public boolean setCustomOrder(int playlistId, boolean customOrder) throws SQLException {
        String query = "UPDATE playlist SET customOrder = ? WHERE id = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setBoolean(1, customOrder);
            pStatement.setInt(2, playlistId);
            int result = pStatement.executeUpdate();
            return result > 0;
        }
    }


    public ArrayList<Integer> getSorting(int pId) throws SQLException {
        String query = "SELECT sorting FROM playlist WHERE id = ?";
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;
        String jSon = null;

        ArrayList<Integer> sortedArray = new ArrayList<Integer>();

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, pId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next())
                jSon = resultSet.getString("sorting");

            if (jSon == null)
                return null;

            sortedArray = Deserializer.fromJsonToArrayList(jSon);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }

        return sortedArray;
    }
}
