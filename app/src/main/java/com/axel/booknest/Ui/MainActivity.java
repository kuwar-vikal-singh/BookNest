package com.axel.booknest.Ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axel.booknest.Adapter.BookAdapter;
import com.axel.booknest.Adapter.GenreAdapter;
import com.axel.booknest.Model.Book;
import com.axel.booknest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.view.View; // Ensure this is imported

public class MainActivity extends AppCompatActivity {

    private RecyclerView genreRecyclerView, bookListRecyclerView;
    private GenreAdapter genreAdapter;
    private BookAdapter bookAdapter;
    private List<String> genres;
    private List<Book> books;

    private FirebaseAuth mAuth;

    private ImageView logoutImg;
    Button btnDilogCancle, btnDilogLogout;
    Dialog dialog;

    private TextView viewAll;
    private TextView greeting,slogan;

    private boolean isPremium;
    private boolean isGridLayout = false;

    // ProgressBars
    private ProgressBar loadingProgressBar, loadingProgressBar2, loadingProgressBar3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize ProgressBars
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingProgressBar2 = findViewById(R.id.loadingProgressBar2);
        loadingProgressBar3 = findViewById(R.id.loadingProgressBar3);

        // Initialize other views and variables
        greeting = findViewById(R.id.greeting);
        viewAll = findViewById(R.id.viewAllTextView);
        slogan = findViewById(R.id.slogan);

        genreRecyclerView = findViewById(R.id.genreRecyclerView);
        bookListRecyclerView = findViewById(R.id.bookListRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dilog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dilog_bg));
        dialog.setCancelable(false);

        btnDilogCancle = dialog.findViewById(R.id.dilogCancle);
        btnDilogLogout = dialog.findViewById(R.id.dilogLogout);

        logoutImg = findViewById(R.id.logoutImg);
        logoutImg.setOnClickListener(view -> dialog.show());

        btnDilogLogout.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnDilogCancle.setOnClickListener(view -> dialog.dismiss());

        genreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bookListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        genres = new ArrayList<>();
        books = new ArrayList<>();

        genreAdapter = new GenreAdapter(genres, genre -> loadBooksForGenre(genre));
        bookAdapter = new BookAdapter(books);

        genreRecyclerView.setAdapter(genreAdapter);
        bookListRecyclerView.setAdapter(bookAdapter);

        viewAll.setOnClickListener(view -> toggleBookLayout());

        // Load user premium status and name
        checkUserPremiumStatus();
    }

    private void toggleBookLayout() {
        if (isGridLayout) {
            bookListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            viewAll.setText("View All");
            slogan.setVisibility(View.VISIBLE);
        } else {
            bookListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            viewAll.setText("View Less");
            slogan.setVisibility(View.GONE);
        }
        isGridLayout = !isGridLayout;
    }

    private void checkUserPremiumStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        isPremium = dataSnapshot.child("premium").getValue(Boolean.class);
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        if (userName != null && !userName.isEmpty()) {
                            String formattedName = userName.substring(0, 1).toUpperCase() + userName.substring(1).toLowerCase();
                            greeting.setText("Welcome, " + formattedName + " !");
                        } else {
                            greeting.setText("Welcome!");
                        }
                        loadGenres();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Failed to load user status", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadGenres() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference genresRef = database.getReference("genres");

        genresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                genres.clear();  // Clear the previous list to avoid duplicates
                for (DataSnapshot genreSnapshot : dataSnapshot.getChildren()) {
                    String genre = genreSnapshot.getKey();
                    String type = genreSnapshot.child("type").getValue(String.class);  // Get the type (free/paid)

                    // If the genre is 'paid' and the user is not premium, skip it
                    if (type.equals("paid") && !isPremium) {
                        continue;  // Skip paid genres for non-premium users
                    }

                    genres.add(genre);

                    // Load books for the default "CodingBook" genre
                    if (genre.equals("CodingBook")) {
                        loadBooksForGenre(genre);
                    }
                }
                genreAdapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE); // Hide progress bar after genres load
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load genres", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadBooksForGenre(String genre) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("genres").child(genre).child("books");

        books.clear();
        loadingProgressBar2.setVisibility(View.VISIBLE); // Show progress bar before books load

        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    books.add(book);
                }
                bookAdapter.notifyDataSetChanged();
                loadingProgressBar2.setVisibility(View.GONE);  // Hide after books load
                loadingProgressBar3.setVisibility(View.GONE);  // Hide other progress bar
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
