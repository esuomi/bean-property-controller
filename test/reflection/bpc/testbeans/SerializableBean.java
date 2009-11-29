package reflection.bpc.testbeans;

import java.io.Serializable;

public class SerializableBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String serial;
    
    public void setSerial(String serial) {
        this.serial = serial;
    }
    
    public String getSerial() {
        return serial;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((serial == null) ? 0 : serial.hashCode());
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
        SerializableBean other = (SerializableBean) obj;
        if (serial == null) {
            if (other.serial != null)
                return false;
        } else if (!serial.equals(other.serial))
            return false;
        return true;
    }
    
    

}
