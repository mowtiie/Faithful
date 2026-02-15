package com.mowtiie.faithful.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.thought.Thought;
import com.mowtiie.faithful.util.DateTimeUtil;
import com.mowtiie.faithful.util.SettingUtil;

import java.util.ArrayList;

public class ThoughtAdapter extends RecyclerView.Adapter<ThoughtAdapter.ViewHolder> {

    private final Context context;
    private final Listener listener;
    private final SettingUtil settingUtil;
    private ArrayList<Thought> thoughts;

    public ThoughtAdapter(Context context, Listener listener, ArrayList<Thought> thoughts) {
        this.context = context;
        this.listener = listener;
        this.thoughts = thoughts;
        this.settingUtil = new SettingUtil(context);
    }

    public interface Listener {
        void OnClick(int position);
        void OnDeleteClick(int position);
        void OnShareClick(int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void search(ArrayList<Thought> searchedThoughts) {
        this.thoughts = searchedThoughts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ThoughtAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View thoughtView = LayoutInflater.from(context).inflate(R.layout.recycler_thought, parent, false);
        return new ViewHolder(thoughtView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThoughtAdapter.ViewHolder holder, int position) {
        Thought thought = thoughts.get(position);
        holder.bind(thought, settingUtil);
        holder.itemView.setOnClickListener(v -> listener.OnClick(position));
        holder.thoughtDelete.setOnClickListener(v -> listener.OnDeleteClick(position));
        holder.thoughtShare.setOnClickListener(v -> listener.OnShareClick(position));
    }

    @Override
    public int getItemCount() {
        return thoughts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView thoughtParent;
        MaterialTextView thoughtTimeStamp, thoughtContent;
        MaterialButton thoughtDelete, thoughtShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thoughtParent = itemView.findViewById(R.id.recycler_thought_parent);
            thoughtTimeStamp = itemView.findViewById(R.id.recycler_thought_timestamp);
            thoughtContent = itemView.findViewById(R.id.recycler_thought_content);
            thoughtDelete = itemView.findViewById(R.id.recycler_thought_delete);
            thoughtShare = itemView.findViewById(R.id.recycler_thought_share);
        }

        public void bind(Thought thought, SettingUtil settingUtil) {
            String timeStamp = DateTimeUtil.getStringDateTime(thought.getTimestamp());
            String content = thought.getContent();

            thoughtTimeStamp.setText(timeStamp);
            thoughtContent.setText(content);
        }
    }
}