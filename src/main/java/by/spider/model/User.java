package by.spider.model;

public class User {
    private final String name;
    private final String lastname;
    private final String email;
    private final int number;

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

    public int getNumber() {
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
        private int number = 0;

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
        public UserBuilder number(int number){
            if (number > 0) {
                this.number = number;
            }
            return this;
        }
        public User build(){
            return new User(this);
        }

    }
}

