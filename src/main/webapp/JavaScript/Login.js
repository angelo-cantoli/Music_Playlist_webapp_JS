/**
 * Login
 */
(function() {
    document.getElementById("loginButton").addEventListener('click' , (e) => {

        console.log("Login event!");

        let form = e.target.closest("form");

        if(form.checkValidity()){

            makeCall("POST" , '../CheckLogin' , form ,
                function (x) {

                    if(x.readyState == XMLHttpRequest.DONE){
                        let message = x.responseText;
                        switch(x.status){

                            case 200:

                                try {
                                    sessionStorage.setItem('userName' , message);
                                } catch (e) {
                                    console.error("Failed to parse login response:", e);
                                    document.getElementById("error").textContent = "Login error - please try again";
                                    return;
                                }
                                window.location.href = "../HTML/HomePage.html";
                                break;
                            default:
                                document.getElementById("error").textContent = message;
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
