package com.myproject.mytranslator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.mytranslator.DTO.LanguageInfo;
import com.myproject.mytranslator.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<LanguageInfo> myArrayList;
    public static ArrayList<LanguageInfo> filteredList;
    /*//RecyclerView에서 유저가 선택한 라디오 버튼의 위치
    public static int recyclerViewRadioBtnPosition = -1;*/

    //private int lastSelectedPosition = -1;

    //아이템 클릭 리스너 변수
    private OnItemClickListener onItemClickListener;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredList = myArrayList;
                } else {
                    ArrayList<LanguageInfo> filteringList = new ArrayList<>();
                    for(LanguageInfo languageInfo : myArrayList) {
                        if((languageInfo.getTextEnglish().contains(charString))||(languageInfo.getTextKorean().contains(charString))) {
                            filteringList.add(languageInfo);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<LanguageInfo>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    //아이템 클릭 인터페이스
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    //아이템 클릭 리스너 설정
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    //생성자에서 데이터 리스트 객체를 전달받는다.
    public RecyclerViewAdapter(ArrayList<LanguageInfo> myLanguageInfoList) {
        this.myArrayList = myLanguageInfoList;
        this.filteredList=myLanguageInfoList;
    }


    //item view를 위한 ViewHolder 객체를 생성하여 return
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_schedule, parent, false);
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_memo, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    //ArrayList에서 position에 해당하는 데이터를 ViewHolder의 item view에 표시
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {

        LanguageInfo languageInfo = filteredList.get(position);
        //holder.imageView.setImageResource(R.drawable.weather);
        if ((languageInfo != null)) {
            holder.textViewEnglish.setText(languageInfo.getTextEnglish());
            holder.textViewKorean.setText(languageInfo.getTextKorean());


        }
    }



    //ArrayList의 size(데이터 개수)를 return
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    //item view를 저장하는 ViewHolder 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewEnglish;
        TextView textViewKorean;



        //view 객체에 대한 참조
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewKorean = itemView.findViewById(R.id.textViewKorean);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    // notifyDataSetChanged()에 의해 리사이클러뷰가 아이템뷰를 갱신하는 과정에서,
                    // 뷰홀더가 참조하는 아이템이 어댑터에서 삭제되면 getAdapterPosition() 메서드는 NO_POSITION을 리턴하기 때문입니다.
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }
}
