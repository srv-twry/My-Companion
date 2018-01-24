package com.example.srv_twry.studentcompanion.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.srv_twry.studentcompanion.R;

/**
 * Created by eXeeLD on 1/24/18.
 */

public class RecycleViewSwapUtils {

    private Bitmap recycleBinIcon;
    private int backgroundColor;
    private Paint paint;

    public RecycleViewSwapUtils(Context context){

        paint = new Paint();
        recycleBinIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_white);
        backgroundColor = context.getResources().getColor(R.color.flashCardsSwipeToDeleteBackground);
    }

    public void drawDeleteIndicator(Canvas c, RecyclerView.ViewHolder viewHolder, float dX, int actionState) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            RectF background;
            RectF icon_dest;
            paint.setColor(backgroundColor);

            if (dX > 0) {
                background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
            } else {
                background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
            }

            c.drawRect(background, paint);
            c.drawBitmap(recycleBinIcon, null, icon_dest, paint);
        }
    }
}
