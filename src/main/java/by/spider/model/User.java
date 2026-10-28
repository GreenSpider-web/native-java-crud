package by.spider.model;

public class User {
    private final String name;
    private final String lastname;
    private final String email;
    private final String number;

    public User(UserBuilder userBuilder){
        this.name = userBuilder.name;
        this.lastname = userBuilder.last_name;
        this.email = userBuilder.email;
        this.number = userBuilder.number;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public static UserBuilder builder(){
        return new UserBuilder();
    }

    public static class UserBuilder
    {
        private String name = "Unknown";
        private String last_name = "Unknown";
        private String email = "Unknown";
        private String number = "";

        public UserBuilder name(String name){
            if(name != null && !name.isBlank()){
                this.name = name;
            }
            return this;
        }
        public UserBuilder lastname(String last_name){
            if(last_name != null && !last_name.isBlank()){
                this.last_name = last_name;
            }
            return this;
        }
        public UserBuilder email(String email){
            if(email != null && !email.isBlank()){
                this.email = email;
            }
            return this;
        }
        public UserBuilder number(String number){
            if (number.isBlank() ) {
                throw new RuntimeException("Enter correct phone number");
            }
            if(number.matches("\\d+"))
                this.number = number;
            else
                throw new RuntimeException("Enter correct phone number");
            return this;
        }
        public User build(){
            return new User(this);
        }

    }
}

