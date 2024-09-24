package com.axel.booknest.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.axel.booknest.Model.Book;
import com.axel.booknest.R;
import com.axel.booknest.Ui.PdfViewerActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Load the book image
        Picasso.get().load(book.getImageUrl()).into(holder.bookImageView);

        // Set the book title
        holder.bookTitle.setText(book.getTitle());

        // Handle click events to open the PDF (Implement the logic to open the book)
        holder.itemView.setOnClickListener(v -> {
            // Open book's PDF (you can add PDF viewer integration here)
            Intent intent = new Intent(v.getContext(), PdfViewerActivity.class);
            intent.putExtra("pdfUrl", book.getPdfUrl()); // Pass PDF URL
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImageView;
        TextView bookTitle;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImageView = itemView.findViewById(R.id.bookImageView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
        }
    }
}
