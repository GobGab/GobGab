package gobgabllc.gobgab;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.rolandl.carousel.CarouselItem;

/**
 * Created by David on 3/18/2016.
 */
    public final class PrimaryUICarouselItem extends CarouselItem<PrimaryUICarousel>
    {

        private ImageView image;

        private TextView name;

        private Context context;

        public PrimaryUICarouselItem(Context context)
        {
            super(context, R.layout.primary_carousel_item);
            this.context = context;
        }

        @Override
        public void extractView(View view)
        {
            image = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void update(PrimaryUICarousel photo)
        {
            image.setImageResource(getResources().getIdentifier(photo.image, "drawable", context.getPackageName()));
            name.setText(photo.name);
        }

    }
