(function() {
    document.getElementById("confirmSortingButton").addEventListener("click" , (e) => {

        let rows = Array.from(document.getElementById("sortPlayListTable").querySelectorAll('tbody > tr'));

        let sortingToSend = new Array();

        for(let i = 0 ; i < rows.length ; i++){

            let songId = rows[i].dataset.songId;
            if (songId) {
                sortingToSend.push(songId);
            } else {
                console.warn("Found a row without a songId:", rows[i]);
            }
        }

        if (sortingToSend.length === 0) {
            document.getElementById("sortingError").textContent = "No songs to order or song IDs could not be retrieved.";
            return;
        }

        makeCall("POST" , "../AddSorting?playlistId=" + playListSongsToOrder.currentPlaylistId, null ,
            function(request) {

                if(request.readyState == XMLHttpRequest.DONE){
                    pageOrchestrator.resetErrors();
                    switch(request.status){
                        case 200:

                            if (songsInPlayList && playListSongsToOrder.currentPlaylistId) {
                                songsInPlayList.show(playListSongsToOrder.currentPlaylistId);
                            }
                            pageOrchestrator.showHomePageView();
                            break;
                        case 403:
                            sessionStorage.removeItem("userName");
                            window.location.href = "../HTML/Login.html";
                            break;
                        default:
                            document.getElementById("sortingError").textContent = request.responseText;
                            break;
                    }
                }
            } , sortingToSend
        );
    });
})();