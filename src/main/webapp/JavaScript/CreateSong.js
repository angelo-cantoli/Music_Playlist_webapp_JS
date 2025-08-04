/**
 * Create a new song in the database
 */
(function () {
    document.getElementById("createSongButton").addEventListener("click" , (e) => {
        console.log("Creating a new song");

        let form = e.target.closest("form");
        pageOrchestrator.resetErrors();

        if(form.checkValidity()){
            let title = document.getElementById("title").value;
            let genre = document.getElementById("genre").value;
            let albumTitle = document.getElementById("albumTitle").value;
            let singer = document.getElementById("artist").value;
            let publicationYear = document.getElementById("publicationYear").value;

            if(isNaN(publicationYear)){
                document.getElementById("songError").textContent = "Publication year is not a number";
                return;
            }
            if(publicationYear > (new Date().getFullYear())){
                document.getElementById("songError").textContent = "Publication year not valid";
                return;
            }

            if(!(genre === "Dance" || genre === "Pop" || genre ==="Rap" || genre === "Rock" || genre === "Classical" || genre === "Jazz" || genre === "Blues")){
                document.getElementById("songError").textContent = "Invalid genre";
                return;
            }

            if(title.length > 45 || albumTitle.length > 45 || singer.length > 45 || genre.length > 45){
                document.getElementById("songError").textContent = "Some values are too long";
                return;
            }

            makeCall("POST" , "../CreateSong" , form ,
                function (x) {

                    if(x.readyState === XMLHttpRequest.DONE){
                        switch(x.status){
                            case 200:

                                if (songsNotInPlayList && songsNotInPlayList.playlistId) { // Check if a playlist is selected for this component
                                    songsNotInPlayList.show(songsNotInPlayList.playlistId);
                                }

                                if (typeof window.loadUserSongsForPlaylistCreation === 'function') {
                                    window.loadUserSongsForPlaylistCreation();
                                }
                                form.reset();
                                break;
                            case 403:
                                sessionStorage.removeItem("userName");
                                window.location.href = "../HTML/Login.html";
                                break;

                            default:
                                document.getElementById("songError").textContent = x.responseText;
                                break;
                        }
                    }
                }
            );

        }else{
            form.reportValidity();
        }
    });
})();