public class InfoStore {
    private String id;
    private String login;
    private String password;

    /**
     * Primarily for data transfer in one object
     * @param id
     * @param login
     * @param password
     */
    public InfoStore(String id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }


}
