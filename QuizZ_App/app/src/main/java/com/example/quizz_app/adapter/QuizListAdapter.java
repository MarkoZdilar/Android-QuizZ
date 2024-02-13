package com.example.quizz_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizz_app.R;
import com.example.quizz_app.model.QuizListModel;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizListViewHolder> {

    private List<QuizListModel> quizListModels;
    private OnItemClickedListener onItemClickedListener;

    public void setQuizListModels(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }

    public QuizListAdapter(OnItemClickedListener onItemClickedListener){
        this.onItemClickedListener = onItemClickedListener;
    }
    @Override
    public QuizListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_layout, parent, false);
        return new QuizListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuizListViewHolder holder, int position) {
        QuizListModel model = quizListModels.get(position);
        holder.title.setText(model.getTitle());
        Glide.with(holder.itemView).load(model.getImage()).into(holder.imageOfQuiz);
    }

    @Override
    public int getItemCount() {
        if(quizListModels != null) {
            return quizListModels.size();
        }
        return 0;
    }

    public class QuizListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView imageOfQuiz;
        private ConstraintLayout constraintLayout;

        public QuizListViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.quizTitleList);
            imageOfQuiz = itemView.findViewById(R.id.quizImageList);
            constraintLayout = itemView.findViewById(R.id.quizListConstraintLayout);
            constraintLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickedListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickedListener {
        void onItemClick(int position);

    }
}
