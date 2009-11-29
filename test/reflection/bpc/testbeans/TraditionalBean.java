package reflection.bpc.testbeans;

public class TraditionalBean {
    
    private String name;
    private int age;
    private double accountBalance;
    
    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getAccountBalance() {
        return accountBalance;
    }
    public int getAge() {
        return age;
    }
    public String getName() {
        return name;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(accountBalance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + age;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TraditionalBean other = (TraditionalBean) obj;
        if (Double.doubleToLongBits(accountBalance) != Double
                .doubleToLongBits(other.accountBalance))
            return false;
        if (age != other.age)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
