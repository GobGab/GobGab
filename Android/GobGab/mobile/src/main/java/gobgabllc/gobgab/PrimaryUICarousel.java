package gobgabllc.gobgab;

import java.io.Serializable;

/**
 * Created by David on 3/18/2016.
 */
public final class PrimaryUICarousel implements Serializable {

    private static final long serialVersionUID = 1L;

    public final String name;

    public final String image;

    public PrimaryUICarousel(String name, String image) {
        this.name = name;
        this.image = image;
    }

}
