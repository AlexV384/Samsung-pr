package com.example.nousers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

// Активность для отображения меню

public class MenuActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private LinearLayout mSportList;
    private FloatingActionButton addAdvertisementButton;
    private static final int NEW_ADVERTISEMENT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSportList = findViewById(R.id.sport_list);

        // Слушатель изменений в дочерних элементах узла "advertisements" в базе данных
        mDatabase.child("advertisements").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Advertisement advertisement = dataSnapshot.getValue(Advertisement.class);
                addAdvertisementToView(advertisement);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        addAdvertisementButton = findViewById(R.id.add_advertisement_button);
        addAdvertisementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, NewAdvertisementActivity.class);
                startActivityForResult(intent, NEW_ADVERTISEMENT_REQUEST_CODE);
            }
        });
    }

    // Метод для добавления объявления на экран
    private void addAdvertisementToView(Advertisement advertisement) {
        String sport = advertisement.getSport();
        RecyclerView recyclerView = null;
        for (int i = 0; i < mSportList.getChildCount(); i++) {
            View child = mSportList.getChildAt(i);
            if (child instanceof RecyclerView && sport.equals(child.getTag())) {
                recyclerView = (RecyclerView) child;
                break;
            }
        }
        if (recyclerView == null) {
            TextView sportTextView = new TextView(this);
            sportTextView.setText(sport);
            sportTextView.setTextSize(30);
            sportTextView.setTypeface(null, Typeface.BOLD);
            mSportList.addView(sportTextView);
            recyclerView = new RecyclerView(this);
            recyclerView.setTag(sport);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
            recyclerView.setAdapter(new AdvertisementAdapter(new ArrayList<>()));
            mSportList.addView(recyclerView);
        }
        AdvertisementAdapter adapter = (AdvertisementAdapter) recyclerView.getAdapter();
        adapter.addAdvertisement(advertisement);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (advertisement.getUserId().equals(uid) || advertisement.getParticipants().contains(uid)) {
            RecyclerView myAdsRecyclerView = null;
            for (int i = 0; i < mSportList.getChildCount(); i++) {
                View child = mSportList.getChildAt(i);
                if (child instanceof RecyclerView && "Мои объявления".equals(child.getTag())) {
                    myAdsRecyclerView = (RecyclerView) child;
                    break;
                }
            }
            if (myAdsRecyclerView == null) {
                TextView myAdsTextView = new TextView(this);
                myAdsTextView.setText("Мои объявления");
                myAdsTextView.setTextSize(30);
                myAdsTextView.setTypeface(null, Typeface.BOLD);
                mSportList.addView(myAdsTextView, 0);
                myAdsRecyclerView = new RecyclerView(this);
                myAdsRecyclerView.setTag("Мои объявления");
                myAdsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                myAdsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
                myAdsRecyclerView.setAdapter(new AdvertisementAdapter(new ArrayList<>()));
                mSportList.addView(myAdsRecyclerView, 1);
            }
            AdvertisementAdapter myAdsAdapter = (AdvertisementAdapter) myAdsRecyclerView.getAdapter();
            myAdsAdapter.addAdvertisement(advertisement);
        }
    }

    // Адаптер для отображения списка объявлений
    class AdvertisementAdapter extends RecyclerView.Adapter<AdvertisementAdapter.ViewHolder> {
        private final List<Advertisement> mAdvertisements;

        public AdvertisementAdapter(List<Advertisement> advertisements) {
            mAdvertisements = advertisements;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advertisement_view, parent, false);
            int screenWidth = parent.getResources().getDisplayMetrics().widthPixels;
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (int) (screenWidth * 0.8);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Advertisement advertisement = mAdvertisements.get(position);
            holder.mTitleTextView.setText(advertisement.getTitle());
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadii(new float[] {10, 10, 10, 10, 0, 0, 0, 0});
            drawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.c3));
            drawable.setStroke(2, ContextCompat.getColor(holder.itemView.getContext(), R.color.c1));
            holder.mTitleTextView.setBackground(drawable);
            holder.mTitleTextView.setPadding(20, 20, 20, 20);
            holder.mTitleTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.c1));
            holder.mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            holder.mTitleTextView.setTypeface(null, Typeface.BOLD);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, AdvertisementDetailsActivity.class);
                    intent.putExtra("advertisement", advertisement);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAdvertisements.size();
        }

        public void addAdvertisement(Advertisement advertisement) {
            mAdvertisements.add(advertisement);
            notifyItemInserted(mAdvertisements.size() - 1);
        }

        public void removeAdvertisement(Advertisement advertisement) {
            int position = mAdvertisements.indexOf(advertisement);
            if (position != -1) {
                mAdvertisements.remove(position);
                notifyItemRemoved(position);
            }
        }

        // ViewHolder для элементов списка
        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTitleTextView;

            ViewHolder(View view) {
                super(view);
                mTitleTextView = view.findViewById(R.id.advertisement_title);
            }
        }
    }
}