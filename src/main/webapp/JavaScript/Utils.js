/**
 * AJAX call
 * @param method is the kind of action: GET or POST
 * @param url is the servlet that should be called
 * @param formElement is the form that has been selected, or a FormData object
 * @param callBack is the function to call when arrive the answer
 * @param objectToSend is an object to send as JSON
 * @param reset true reset the fields of the form (only if formElement is an HTMLFormElement)
 */
function makeCall(method, url, formElement, callBack, objectToSend, reset = true) {
    let request = new XMLHttpRequest();

    request.onreadystatechange = function() {
        if (request.readyState === XMLHttpRequest.DONE) {
            if (request.status >= 500) {
                window.location.href = "ErrorPage.html";
                return;
            }
            callBack(request);
        }
    };

    request.open(method, url);
    request.setRequestHeader("X-Requested-With", "XMLHttpRequest");

    if (objectToSend) {
        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        request.send(JSON.stringify(objectToSend));
    } else if (formElement) {
        if (formElement instanceof FormData) {
            request.send(formElement);
        } else if (formElement instanceof HTMLFormElement) {
            request.send(new FormData(formElement));
        } else {
            console.error("makeCall: formElement parameter is of an unexpected type.", formElement);
            request.send();
        }
    } else {
        request.send();
    }

    if (formElement instanceof HTMLFormElement && reset === true) {
        formElement.reset();
    }
}
