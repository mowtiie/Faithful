package com.mowtiie.faithful.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.thought.Thought;
import com.mowtiie.faithful.data.thought.ThoughtRepository;
import com.mowtiie.faithful.databinding.ActivityMainBinding;
import com.mowtiie.faithful.ui.adapters.ThoughtAdapter;
import com.mowtiie.faithful.util.DateTimeUtil;
import com.mowtiie.faithful.util.LockUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends FaithfulActivity implements ThoughtAdapter.Listener {

    private ActivityMainBinding binding;
    private ThoughtRepository thoughtRepository;

    private ArrayList<Thought> allThoughts;
    private ArrayList<Thought> displayedThoughts;
    private ThoughtAdapter thoughtAdapter;

    private Long selectedFilterDate = null;
    private String currentSearchQuery = "";

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

        thoughtRepository = new ThoughtRepository(this);
        allThoughts = new ArrayList<>();
        displayedThoughts = new ArrayList<>();

        thoughtAdapter = new ThoughtAdapter(this, this, displayedThoughts);
        binding.thoughtsList.setLayoutManager(new LinearLayoutManager(this));
        binding.thoughtsList.setAdapter(thoughtAdapter);

        refreshList();

        binding.writeThought.setOnClickListener(v -> showNewThoughtDialog());

        binding.undoFilter.setOnClickListener(v -> {
            selectedFilterDate = null;
            binding.undoFilter.hide();
            refreshList();
            Toast.makeText(this, "Filter cleared", Toast.LENGTH_SHORT).show();
        });

        if (getIntent().getBooleanExtra("QUICK_THOUGHT", false)) {
            showNewThoughtDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem lockItem = menu.findItem(R.id.lock);
        lockItem.setVisible(settingUtil.getPassword() != null);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.hint_toolbar_search));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) { return false; }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentSearchQuery = newText;
                    applyFilters();
                    return true;
                }
            });
        }
        return true;
    }

    private void applyFilters() {
        ArrayList<Thought> results = new ArrayList<>();

        List<Thought> source = (selectedFilterDate != null)
                ? thoughtRepository.getByDate(selectedFilterDate)
                : allThoughts;

        for (Thought item : source) {
            if (item.getContent().toLowerCase().contains(currentSearchQuery.toLowerCase())) {
                results.add(item);
            }
        }

        displayedThoughts.clear();
        displayedThoughts.addAll(results);
        displayedThoughts.sort(Thought.SORT_DESCENDING);

        thoughtAdapter.updateList(new ArrayList<>(displayedThoughts));
        binding.emptyIndicator.setVisibility(displayedThoughts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showFilterByDateDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedFilterDate = selection;
            applyFilters();
            binding.undoFilter.show();

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Toast.makeText(this, "Filtering by: " + sdf.format(new Date(selection)), Toast.LENGTH_SHORT).show();
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void refreshList() {
        allThoughts.clear();
        allThoughts.addAll(thoughtRepository.getAll());
        applyFilters();
    }

    private void sortThoughts(boolean isAscending) {
        displayedThoughts.sort(isAscending ? Thought.SORT_ASCENDING : Thought.SORT_DESCENDING);
        thoughtAdapter.updateList(new ArrayList<>(displayedThoughts));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_latest) {
            sortThoughts(false);
        } else if (id == R.id.sort_oldest) {
            sortThoughts(true);
        } else if (id == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.lock) {
            LockUtil.getInstance().lock();
            Intent lockIntent = new Intent(this, LockActivity.class);
            lockIntent.putExtra(LockActivity.EXTRA_RETURN_CLASS, MainActivity.class.getName());
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(lockIntent);
        } else if (id == R.id.filter_date) {
            showFilterByDateDialog();
        }
        return true;
    }

    private void showNewThoughtDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_new_thought, null, false);
        TextInputLayout layout = view.findViewById(R.id.field_thought_content_layout);
        TextInputEditText editText = view.findViewById(R.id.field_thought_content_text);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_new_thought)
                .setIcon(R.drawable.ic_thought)
                .setView(view)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_confirm, (dialog, which) -> {
                    String content = Objects.requireNonNull(editText.getText()).toString().trim();
                    if (content.isEmpty()) {
                        layout.setError(getString(R.string.field_thought_content_empty_error));
                        return;
                    }
                    Thought thought = new Thought();
                    thought.setId(UUID.randomUUID().toString());
                    thought.setContent(content);
                    thought.setTimestamp(System.currentTimeMillis());
                    thoughtRepository.add(thought);

                    refreshList();
                    binding.thoughtsList.post(() -> binding.thoughtsList.smoothScrollToPosition(0));
                }).show();

        editText.requestFocus();
        editText.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }, 100);
    }

    @Override
    public void OnClick(Thought thought) {
        String timestamp = settingUtil.getTimestamp().equals("Dynamic")
                ? DateTimeUtil.getPrettyStringDateTime(thought.getTimestamp())
                : DateTimeUtil.getStringDateTime(thought.getTimestamp());

        new MaterialAlertDialogBuilder(this)
                .setTitle(timestamp)
                .setIcon(R.drawable.ic_thought)
                .setMessage(thought.getContent())
                .setPositiveButton(R.string.dialog_button_close, null)
                .show();
    }

    @Override
    public void OnDeleteClick(Thought thought) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_thought)
                .setIcon(R.drawable.ic_delete)
                .setMessage(R.string.dialog_message_delete_thought)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_delete, (dialog, i) -> {
                    thoughtRepository.delete(thought.getId());
                    refreshList();
                }).show();
    }

    @Override
    public void OnShareClick(Thought thought) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, thought.getContent());
        startActivity(Intent.createChooser(sendIntent, null));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thoughtRepository != null) thoughtRepository.close();
        binding = null;
    }
}