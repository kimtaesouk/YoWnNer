package com.example.yownner;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemSwipe(int position);
}
