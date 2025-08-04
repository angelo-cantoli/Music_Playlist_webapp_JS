package it.polimi.tiw.progetto_tiw_js.beans;


    public class User{
        private int id;
        private String userName;
        private String password;
        private String firstName;
        private String lastName;



        public User(String userName, String password, int id, String firstName, String lastName) {
            this.userName = userName;
            this.password = password;
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        /**
         * @return the userName
         */
        public String getUserName() {
            return userName;
        }

        /**
         * @return the password of the user
         */
        public String getPassword() {
            return password;
        }

        /**
         * @return the user's id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the firstName of the user
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         * @return the lastName of the user
         */
        public String getLastName() {
            return lastName;
        }

        /**
         * Change the userName of the user
         * @param newUserName is the new userName
         */
        public void setUserName(String newUserName) {
            this.userName = newUserName;
        }

        /**
         * Change the password of the user
         * @param newPassword is the new password
         */
        public void setPassword(String newPassword) {
            this.password = newPassword;
        }

        /**
         * Change the id of the user
         * @param id is the new id
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * Set the firstName of the user
         * @param firstName is the new firstName
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        /**
         * Set the lastName of the user
         * @param lastName is the new lastName
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

    }

