/**
 * Create a new playList
 */
(function() {
    const playlistSongSelectionContainer = document.getElementById("playlistSongSelectionContainer");
    const playlistSongSelectionMessage = document.getElementById("playlistSongSelectionMessage");

    function loadUserSongsForPlaylistCreation() {
        makeCall("GET", "../GetUserSongsServlet", null, function(xhr) {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                playlistSongSelectionMessage.style.display = "none"; // Hide loading message
                playlistSongSelectionContainer.innerHTML = ''; // Clear previous content

                if (xhr.status === 200) {
                    const songs = JSON.parse(xhr.responseText);
                    if (songs && songs.length > 0) {
                        songs.forEach(song => {
                            const label = document.createElement("label");
                            const checkbox = document.createElement("input");
                            checkbox.type = "checkbox";
                            checkbox.name = "selectedSongs";
                            checkbox.value = song.id;
                            label.appendChild(checkbox);
                            label.appendChild(document.createTextNode(" " + song.songTitle + " - " + song.author + " (" + song.publicationYear + ")"));
                            playlistSongSelectionContainer.appendChild(label);
                            playlistSongSelectionContainer.appendChild(document.createElement("br"));
                        });
                    } else {
                        playlistSongSelectionContainer.textContent = "You have no songs to add.";
                    }
                } else if (xhr.status === 401) { // Unauthorized
                    sessionStorage.removeItem("userName");
                    window.location.href = "../HTML/Login.html";
                }
                else {
                    playlistSongSelectionContainer.textContent = "Error loading songs.";
                    console.error("Error loading songs: " + xhr.responseText);
                }
            }
        })
    }

    if (document.getElementById("homePage").classList.contains("active-section")) {
        loadUserSongsForPlaylistCreation();
    }

    window.loadUserSongsForPlaylistCreation = loadUserSongsForPlaylistCreation;

    document.getElementById("createPlaylistButton").addEventListener("click" , (e) => {

        console.log("Creating a new playList!");

        let form = e.target.closest("form");

        let formData = new FormData(form);

        if(form.checkValidity()){
            let title = document.getElementById("name").value;
            let tableBody = document.getElementById("playlistTableBody");
            let targetTitles = tableBody.getElementsByTagName("tr");
            let targetTitle;

            for(let i = 0 ; i < targetTitles.length ; i++){
                targetTitle = targetTitles[i].getElementsByTagName("a")[0].innerHTML;
                if(title === targetTitle){
                    document.getElementById("createPlaylistError").textContent = "PlayList name already used - client";
                    return;
                }
            }

            makeCall("POST" , "../CreatePlaylist" , formData ,
                function (x) {

                    if(x.readyState == XMLHttpRequest.DONE){
                        pageOrchestrator.resetErrors();

                        switch (x.status){
                            case 200:
                                playlistList.show();
                                loadUserSongsForPlaylistCreation();
                                break;

                            case 403:
                                sessionStorage.removeItem("userName");
                                window.location.href = "../HTML/Login.html";
                                break;

                            case 401:
                                sessionStorage.removeItem("userName");
                                window.location.href = "../HTML/Login.html";
                                break;

                            default:
                                document.getElementById("createPlaylistError").textContent = x.responseText;
                                break;
                        }
                    }
                }
            );
            form.reset();
            loadUserSongsForPlaylistCreation();
        }else{
            form.reportValidity();
        }
    });
})();