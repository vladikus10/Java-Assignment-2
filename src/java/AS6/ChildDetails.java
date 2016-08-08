
package AS6;

public class ChildDetails {
    private String account;
    private String name;
    private String address;
    
    public void setDetails(String account, String name, String address){
        this.account = account;
        this.name = name;
        this.address = address;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
}
