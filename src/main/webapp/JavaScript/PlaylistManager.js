{

    var playlistList;
    var songsInPlayList;
    var songsNotInPlayList;
    var songDetails;
    var sortingList;
    var playListSongsToOrder;
    var handleButtons;
    let personalMessage;
    let playListMessage;
    var pageOrchestrator = new PageOrchestrator();


    function PersonalMessage(usernameJson, element) {
        this.usernameJson = usernameJson;
        this.element = element;

        this.show = function() {
            if (this.element) {
                let displayUser = "Guest";
                if (this.usernameJson) {
                    try {
                        const userObj = JSON.parse(this.usernameJson);
                        if (userObj && userObj.username) {
                            displayUser = userObj.username;
                        } else if (typeof this.usernameJson === 'string' && !this.usernameJson.startsWith('{')) {
                            displayUser = this.usernameJson;
                        }
                    } catch (e) {
                        console.warn("Failed to parse username JSON from sessionStorage, displaying as is or Guest.", e);
                        if (typeof this.usernameJson === 'string' && !this.usernameJson.startsWith('{')) {
                           displayUser = this.usernameJson;
                        }
                    }
                }
                this.element.textContent = displayUser;
            }
        };
    }

    function PlaylistList(errorContainer, tableBody, messageContainer) {
        this.errorContainer = errorContainer;
        this.tableBody = tableBody;
        this.messageContainer = messageContainer;

        this.show = function() {
            if (this.messageContainer) this.messageContainer.textContent = "Loading playlists...";
            if (this.errorContainer) this.errorContainer.textContent = "";
            makeCall("GET", "../GetPlaylistList", null, (req) => {
                if (req.readyState === XMLHttpRequest.DONE) {
                    if (this.messageContainer) this.messageContainer.textContent = "";

                    switch (req.status) {
                        case 200: // OK
                            const playlists = JSON.parse(req.responseText);
                            this.tableBody.innerHTML = ""; // Clear previous entries
                            if (playlists && playlists.length > 0) {
                                playlists.forEach(playlist => {
                                    const row = this.tableBody.insertRow();
                                    const titleCell = row.insertCell();
                                    const dateCell = row.insertCell();
                                    const titleLink = document.createElement("a");
                                    titleLink.href = "#";
                                    titleLink.textContent = playlist.title;
                                    titleLink.addEventListener("click", (e) => {
                                        e.preventDefault();
                                        pageOrchestrator.showPlaylistView(playlist.id, playlist.title);
                                    });
                                    titleCell.appendChild(titleLink);
                                    dateCell.textContent = playlist.creationDate;
                                });
                            } else {
                                if (this.messageContainer) this.messageContainer.textContent = "No playlists found. Create one!";
                            }
                            break;

                        case 401:
                        case 403:
                            sessionStorage.removeItem("userName");
                            window.location.href = "../HTML/Login.html";
                            break;

                        default:
                            if (this.errorContainer) {
                                this.errorContainer.textContent = "An unexpected error occurred. Please try again. (Status: " + req.status + ")";
                            }
                            break;
                    }
                }
            });
        };

        this.reset = function() {
            if (this.tableBody) this.tableBody.innerHTML = "";
            if (this.errorContainer) this.errorContainer.textContent = "";
            if (this.messageContainer) this.messageContainer.textContent = "";
        };
    }

    function SongsInPlaylist(errorContainer, songListContainer, messageContainer, nextButtonEl, prevButtonEl) {
        this.errorContainer = errorContainer;
        this.songListContainer = songListContainer;
        this.messageContainer = messageContainer;
        this.nextButton = nextButtonEl;
        this.prevButton = prevButtonEl;
        this.currentPlaylistId = null;
        this.allSongs = [];
        this.currentPage = 0;
        this.songsPerPage = 5;

        this.updatePaginationButtons = function() {
            const totalPages = Math.ceil(this.allSongs.length / this.songsPerPage);

            if (this.nextButton) {
                this.nextButton.style.display = (this.currentPage < totalPages - 1) ? "inline-block" : "none";
            }
            if (this.prevButton) {
                this.prevButton.style.display = (this.currentPage > 0) ? "inline-block" : "none";
            }
            if (totalPages <= 1) {
                if (this.nextButton) this.nextButton.style.display = "none";
                if (this.prevButton) this.prevButton.style.display = "none";
            }
        };
        
        if (this.nextButton) {
            this.nextButton.addEventListener('click', () => {
                const totalPages = Math.ceil(this.allSongs.length / this.songsPerPage);
                if (this.currentPage < totalPages - 1) {
                    this.currentPage++;
                    this.renderCurrentPageSongs();
                }
            });
        }

        if (this.prevButton) {
            this.prevButton.addEventListener('click', () => {
                if (this.currentPage > 0) {
                    this.currentPage--;
                    this.renderCurrentPageSongs();
                }
            });
        }

        this.renderCurrentPageSongs = function() {
            this.songListContainer.innerHTML = ""; // Clear previous items
            if (this.allSongs.length === 0) {
                if (this.messageContainer) this.messageContainer.textContent = "This playlist is empty.";
                this.updatePaginationButtons();
                return;
            }

            const start = this.currentPage * this.songsPerPage;
            const end = start + this.songsPerPage;
            const songsToDisplay = this.allSongs.slice(start, end);

            songsToDisplay.forEach(song => {
                const songItemDiv = document.createElement("div");
                songItemDiv.className = "song-item";
                songItemDiv.addEventListener("click", () => {
                    pageOrchestrator.showPlayerView(this.currentPlaylistId, song.songId);
                });

                const img = document.createElement("img");

                img.src = (song.base64String && song.base64String.startsWith("data:image")) ? song.base64String : "../Images/defaultAlbum.png";
                img.alt = song.songTitle;

                const titleSpan = document.createElement("span");
                titleSpan.className = "song-title";
                titleSpan.textContent = song.songTitle;

                songItemDiv.appendChild(img);
                songItemDiv.appendChild(titleSpan);
                this.songListContainer.appendChild(songItemDiv);
            });
            this.updatePaginationButtons();
            if (this.messageContainer) this.messageContainer.textContent = ""; // Clear loading/empty message
        };


        this.show = function(playlistId) {
            this.currentPlaylistId = playlistId;
            this.currentPage = 0; // Reset to first page
            if (this.messageContainer) this.messageContainer.textContent = "Loading songs...";
            if (this.errorContainer) this.errorContainer.textContent = "";
            this.songListContainer.innerHTML = ""; // Clear before loading

            makeCall("GET", `../GetSongsInPlaylist?playlistId=${playlistId}`, null, (req) => {
                if (req.readyState === XMLHttpRequest.DONE) {
                    if (this.messageContainer) this.messageContainer.textContent = "";
                    if (req.status === 200) {
                        this.allSongs = JSON.parse(req.responseText);
                        this.renderCurrentPageSongs();
                    } else if (req.status === 401 || req.status === 403) {
                        sessionStorage.removeItem("userName");
                        window.location.href = "../HTML/Login.html";
                    } else {
                        if (this.errorContainer) this.errorContainer.textContent = "Error loading songs: " + req.responseText;
                        this.allSongs = [];
                        this.renderCurrentPageSongs();
                    }
                }
            });
        };

        this.reset = function() {
            this.songListContainer.innerHTML = "";
            if (this.errorContainer) this.errorContainer.textContent = "";
            if (this.messageContainer) this.messageContainer.textContent = "";
            this.allSongs = [];
            this.currentPage = 0;
            this.updatePaginationButtons();
        };
    }

    function SongsNotInPlaylist(alertContainer, containerElement) {
        this.alertContainer = alertContainer;
        this.containerElement = containerElement;
        this.playlistId = null;
        this.addButtonElement = document.getElementById("addSongButton");

        if (this.addButtonElement) {
            this.addButtonElement.addEventListener("click", (e) => {
                let form = e.target.closest("form");
                if (this.alertContainer) this.alertContainer.textContent = "";

                if (!this.playlistId) {
                    if (this.alertContainer) this.alertContainer.textContent = "No playlist selected to add song to.";
                    return;
                }

                // Get all selected checkboxes
                const selectedCheckboxes = this.containerElement.querySelectorAll('input[type="checkbox"]:checked');
                if (selectedCheckboxes.length === 0) {
                    if (this.alertContainer) this.alertContainer.textContent = "Please select at least one song to add.";
                    return;
                }

                let formData = new FormData();
                // Add each selected song ID
                selectedCheckboxes.forEach(checkbox => {
                    formData.append("selectedSongs", checkbox.value);
                });

                makeCall("POST", `../AddSong?playlistId=${this.playlistId}`, formData,
                    (request) => {
                        if (request.readyState == XMLHttpRequest.DONE) {
                            pageOrchestrator.resetErrors();
                            if (this.alertContainer) this.alertContainer.textContent = "";

                            switch (request.status) {
                                case 200:
                                    if (songsInPlayList && songsInPlayList.show) {
                                        songsInPlayList.show(this.playlistId);
                                    }
                                    this.show(this.playlistId);
                                    break;
                                case 401:
                                case 403:
                                    sessionStorage.removeItem("userName");
                                    window.location.href = "../HTML/Login.html";
                                    break;
                                default:
                                    if (this.alertContainer) {
                                        this.alertContainer.textContent = request.responseText;
                                    }
                                    break;
                            }
                        }
                    },
                    null,
                    false
                );

            });
        } else {
            console.error("Element with ID 'addSongButton' not found for SongsNotInPlaylist.");
        }

        this.show = function(playlistId) {
            if (!playlistId) {
                if (this.alertContainer) this.alertContainer.textContent = "Invalid playlist ID for showing songs not in playlist.";
                this.reset();
                return;
            }
            this.playlistId = playlistId;
            this.reset();

            makeCall("GET", `../GetSongsNotInPlaylist?playlistId=${playlistId}`, null,
                (request) => {
                    if (request.readyState == XMLHttpRequest.DONE) {
                        if (this.alertContainer) this.alertContainer.textContent = "";

                        switch (request.status) {
                            case 200:
                                let songs = JSON.parse(request.responseText);
                                if (songs.length === 0) {
                                    if (this.alertContainer) {
                                        this.alertContainer.textContent = "All songs already in this playlist or no other songs available.";
                                    }
                                    if (this.addButtonElement) this.addButtonElement.disabled = true;
                                } else {
                                    songs.forEach(song => {
                                        let songDiv = document.createElement("div");
                                        songDiv.style.display = "block";
                                        songDiv.style.width = "100%";
                                        
                                        let label = document.createElement("label");
                                        label.style.display = "flex";
                                        label.style.alignItems = "center";
                                        label.style.gap = "8px";
                                        label.style.width = "100%";
                                        
                                        let checkbox = document.createElement("input");
                                        checkbox.type = "checkbox";
                                        checkbox.value = song.id;
                                        checkbox.name = "selectedSongs";
                                        
                                        let span = document.createElement("span");
                                        span.textContent = `${song.songTitle} - ${song.author || 'Unknown Artist'} (${song.publicationYear || '0'})`;
                                        
                                        label.appendChild(checkbox);
                                        label.appendChild(span);
                                        songDiv.appendChild(label);
                                        
                                        if (this.containerElement) this.containerElement.appendChild(songDiv);
                                    });
                                    if (this.addButtonElement) this.addButtonElement.disabled = false;
                                }
                                break;
                            case 401:
                            case 403:
                                sessionStorage.removeItem("userName");
                                window.location.href = "../HTML/Login.html";
                                break;
                            default:
                                if (this.alertContainer) this.alertContainer.textContent = request.responseText;
                                if (this.addButtonElement) this.addButtonElement.disabled = true;
                                if (this.selectElement) this.selectElement.disabled = true;
                                break;
                        }
                    }
                }
            );
        };

        this.reset = function() {
            if (this.containerElement) {
                while (this.containerElement.firstChild) {
                    this.containerElement.removeChild(this.containerElement.firstChild);
                }
            }
            if (this.addButtonElement) this.addButtonElement.disabled = true;
            if (this.alertContainer) {
                this.alertContainer.textContent = "";
            }
        };
    }

    function SongDetails(errorContainer, detailsContainerElementId) {
        this.errorContainer = errorContainer;
        this.detailsContainer = document.getElementById(detailsContainerElementId);
        this.playlistPageElement = document.getElementById("playlistPage");
        this.currentPlaylistId = null;
        this.currentSongId = null;


        this.show = function(playlistId, songId) {
            this.currentPlaylistId = playlistId;
            this.currentSongId = songId;
            if (this.errorContainer) this.errorContainer.textContent = "";
            if (!this.detailsContainer || !this.playlistPageElement) {
                console.error("SongDetails: Critical elements (detailsContainer or playlistPageElement) not found!");
                if(this.errorContainer) this.errorContainer.textContent = "UI error: Cannot display song details.";
                return;
            }
            this.detailsContainer.innerHTML = "Loading details...";
            this.playlistPageElement.classList.add("details-active");


            makeCall("GET", `../GetSongDetails?songId=${songId}&playlistId=${playlistId}`, null, (req) => {
                if (req.readyState === XMLHttpRequest.DONE) {
                    if (!this.detailsContainer) return;
                    
                    this.detailsContainer.style.display = "block";

                    if (req.status === 200) {
                        const song = JSON.parse(req.responseText);

                        let detailsHtml = '<div class="song-details-container">';

                        detailsHtml += '<div class="song-media-column">';
                        if (song.imageBase64String && (song.imageBase64String.startsWith("data:image") || song.imageBase64String.length > 100)) {
                            detailsHtml += `<img src="${song.imageBase64String}" alt="Album Art" class="song-details-album-art">`;
                        } else {
                            detailsHtml += '<div class="song-details-album-art no-image">No Album Art Available</div>';
                        }

                        if (song.base64String && song.base64String.startsWith("data:audio")) {
                            detailsHtml += `<audio controls class="song-details-audio-player"><source src="${song.base64String}" type="audio/mpeg">Your browser does not support the audio element.</audio>`;
                        } else if (song.songFile) {
                            detailsHtml += `<audio controls class="song-details-audio-player"><source src="../GetSong/${encodeURIComponent(song.songFile)}" type="audio/mpeg">Your browser does not support the audio element.</audio>`;
                        } else {
                            detailsHtml += '<p class="detail-message">Audio player not available.</p>';
                        }
                        detailsHtml += '</div>';

                        // Metadata Column: Song Info
                        detailsHtml += '<div class="song-metadata-column">';
                        detailsHtml += '<h3>Song Details</h3>';
                        detailsHtml += '<ul class="song-details-list">';
                        detailsHtml += `<li><strong>Artist:</strong> <span>${song.author || "N/A"}</span></li>`;
                        detailsHtml += `<li><strong>Album:</strong> <span>${song.albumTitle || "N/A"}</span></li>`;
                        detailsHtml += `<li><strong>Year:</strong> <span>${song.publicationYear || "N/A"}</span></li>`;
                        detailsHtml += `<li><strong>Genre:</strong> <span>${song.genre || "N/A"}</span></li>`;
                        detailsHtml += '</ul>';
                        detailsHtml += '</div>';

                        detailsHtml += '</div>';
                        
                        this.detailsContainer.innerHTML = detailsHtml;

                    } else if (req.status === 401 || req.status === 403) {
                        sessionStorage.removeItem("userName");
                        window.location.href = "../HTML/Login.html";
                    } else {
                        if (this.errorContainer) this.errorContainer.textContent = "Error loading song details: " + req.responseText;
                        if (this.detailsContainer) this.detailsContainer.innerHTML = `<p class="error detail-message">Error loading song details.</p>`;
                    }
                }
            });
        };

        this.reset = function() {
            if (this.detailsContainer) {
                this.detailsContainer.innerHTML = "";
                this.detailsContainer.style.display = "none";
            }
            if (this.playlistPageElement) {
                this.playlistPageElement.classList.remove("details-active");
            }

            if (this.errorContainer) this.errorContainer.textContent = "";
            this.currentSongId = null;
        };
    }

    function SortingList(errorContainer, tableBodyElement, messageContainer) {
        this.errorContainer = errorContainer;
        this.tableBody = tableBodyElement;
        this.messageContainer = messageContainer;

        this.show = function(playlistId) {
            if (this.errorContainer) this.errorContainer.textContent = "";
            if (this.messageContainer) this.messageContainer.textContent = "Loading songs for sorting...";
            this.tableBody.innerHTML = "";

            makeCall("GET", `../GetSongsInPlaylist?playlistId=${playlistId}`, null, (req) => {
                if (req.readyState === XMLHttpRequest.DONE) {
                    if (this.messageContainer) this.messageContainer.textContent = "";
                    if (req.status === 200) {
                        const songs = JSON.parse(req.responseText);
                        if (songs && songs.length > 0) {
                            songs.forEach(song => {
                                const row = this.tableBody.insertRow();
                                row.classList.add("draggable"); // Add draggable class
                                row.draggable = true;           // Ensure draggable attribute is true
                                row.dataset.songId = song.songId; // Store song ID
                                const cell = row.insertCell();
                                cell.textContent = song.songTitle;

                            });

                            if (typeof window.initializeDragAndDrop === "function") {

                                if (!this.tableBody.id) {
                                    this.tableBody.id = "sortableTbody_" + new Date().getTime(); // Generate a unique ID
                                }
                                window.initializeDragAndDrop(this.tableBody.id, this.errorContainer ? this.errorContainer.id : null);
                            }
                        } else {
                            if (this.messageContainer) this.messageContainer.textContent = "No songs in this playlist to sort.";
                            else if(this.errorContainer) this.errorContainer.textContent = "No songs in this playlist to sort.";
                        }
                    } else {
                        if (this.errorContainer) this.errorContainer.textContent = "Error loading songs for sorting: " + req.responseText;
                    }
                }
            });
        };

        this.reset = function() {
            if (this.tableBody) this.tableBody.innerHTML = "";
            if (this.errorContainer) this.errorContainer.textContent = "";
            if (this.messageContainer) this.messageContainer.textContent = "";
        };

        this.getOrderedSongIds = function() {
            const songIds = [];
            if (this.tableBody) {
                const rows = this.tableBody.querySelectorAll("tr[data-song-id]");
                rows.forEach(row => {
                    songIds.push(row.dataset.songId);
                });
            }
            return songIds;
        };
    }


    function PlayListSongsToOrder(playlistNameElement, sortPlayListBody, confirmSortingButton, goToMainPageButton, sortingError) {
        this.playlistNameElement = playlistNameElement;
        this.sortPlayListBody = sortPlayListBody;
        this.goToMainPageButton = goToMainPageButton;
        this.sortingError = sortingError;
        this.currentPlaylistId = null;
        this.currentPlaylistName = "";


        this.setup = function(playlistId, playlistName) {
            this.currentPlaylistId = playlistId;
            this.currentPlaylistName = playlistName;
            if (this.playlistNameElement) {
                this.playlistNameElement.textContent = "Order songs for: " + playlistName;
            }
            if (this.sortingError) this.sortingError.textContent = "";


            if (sortingList) {
                sortingList.show(playlistId);
            }


            if (this.goToMainPageButton && !this.goToMainPageButton.hasAttribute('data-listener-attached')) {
                this.goToMainPageButton.addEventListener('click', () => {
                    pageOrchestrator.showHomePageView();
                });
                this.goToMainPageButton.setAttribute('data-listener-attached', 'true');
            }
        };

        this.reset = function() {
            if (this.playlistNameElement) this.playlistNameElement.textContent = "";

            if (sortingList) sortingList.reset();
            if (this.sortingError) this.sortingError.textContent = "";
            this.currentPlaylistId = null;
            this.currentPlaylistName = "";
        };
    }

    function HandleButtons(config) {
        this.buttons = {};
        for (const key in config) {
            if (config.hasOwnProperty(key) && config[key]) {
                this.buttons[key] = document.getElementById(config[key]);
            }
        }

        this.show = function(buttonKeysToShow = []) {
            for (const key in this.buttons) {
                if (this.buttons[key]) {
                    this.buttons[key].style.display = buttonKeysToShow.includes(key) ? "inline-block" : "none";
                }
            }
        };

        this.hideAll = function() {
            for (const key in this.buttons) {
                if (this.buttons[key]) {
                    this.buttons[key].style.display = "none";
                }
            }
        };

        this.attachListener = function(buttonKey, eventType, callback) {
            if (this.buttons[buttonKey] && !this.buttons[buttonKey].hasAttribute('data-listener-attached-' + eventType)) {
                this.buttons[buttonKey].addEventListener(eventType, callback);
                this.buttons[buttonKey].setAttribute('data-listener-attached-' + eventType, 'true');
            }
        };
    }

    function PlaylistMessage(playlistNameElement) {
        this.playlistNameElement = playlistNameElement;
        this.currentPlaylistName = "";

        this.setPlayListName = function(name) {
            this.currentPlaylistName = name;
            if (this.playlistNameElement) {
                this.playlistNameElement.textContent = name;
            }
        };

        this.reset = function() {
            this.currentPlaylistName = "";
            if (this.playlistNameElement) {
                this.playlistNameElement.textContent = "";
            }
        };
    }

    // --- PAGE ORCHESTRATOR ---
    function PageOrchestrator() {
        this.start = function() {
            personalMessage = new PersonalMessage(
                sessionStorage.getItem("userName"),
                document.getElementById("userName")
            );

            playlistList = new PlaylistList(
                document.getElementById("playlistTableError"),
                document.getElementById("playlistTableBody"),
                document.getElementById("playListTableMessage")
            );

            songsInPlayList = new SongsInPlaylist(
                document.getElementById("songTableError"),
                document.getElementById("songListContainer"),
                document.getElementById("songTableMessage"),
                document.getElementById("nextButton"),
                document.getElementById("beforeButton")
            );

            songsNotInPlayList = new SongsNotInPlaylist(
                document.getElementById("addSongMessage"),
                document.getElementById("songSelectionContainer")
            );

            songDetails = new SongDetails(
                document.getElementById("songDetailsError"),
                "songDetailsInlineContainer"
            );

            sortingList = new SortingList(
                document.getElementById("sortingError"),
                document.getElementById("sortPLayListBody"),
                null
            );

            playListSongsToOrder = new PlayListSongsToOrder(
                document.getElementById("playlistToOrder"),
                document.getElementById("sortPLayListBody"),
                document.getElementById("confirmSortingButton"),
                document.getElementById("goToMainPageButton"),
                document.getElementById("sortingError")
            );

            handleButtons = new HandleButtons({
                goToSortingPageButton: "goToSortingPageButton",
                confirmSortingButton: "confirmSortingButton",
                goToMainPageButton: "goToMainPageButton",
                goToHomeFromPlaylist: "goToHomeFromPlaylist",
                nextButton: "nextButton",
                prevButton: "beforeButton"
            });

            playListMessage = new PlaylistMessage(
                document.getElementById("playlistNameMessage")
            );



            if (personalMessage) personalMessage.show();


            if (handleButtons) {
                handleButtons.attachListener('goToSortingPageButton', 'click', () => {
                    if (songsInPlayList.currentPlaylistId && playListMessage.currentPlaylistName) {
                        pageOrchestrator.showSortPlaylistView(songsInPlayList.currentPlaylistId, playListMessage.currentPlaylistName);
                    } else {
                        console.error("Cannot go to sorting page: playlist ID or name missing.");
                    }
                });
                handleButtons.attachListener('goToMainPageButton', 'click', () => {
                    pageOrchestrator.showHomePageView();
                });
                handleButtons.attachListener('goToHomeFromPlaylist', 'click', () => {
                    pageOrchestrator.showHomePageView();
                });
            }

            window.onpopstate = this.handlePopState.bind(this);
            this.handleInitialLoad();
        };

        this.handleInitialLoad = function() {
            const hash = window.location.hash;
            const options = { isReplace: true }; 

            if (songDetails) songDetails.reset();


            if (hash.startsWith("#playlist/")) {
                const playlistId = parseInt(hash.substring("#playlist/".length), 10);
                this.showPlaylistView(playlistId, "Playlist", options);
            } else if (hash.startsWith("#player/")) {
                const parts = hash.substring("#player/".length).split('/');
                if (parts.length === 2) {

                    const playlistId = parseInt(parts[0], 10);
                    const songId = parseInt(parts[1], 10);

                    this.showPlaylistView(playlistId, "Playlist", { isReplace: true, skipSongDetailsReset: true });

                    this.showPlayerView(playlistId, songId, options);
                } else {
                    this.showHomePageView(options); 
                }
            } else if (hash.startsWith("#sort/")) {
                const playlistId = parseInt(hash.substring("#sort/".length), 10);
                this.showSortPlaylistView(playlistId, "Sort Playlist", options);
            } else { 
                this.showHomePageView(options);
            }
        };

        this.handlePopState = function(event) {
            const options = { isPop: true };
            if (songDetails && (!event.state || event.state.view !== 'player')) {
                songDetails.reset();
            }

            if (event.state) {
                const state = event.state;
                if (state.view === 'home') {
                    this.showHomePageView(options);
                } else if (state.view === 'playlist' && typeof state.playlistId !== 'undefined') {
                    this.showPlaylistView(state.playlistId, state.playlistTitle || "Playlist", options);
                } else if (state.view === 'player' && typeof state.playlistId !== 'undefined' && typeof state.songId !== 'undefined') {
                    this.showPlaylistView(state.playlistId, state.playlistTitle || "Playlist", {isPop: true, skipSongDetailsReset: true});
                    this.showPlayerView(state.playlistId, state.songId, options);
                } else if (state.view === 'sort' && typeof state.playlistId !== 'undefined') {
                    this.showSortPlaylistView(state.playlistId, state.playlistName || "Sort Playlist", options);
                } else {
                    this.showHomePageView(options); 
                }
            } else {
                this.handleInitialLoad(); 
            }
        };


        this.refresh = function() { 
            this.resetErrors();
            if (document.getElementById("homePage").classList.contains("active-section")) {
                if (playlistList) playlistList.show();

            } else if (document.getElementById("playlistPage").classList.contains("active-section")) {
                if (songsInPlayList && songsInPlayList.currentPlaylistId) {
                    songsInPlayList.show(songsInPlayList.currentPlaylistId);
                }
                if (songsNotInPlayList && songsNotInPlayList.playlistId) {
                    songsNotInPlayList.show(songsNotInPlayList.playlistId);
                }
            }
        };

        this.resetErrors = function() {
            const errorHoldersIds = [
                "playlistTableError", "playListTableMessage", "createPlaylistError", "playlistSongSelectionMessage",
                "songError",
                "songTableError", "songTableMessage", "addSongError", "addSongMessage",
                "songDetailsError",
                "sortingError"
            ];
            errorHoldersIds.forEach(id => {
                const elem = document.getElementById(id);
                if (elem) elem.textContent = "";
            });
        };

        this.showHomePageView = function(options = {}) {
            this.resetErrors();
            this.hideAllSections();
            if(songDetails) songDetails.reset();

            const userHeader = document.querySelector(".user-info-header");
            if (userHeader) userHeader.style.display = "flex";

            const homePage = document.getElementById("homePage");
            if (homePage) {
                homePage.classList.add("active-section");

                setTimeout(() => {
                    homePage.style.opacity = "1";
                }, 10);
            }

            if (playlistList) playlistList.show();

            if (handleButtons) handleButtons.hideAll();
            document.getElementById("addSongToPlaylistDiv").style.display = "none";

            if (!options.isPop) {
                const state = { view: 'home' };
                const title = "Home - Music Playlist Manager";
                const url = "#home";
                if (options.isReplace) {
                    history.replaceState(state, title, url);
                } else {
                    history.pushState(state, title, url);
                }
            }
        };


        this.hideAllSections = function() {
            const sections = ['homePage', 'playlistPage', 'sortPlayListPage'];
            sections.forEach(sectionId => {
                const section = document.getElementById(sectionId);
                if (section) {
                    section.classList.remove("active-section");
                    section.style.opacity = "0";
                }
            });
        };

        this.showPlaylistView = function(playlistId, playlistTitle, options = {}) {
            this.resetErrors();
            this.hideAllSections();
            if (!options.skipSongDetailsReset && songDetails) {
                songDetails.reset();
            } else if (options.skipSongDetailsReset && songDetails && songDetails.playlistPageElement && !songDetails.currentSongId) {
                songDetails.playlistPageElement.classList.remove("details-active");
                const detailsCol = document.getElementById("playlistSongDetailsColumn");
                if (detailsCol) detailsCol.style.display = "none";
            }

            const userHeader = document.querySelector(".user-info-header");
            if (userHeader) userHeader.style.display = "none";

            const playlistPage = document.getElementById("playlistPage");
            if (playlistPage) {
                playlistPage.classList.add("active-section");
                setTimeout(() => {
                    playlistPage.style.opacity = "1";
                }, 10);
            }

            const displayTitle = playlistTitle || "Playlist"; 
            if (playListMessage) playListMessage.setPlayListName(displayTitle);
            if (songsInPlayList) songsInPlayList.show(playlistId);
            if (songsNotInPlayList) songsNotInPlayList.show(playlistId);

            document.getElementById("addSongToPlaylistDiv").style.display = "block";
            if (handleButtons) handleButtons.show(['goToSortingPageButton', 'goToHomeFromPlaylist']); 

            if (!options.isPop) {
                const state = { view: 'playlist', playlistId: playlistId, playlistTitle: displayTitle };
                const title = `${displayTitle} - Music Playlist Manager`;
                const url = `#playlist/${playlistId}`;
                if (options.isReplace) {
                    history.replaceState(state, title, url);
                } else {
                    history.pushState(state, title, url);
                }
            }
        };

        this.showPlayerView = function(playlistId, songId, options = {}) {
            this.resetErrors();

            this.hideAllSections();
            const userHeader = document.querySelector(".user-info-header");
            if (userHeader) userHeader.style.display = "none";

            const playlistPage = document.getElementById("playlistPage");
            if (playlistPage) {
                playlistPage.classList.add("active-section");
                setTimeout(() => {
                    playlistPage.style.opacity = "1";
                }, 10);
            }

            if (songDetails) songDetails.show(playlistId, songId);
            if (handleButtons) handleButtons.show(['goToSortingPageButton', 'goToHomeFromPlaylist']);

            if (!options.isPop) {
                const state = { view: 'player', playlistId: playlistId, songId: songId, playlistTitle: playListMessage ? playListMessage.currentPlaylistName : "Playlist" };
                const title = `Playing Song - ${state.playlistTitle}`;
                const url = `#player/${playlistId}/${songId}`;
                if (options.isReplace) {
                    history.replaceState(state, title, url);
                } else {
                    history.pushState(state, title, url);
                }
            }
        };

        this.showSortPlaylistView = function(playlistId, playlistName, options = {}) {
            this.resetErrors();
            this.hideAllSections();
            if(songDetails) songDetails.reset();

            const userHeader = document.querySelector(".user-info-header");
            if (userHeader) userHeader.style.display = "none";

            const sortPage = document.getElementById("sortPlayListPage");
            if (sortPage) {
                sortPage.classList.add("active-section");
                setTimeout(() => {
                    sortPage.style.opacity = "1";
                }, 10);
            }

            const displayName = playlistName || "Sort Playlist";
            if (playListSongsToOrder) playListSongsToOrder.setup(playlistId, displayName);
            if (handleButtons) handleButtons.show(['confirmSortingButton', 'goToMainPageButton']);

            if (!options.isPop) {
                const state = { view: 'sort', playlistId: playlistId, playlistName: displayName };
                const title = `Sort: ${displayName} - Music Playlist Manager`;
                const url = `#sort/${playlistId}`;
                if (options.isReplace) {
                    history.replaceState(state, title, url);
                } else {
                    history.pushState(state, title, url);
                }
            }
        };
    }

    window.addEventListener("DOMContentLoaded", () => {
        if (pageOrchestrator && pageOrchestrator.start) {
            pageOrchestrator.start();
        }

        const logoutLink = document.querySelector('div.logout a[href="../Logout"]');
        if (logoutLink) {
            logoutLink.addEventListener('click', (e) => {
                e.preventDefault();
                makeCall("GET", "../Logout", null, function(req) {
                    sessionStorage.clear(); // Clear all session storage (includes userName)
                    // Use replace to prevent back navigation to this page
                    window.location.replace("../HTML/Login.html");
                });
            });
        }
    });
}
