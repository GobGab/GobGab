package gobgabllc.gobgab;

import android.content.Context;

import java.util.List;

import fr.rolandl.carousel.CarouselAdapter;
import fr.rolandl.carousel.CarouselItem;

/**
 * Created by David on 3/18/2016.
 */
public final class MyCarouselAdapter extends CarouselAdapter<PrimaryUICarousel> {

    public MyCarouselAdapter(Context context, List<PrimaryUICarousel> icons) {
        super(context, icons);
    }

    @Override
    public CarouselItem<PrimaryUICarousel> getCarouselItem(Context context) {
        return new PrimaryUICarouselItem(context);
    }

}