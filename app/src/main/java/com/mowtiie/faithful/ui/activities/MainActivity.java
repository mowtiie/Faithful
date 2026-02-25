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

        allThoughts = new ArrayList<>();
        displayedThoughts = new ArrayList<>();
        thoughtRepository = new ThoughtRepository(this);
        allThoughts.addAll(thoughtRepository.getAll());
        allThoughts.sort(Thought.SORT_DESCENDING);
        displayedThoughts.addAll(allThoughts);

        thoughtAdapter = new ThoughtAdapter(this, this, new ArrayList<>(displayedThoughts));
        binding.emptyIndicator.setVisibility(displayedThoughts.isEmpty() ? View.VISIBLE : View.GONE);
        binding.thoughtsList.setLayoutManager(new LinearLayoutManager(this));
        binding.thoughtsList.setAdapter(thoughtAdapter);

        binding.writeThought.setOnClickListener(v -> showNewThoughtDialog());
        binding.undoFilter.setOnClickListener(v -> {
            binding.undoFilter.setVisibility(View.GONE);
            refreshList();
        });

        if (getIntent().getBooleanExtra("QUICK_THOUGHT", false)) {
            showNewThoughtDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        MenuItem lockItem = menu.findItem(R.id.lock);
        lockItem.setVisible(settingUtil.getPassword() != null);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView == null) return true;

        searchView.setQueryHint(getString(R.string.hint_toolbar_search));
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
        for (Thought item : allThoughts) {
            if (item.getContent().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        displayedThoughts.clear();
        displayedThoughts.addAll(filteredList);
        thoughtAdapter.updateList(new ArrayList<>(displayedThoughts));
        binding.emptyIndicator.setVisibility(displayedThoughts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_latest) {
            sortThoughts(false);
        } else if (item.getItemId() == R.id.sort_oldest) {
            sortThoughts(true);
        } else if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (item.getItemId() == R.id.lock) {
            LockUtil.getInstance().lock();
            Intent lockIntent = new Intent(this, LockActivity.class);
            lockIntent.putExtra(LockActivity.EXTRA_RETURN_CLASS, MainActivity.class.getName());
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(lockIntent);
        } else if (item.getItemId() == R.id.filter_date) {
            showFilterByDateDialog();
        }
        return true;
    }

    private void showFilterByDateDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            ArrayList<Thought> filtered = thoughtRepository.getByDate(selection);
            thoughtAdapter.updateList(new ArrayList<>(filtered));
            binding.undoFilter.setVisibility(View.VISIBLE);
            binding.emptyIndicator.setVisibility(displayedThoughts.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void sortThoughts(boolean isAscending) {
        allThoughts.sort(isAscending ? Thought.SORT_ASCENDING : Thought.SORT_DESCENDING);
        displayedThoughts.sort(isAscending ? Thought.SORT_ASCENDING : Thought.SORT_DESCENDING);
        thoughtAdapter.updateList(new ArrayList<>(displayedThoughts));
    }

    private void refreshList() {
        List<Thought> freshList = thoughtRepository.getAll();
        freshList.sort(Thought.SORT_DESCENDING);

        allThoughts.clear();
        allThoughts.addAll(freshList);
        displayedThoughts.clear();
        displayedThoughts.addAll(freshList);

        thoughtAdapter.updateList(new ArrayList<>(displayedThoughts));
        binding.emptyIndicator.setVisibility(displayedThoughts.isEmpty() ? View.VISIBLE : View.GONE);
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
        newThoughtDialog.setOnShowListener(dialog ->
                newThoughtDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String thoughtContent = Objects.requireNonNull(thoughtContentText.getText()).toString().trim();
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
                    binding.thoughtsList.post(() -> binding.thoughtsList.smoothScrollToPosition(0));
                }));
        newThoughtDialog.show();

        thoughtContentText.requestFocus();
        thoughtContentText.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(thoughtContentText, InputMethodManager.SHOW_IMPLICIT);
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thoughtRepository != null) {
            thoughtRepository.close();
        }
        binding = null;
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
                .create().show();
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
                })
                .create().show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("OPEN_DIALOG", false)) showNewThoughtDialog();
    }

    @Override
    public void OnShareClick(Thought thought) {
        Intent sendIntent = new Intent();
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, thought.getContent());
        startActivity(Intent.createChooser(sendIntent, null));
    }
}
