
{
    var handleSorting = new HandleSorting();

    function HandleSorting() {

        let startElement = null;

        this.addEventListeners = function(tableBodyId, errorContainerId) {
            const tableBody = document.getElementById(tableBodyId);
            if (!tableBody) {
                console.error("Table body with ID '" + tableBodyId + "' not found.");
                return;
            }
            const elements = tableBody.getElementsByClassName("draggable");

            for(let i = 0 ; i < elements.length ; i++){
                if (!elements[i].dataset.dndInitialized) {
                    elements[i].draggable = true; //Ensure it can be dragged

                    elements[i].addEventListener("dragstart" , dragStart);
                    elements[i].addEventListener("dragover" , dragOver);
                    elements[i].addEventListener("dragleave" , dragLeave);
                    elements[i].addEventListener("drop" , drop);
                    
                    elements[i].dataset.dndInitialized = "true";
                }
            }
        }

        function unselectRows(rows) {
            for(let i = 0 ; i < rows.length ; i++){
                rows[i].classList.remove("selected");
            }
        }

        function dragStart(event) {

            startElement = event.target.closest("tr");

        }

        /**
         * The dragover event is fired when an element is being dragged over a valid drop target
         * @param event is the event caused by the user
         */
        function dragOver(event) {
            event.preventDefault();

            let dest = event.target.closest("tr");
            if (dest && dest.classList.contains("draggable") && dest !== startElement) {
                dest.classList.add("selected");
            }
        }


        function dragLeave(event) {
            let dest = event.target.closest("tr");
            if (dest && dest.classList.contains("draggable")) {
                dest.classList.remove("selected");
            }
        }


        function drop(event) {
            event.preventDefault();

            let dest = event.target.closest("tr");

            if (startElement && dest && dest.classList.contains("draggable") && startElement !== dest) {
                let parentTBody = startElement.parentElement;
                let allDraggableRows = Array.from(parentTBody.getElementsByClassName("draggable"));
                
                const rect = dest.getBoundingClientRect();
                const offsetY = event.clientY - rect.top;

                if (offsetY < dest.offsetHeight / 2) {

                    parentTBody.insertBefore(startElement, dest);
                } else {

                    parentTBody.insertBefore(startElement, dest.nextSibling);
                }
                
                unselectRows(allDraggableRows);
            }

            
            startElement = null;
        }
    }

    window.initializeDragAndDrop = function(tableBodyId, errorContainerId) {
        handleSorting.addEventListeners(tableBodyId, errorContainerId);
    };
}
