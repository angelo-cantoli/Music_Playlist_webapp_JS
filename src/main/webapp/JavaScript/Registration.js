/**
 * Registration
 */
(function () {
    document.getElementById("registrationButton").addEventListener('click' , (e) => {

        console.log("Registration event!");

        let form = e.target.closest("form");

        document.getElementById("error").textContent = null;


        if(form.checkValidity()){

            let user = document.getElementById("userReg").value;
            let password = document.getElementById("passwordReg").value;
            let firstName = document.getElementById("firstNameReg").value;
            let lastName = document.getElementById("lastNameReg").value;

            if(user.length > 45 || password.length > 45 || firstName.length > 100 || lastName.length > 100){
                document.getElementById("error").textContent = "One or more fields are too long (username/password max 45, names max 100)";
                return;
            }
            if(!(password.includes("0") || password.includes("1") || password.includes("2") ||
                    password.includes("3") || password.includes("4") || password.includes("5") ||
                    password.includes("6") || password.includes("7") || password.includes("8") ||
                    password.includes("9")) ||
                !(password.includes("#") || password.includes("@") || password.includes("_")) ||
                password.length < 4){
                document.getElementById("error").textContent = "Password has to contain at least:4 character,1 number and 1 of the following @,# and _ ";
                return;
            }
            console.log("Validity ok");

            makeCall("POST" , '../Registration' , form ,
                function (x) {

                    if(x.readyState == XMLHttpRequest.DONE){
                        switch(x.status){

                            case 200:
                                window.location.href = "../HTML/Login.html";
                                break;

                            default:
                                document.getElementById("error").textContent = x.responseText;
                        }
                    }
                }
            );
        }else{
            form.reportValidity();
        }
    });
})();
