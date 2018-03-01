package Classes;

/**
 * Created by DELL on 14/02/2018.
 */

public class Stores {

    private String title;
    private String address;
    private String image;
    private Double latitude;
    private Double longitude;

    public Stores(String title, String address, String image, Double latitude, Double longitude) {
        this.title = title;
        this.address = address;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Stores() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
