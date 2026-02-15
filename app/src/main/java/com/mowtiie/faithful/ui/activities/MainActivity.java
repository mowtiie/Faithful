package com.mowtiie.faithful.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.thought.Thought;
import com.mowtiie.faithful.data.thought.ThoughtRepository;
import com.mowtiie.faithful.databinding.ActivityMainBinding;
import com.mowtiie.faithful.ui.adapters.ThoughtAdapter;
import com.mowtiie.faithful.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends FaithfulActivity implements ThoughtAdapter.Listener {

    private ActivityMainBinding binding;
    private ThoughtRepository thoughtRepository;
    private ArrayList<Thought> thoughts;
    private ThoughtAdapter thoughtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent().getBooleanExtra("QUICK_THOUGHT", false)) {
            showNewThoughtDialog();
        }

        thoughts = new ArrayList<>();
        thoughtRepository = new ThoughtRepository(this);
        thoughts.addAll(thoughtRepository.getAll());
        thoughts.sort(Thought.SORT_DESCENDING);
        thoughtAdapter = new ThoughtAdapter(this, this, thoughts);

        binding.emptyIndicator.setVisibility(thoughts.isEmpty() ? View.VISIBLE : View.GONE);
        binding.thoughtsList.setLayoutManager(new LinearLayoutManager(this));
        binding.thoughtsList.setAdapter(thoughtAdapter);

        binding.toolbar.setNavigationOnClickListener(v -> {
            Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        });

        binding.writeThought.setOnClickListener(v -> showNewThoughtDialog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        searchView.setQueryHint("Search here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchThoughts(newText);
                return true;
            }
        });
        return true;
    }

    private void searchThoughts(String text) {
        ArrayList<Thought> filteredList = new ArrayList<>();
        for (Thought item : thoughts) {
            if (item.getContent().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        thoughtAdapter.search(filteredList);
        binding.emptyIndicator.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_latest) {
            sortThoughts(false);
        }

        if (item.getItemId() == R.id.sort_oldest) {
            sortThoughts(true);
        }

        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortThoughts(boolean isAscending) {
        thoughts.sort(isAscending ? Thought.SORT_ASCENDING : Thought.SORT_DESCENDING);
        thoughtAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshList() {
        thoughts.clear();
        thoughts.addAll(thoughtRepository.getAll());
        thoughts.sort(Thought.SORT_DESCENDING);

        binding.emptyIndicator.setVisibility(thoughts.isEmpty() ? View.VISIBLE : View.GONE);
        binding.thoughtsList.setVisibility(thoughts.isEmpty() ? View.GONE : View.VISIBLE);
        thoughtAdapter.notifyDataSetChanged();
    }

    private void showNewThoughtDialog() {
        View newThoughtDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_thought, null, false);

        TextInputLayout thoughtContentLayout = newThoughtDialogView.findViewById(R.id.field_thought_content_layout);
        TextInputEditText thoughtContentText = newThoughtDialogView.findViewById(R.id.field_thought_content_text);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_new_thought)
                .setIcon(R.drawable.ic_thought)
                .setView(newThoughtDialogView)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_confirm, null);

        AlertDialog newThoughtDialog = builder.create();
        newThoughtDialog.setOnShowListener(dialog -> newThoughtDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String thoughtContent = Objects.requireNonNull(thoughtContentText.getText()).toString();
            if (thoughtContent.isEmpty()) {
                thoughtContentLayout.setError(getString(R.string.field_thought_content_empty_error));
                return;
            }

            Thought thought = new Thought();
            thought.setId(UUID.randomUUID().toString());
            thought.setContent(thoughtContent);
            thought.setTimestamp(System.currentTimeMillis());
            thoughtRepository.add(thought);
            refreshList();
            newThoughtDialog.dismiss();
        }));
        newThoughtDialog.show();

        thoughtContentText.requestFocus();
        thoughtContentText.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(thoughtContentText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void OnClick(int position) {
        Thought thought = thoughts.get(position);
        String timestamp;
        if (settingUtil.getTimestamp().equals("Dynamic")) {
            timestamp = DateTimeUtil.getPrettyStringDateTime(thought.getTimestamp());
        } else {
            timestamp = DateTimeUtil.getStringDateTime(thought.getTimestamp());
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(timestamp)
                .setIcon(R.drawable.ic_thought)
                .setMessage(thought.getContent())
                .setPositiveButton(R.string.dialog_button_close, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void OnDeleteClick(int position) {
        Thought thought = thoughts.get(position);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_thought)
                .setIcon(R.drawable.ic_delete)
                .setMessage(R.string.dialog_message_delete_thought)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_delete, (dialogInterface, i) -> {
                    thoughtRepository.delete(thought.getId());
                    refreshList();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("OPEN_DIALOG", false)) {
            showNewThoughtDialog();
        }
    }

    @Override
    public void OnShareClick(int position) {
        Thought thought = thoughts.get(position);

        Intent sendIntent = new Intent();
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, thought.getContent());

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}