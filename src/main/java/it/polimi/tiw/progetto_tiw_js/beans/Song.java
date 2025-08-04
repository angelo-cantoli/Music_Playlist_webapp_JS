package it.polimi.tiw.progetto_tiw_js.beans;

public class Song{
    private int id;
    private String songTitle;
    private String author;
    private String userId;
    private String kindOf;
    private String songFile;
    private String imgFile;
    private int albumId;
    private String albumTitle;
    private int publicationYear;


    /**
     * @return the id of the song
     */
    public int getId() {
        return id;
    }

    /**
     * @return the song title
     */
    public String getSongTitle() {
        return songTitle;
    }

    /**
     * @return the title of the album that contains the song
     */
    public int getAlbumId() {
        return albumId;
    }

    /**
     * @return the author of the song
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the kind of song
     */
    public String getKindOf() {
        return kindOf;
    }

    /**
     * @return the name of the music file
     */
    public String getSongFile() {
        return songFile;
    }

    /**
     * @return the name of the image file
     */
    public String getImgFile() {
        return imgFile;
    }

    /**
     * @return the publication year of the album that contains the song
     */
    public int getPublicationYear() {
        return publicationYear;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public int getUserId() {
        return Integer.parseInt(userId);
    }

    /**
     * Set the song id
     * @param id is the unique id of the song
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set the SongTitle
     * @param songTitle is the title of the song
     */
    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

 /**
     * Set the title of the album that contains the song
     * @param albumId is the album id
     */
    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    /**
     * Set the name of singer
     * @param userId is the id of of the user that created the song
     */

    public void setUserId(String userId) {
        this.userId = userId;
    }


    /**
     * Set the name of author
     * @param author is the name of the author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Set the kind of the song
     * @param kindOf is the new type
     */
    public void setKindOf(String kindOf) {
        this.kindOf = kindOf;
    }

    /**
     * Set the name of the song file
     * @param songFile is the name of the file stored
     */
    public void setSongFile(String songFile) {
        this.songFile = songFile;
    }

    /**
     * Set the name of the image
     * @param imgFile is the name of the song is stored
     */
    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }

    /**
     * Set the publication year
     * @param date is the year
     */
    public void setPublicationYear(int date) {
        this.publicationYear = date;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }
}
