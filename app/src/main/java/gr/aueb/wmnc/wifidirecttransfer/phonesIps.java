package gr.aueb.wmnc.wifidirecttransfer;

import java.io.Serializable;

public class phonesIps implements Serializable{

    public static final long serialVersionUID = 42L;

    private String serverIp;
    private String clientIp;

    public phonesIps(){

    }

    public phonesIps(String serverIp, String clientIp){
        this.serverIp = serverIp;
        this.clientIp = clientIp;
    }

    public void setServerIp(String serverIp){
        this.serverIp = serverIp;
    }

    public void setClientIp(String clientIp){
        this.clientIp = clientIp;
    }

    public String getServerIp(){
        return serverIp;
    }

    public String getClientIp() {
        return clientIp;
    }
}
